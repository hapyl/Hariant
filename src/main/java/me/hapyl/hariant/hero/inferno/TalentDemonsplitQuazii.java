package me.hapyl.hariant.hero.inferno;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.entity.PacketAttributes;
import me.hapyl.eterna.module.entity.packet.PacketEntity;
import me.hapyl.eterna.module.entity.packet.PacketGuardian;
import me.hapyl.eterna.module.entity.packet.PacketSquid;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.heal.HealingSource;
import me.hapyl.hariant.entity.mutator.Decay;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.Race;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.util.Definition;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Supplier;

public final class TalentDemonsplitQuazii extends TalentDemonsplit {
    
    private final @DisplayField Decimal beamHeight = Decimal.ofValue(2);
    private final @DisplayField Decimal beamLength = Decimal.ofValue(5);
    private final @DisplayField Decimal beamGrowthDelay = Decimal.ofSeconds(2);
    private final @DisplayField Decimal beamRadius = Decimal.ofValue(0.6);
    
    private final @DisplayField Decimal decayWorthOfMaxHealth = Decimal.ofPercentage(20);
    private final @DisplayField Decimal decayDuration = Decimal.ofSeconds(6);
    
    private final @DisplayField Decimal healingPerEnemyHitWithBeamOfMaxHealth = Decimal.ofPercentage(20);
    
    public TalentDemonsplitQuazii(@NotNull Key key) {
        super(key, InfernoDemonType.QUAZII);
        
        setTalentType(TalentType.IMPAIR);
    }
    
    @Override
    public @NotNull InfernoDemonEntity newInstance(@NotNull HariantPlayer player, @NotNull InfernoDemonType demonType) {
        return new InfernoDemonEntityQuazii(player);
    }
    
    @NotNull
    @Override
    public Component describeAbility() {
        return Component.empty()
                        .append(Component.text("Creates a death beam that spins around the demon."))
                        .appendNewline()
                        .appendNewline()
                        .append(Component.text("Colliding with the beam applies "))
                        .appendNewline()
                        .append(Definition.DECAY)
                        .append(Component.text(" worth "))
                        .append(decayWorthOfMaxHealth)
                        .append(Component.text(" of target's "))
                        .appendNewline()
                        .append(AttributeType.MAX_HEALTH)
                        .append(Component.text(" for "))
                        .append(decayDuration)
                        .append(Component.text("."));
    }
    
    @NotNull
    @Override
    public Component describeReform() {
        return Component.empty()
                        .append(Component.text("Heal for "))
                        .append(healingPerEnemyHitWithBeamOfMaxHealth)
                        .append(Component.text(" of "))
                        .append(AttributeType.MAX_HEALTH)
                        .append(Component.text(" for each "))
                        .append(Component.text("enemy player", Colors.RED))
                        .append(Component.text(" hit with the beam."));
    }
    
    public class InfernoDemonEntityQuazii extends InfernoDemonEntity {
        
        private final QuaziiBeam quaziiBeam;
        
        public InfernoDemonEntityQuazii(@NotNull HariantPlayer player) {
            super(player, InfernoDemonType.QUAZII, TalentDemonsplitQuazii.this);
            
            this.quaziiBeam = new QuaziiBeam(player, player.getLocation());
        }
        
        @Override
        public void onForm(@NotNull HariantPlayer player, @NotNull HeroDataInferno data) {
            InfernoDemon.drawParticleBox(player, location -> player.spawnWorldParticle(location, Particle.SMOKE, 1, 0), 2.3);
        }
        
        @Override
        public void onReform(@NotNull HariantPlayer player, @NotNull HeroDataInferno data) {
            super.onReform(player, data);
            
            final long numberOfEntitiesHit = quaziiBeam.hitPlayers.size();
            
            final double maxHealth = player.getMaxHealth();
            final double healing = numberOfEntitiesHit * healingPerEnemyHitWithBeamOfMaxHealth.doubleValue() * maxHealth;
            
            if (healing > 0) {
                player.heal(HealingSource.create(healing, this.getName()));
                player.messageInfo(
                        Component.empty()
                                 .append(Race.DEMON.getPrefixStyled())
                                 .appendSpace()
                                 .append(getDemonType().getName())
                                 .append(Component.text(" healed for %.0f!".formatted(healing), Colors.GREEN))
                );
            }
        }
        
        @Override
        public boolean tick() {
            super.tick();
            
            quaziiBeam.tick();
            return true;
        }
        
        @Override
        public void onDestroy() {
            super.onDestroy();
            
            quaziiBeam.remove();
        }
    }
    
    public class QuaziiBeam implements Ticking, Removable {
        
        private final HariantPlayer player;
        private final PacketEntity[] entities;
        private final Set<HariantPlayer> hitPlayers;
        
        private int tick;
        
        QuaziiBeam(@NotNull HariantPlayer player, @NotNull Location location) {
            this.player = player;
            this.entities = new PacketEntity[beamHeight.intValue() * 2];
            this.hitPlayers = Sets.newHashSet();
            
            // Create packet entities
            for (int i = 0; i < entities.length; i++) {
                final PacketSquid squid = prepareEntity(() -> new PacketSquid(location));
                final PacketGuardian guardian = prepareEntity(() -> new PacketGuardian(location));
                
                guardian.setBeamTarget(squid);
                
                entities[i] = squid;
                entities[++i] = guardian;
            }
        }
        
        @Override
        public void remove() {
            for (PacketEntity entity : entities) {
                entity.dispose();
            }
        }
        
        @Override
        public void tick() {
            final Location location = player.getMidpointLocation();
            final double length = beamLength.doubleValue() * Math.min(1, tick / beamGrowthDelay.doubleValue());
            
            // Synchronize the beam with player
            for (int i = 0; i < entities.length; i++) {
                final PacketEntity entity = entities[i];
                final boolean raycast = i % 2 == 0;
                
                final double radius = raycast ? 1 : length;
                final double radians = Math.toRadians(tick * 5);
                
                final double x = Math.sin(radians) * radius;
                final double y = i * 0.3 - (raycast ? 0 : 0.1);
                final double z = Math.cos(radians) * radius;
                
                LocationHelper.offset(location, x, y, z, entity::teleport);
                
                // Ray-cast the collision detection
                if (raycast) {
                    raycast(entity.getLocation(), entities[i + 1].getLocation());
                }
            }
            
            tick++;
        }
        
        private void raycast(@NotNull Location from, @NotNull Location to) {
            final Vector vector = to.toVector().subtract(from.toVector()).normalize();
            final double distance = LocationHelper.distance(from, to);
            
            for (double d = 0; d < distance; d += 1) {
                final double x = vector.getX() * d;
                final double y = vector.getY() * d;
                final double z = vector.getZ() * d;
                
                LocationHelper.offset(from, x, y, z, () -> {
                    player.collectNearbyEntities(from, beamRadius)
                          .filter(player::canAffect)
                          .filter(HariantPlayer.class::isInstance)
                          .map(HariantPlayer.class::cast)
                          .forEach(entity -> {
                              final double maxHealth = entity.getMaxHealth();
                              final double decay = maxHealth * decayWorthOfMaxHealth.doubleValue();
                              
                              entity.addHealthMutator(Decay.create(decay, decayDuration));
                              
                              // Mark as hit with the beam
                              hitPlayers.add(entity);
                          });
                });
            }
        }
        
        @NotNull
        private static <E extends PacketEntity & PacketAttributes> E prepareEntity(@NotNull Supplier<E> supplier) {
            final E entity = supplier.get();
            
            entity.setVisible(false);
            entity.setCollision(false);
            entity.setAttribute(Attributes.SCALE, 0.01);
            
            // Show the entity right away
            entity.showAll();
            
            return entity;
        }
        
    }
    
}
