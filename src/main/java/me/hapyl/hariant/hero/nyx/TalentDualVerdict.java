package me.hapyl.hariant.hero.nyx;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.location.Located;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.math.geometry.Geometry;
import me.hapyl.eterna.module.reflect.glowing.Glowing;
import me.hapyl.eterna.module.reflect.team.PacketTeamColor;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.heal.HealingSource;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.task.HariantTickingStepTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public final class TalentDualVerdict extends Talent {
    
    @DisplayField private final Decimal numberOfDroplets = Decimal.ofValue(3);
    @DisplayField private final Decimal dropletRadius = Decimal.ofValue(0.75);
    
    @DisplayField private final Decimal dropletHealingOfMaxNyxHealth = Decimal.ofPercentage(10);
    @DisplayField private final Decimal dropletMaxHealthDecrease = Decimal.ofPercentage(10);
    @DisplayField private final Decimal dropletMaxHealthDecreaseDuration = Decimal.ofSeconds(8);
    
    @DisplayField private final Decimal initialRadius = Decimal.ofValue(3);
    @DisplayField private final Decimal maximumRadius = Decimal.ofValue(10);
    
    private final int numberOfFxOrbs = 6;
    private final int maximumSafeLocationAttempts = 10;
    private final int dropletNoPickupTick = 10;
    
    private final ItemStack dropletItem = ItemBuilder.playerHead("ed5d46bafb21727276d202ccd130f598a6956c79a4cf07a143f74c97b1be918c").asItemStack();
    
    public TalentDualVerdict(@NotNull Key key) {
        super(key, Component.text("Dual Verdict"), Icon.ofMaterial(Material.CHORUS_FRUIT));
        
        setTalentType(TalentType.SUPPORT);
        
        setDurationSeconds(1.5f);
        setCooldownSeconds(20);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Start channeling a chaos spell."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("After a short casting time, create "))
                         .append(Component.text("Chaos Droplets", Colors.CHAOS))
                         .append(Component.text(" that will "))
                         .append(Component.text("judge", Colors.GOLD))
                         .append(Component.text(" the first target that comes in contact with them."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("If the target is a "))
                         .append(Component.text("teammate", Colors.GREEN))
                         .append(Component.text(", the verdict is "))
                         .append(Component.text("Harmony", Colors.SUCCESS))
                         .append(Component.text(", which "))
                         .append(Component.text("heals", Colors.GREEN))
                         .append(Component.text(" them "))
                         .append(Component.text("for "))
                         .append(dropletHealingOfMaxNyxHealth)
                         .append(Component.text(" of Nyx's "))
                         .append(AttributeType.MAX_HEALTH)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("If the target is an "))
                         .append(Component.text("enemy", Colors.RED))
                         .append(Component.text(", the verdict is "))
                         .append(Component.text("Discord", Colors.ERROR))
                         .append(Component.text(", which decreases the target's "))
                         .append(AttributeType.MAX_HEALTH)
                         .append(Component.text(" by "))
                         .append(dropletMaxHealthDecrease)
                         .append(Component.text(" for "))
                         .append(dropletMaxHealthDecreaseDuration)
                         .append(Component.text("."))
        );
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        final HeroDataNyx heroData = player.getHeroData(HeroRegistry.NYX, HeroDataNyx::new);
        final Location location = player.getLocation();
        
        player.delegate(new DualVerdict(player, heroData, location), DelegateType.INTERRUPTABLE);
        return Response.ok();
    }
    
    private class DualVerdict extends HariantTickingStepTask {
        
        private final HariantPlayer player;
        private final HeroDataNyx heroData;
        private final Location location;
        private final List<Entity> entities;
        
        DualVerdict(@NotNull HariantPlayer player, @NotNull HeroDataNyx heroData, @NotNull Location location) {
            super(Scheduler.ofTimer(), 3);
            
            this.player = player;
            this.heroData = heroData;
            this.location = location;
            this.entities = Lists.newArrayList();
            
            // Create droplets
            for (int i = 0; i < numberOfFxOrbs; i++) {
                this.entities.add(createDroplet(location));
            }
        }
        
        @Override
        public void onCancel() {
            entities.forEach(Entity::remove);
            entities.clear();
        }
        
        @Override
        public boolean run(int tick, int step) {
            final int duration = getDuration();
            
            if (tick >= duration) {
                // Store locations of entities for fx later
                final List<? extends Location> entityLocations = entities.stream()
                                                                         .map(entity -> entity.getLocation().add(0, 1, 0))
                                                                         .toList();
                
                this.cancel();
                this.createDroplets();
                
                // Fx
                player.playWorldSound(location, Sound.ENTITY_WARDEN_DEATH, 0.75f);
                
                // Draw lines
                for (int i = 0; i < entityLocations.size(); i++) {
                    final Location from = entityLocations.get(i);
                    final Location to = entityLocations.get((i + 1) % entityLocations.size());
                    
                    Geometry.drawLine(from, to, 0.5, location -> HeroRegistry.NYX.spawnParticle(player, location));
                }
                
                return true;
            }
            
            final double progress = (double) tick / duration;
            final double radians = Math.toRadians(tick * 10);
            
            final double spread = Math.PI * 2 / Math.max(1, entities.size());
            final double radius = initialRadius.doubleValue() + (maximumRadius.doubleValue() - initialRadius.doubleValue()) * progress;
            
            int index = 0;
            
            for (Entity entity : entities) {
                final double x = Math.sin(radians + spread * index) * radius;
                final double y = Math.atan(Math.PI / 2 * radians) * 0.75 - 1.5;
                final double z = Math.cos(radians + spread * index) * radius;
                
                LocationHelper.offset(location, x, y, z, entity::teleport);
                
                index++;
            }
            
            // Sfx
            if (step == 0 && modulo(2)) {
                player.playWorldSound(location, Sound.ENTITY_ENDERMAN_HURT, (float) (0.5f + (0.75f * progress)));
            }
            
            return false;
        }
        
        private void createDroplets() {
            // Remove existing droplets
            heroData.removeDroplets();
            
            for (int i = 0; i < numberOfDroplets.intValue(); i++) {
                heroData.createDroplet(new Droplet(player, selectSafeDropletLocation()));
            }
        }
        
        private @NotNull Location selectSafeDropletLocation() {
            final double maximumDropletRadius = maximumRadius.doubleValue() - 1;
            int attempts = 0;
            
            do {
                final double x = player.random.nextSignedDouble(maximumDropletRadius);
                final double z = player.random.nextSignedDouble(maximumDropletRadius);
                
                final Location dropletLocation = LocationHelper.anchor(LocationHelper.copyOf(location).add(x, 0, z));
                
                if (dropletLocation.getBlock().getRelative(BlockFace.DOWN).isSolid()) {
                    return dropletLocation;
                }
            }
            while (attempts++ < maximumSafeLocationAttempts);
            
            // Default to origin location I guess
            return LocationHelper.copyOf(location).add(player.random.nextSignedDouble(1), player.random.nextSignedDouble(1), player.random.nextSignedDouble(1));
        }
    }
    
    private @NotNull Entity createDroplet(@NotNull Location location) {
        return location.getWorld().spawn(location, ArmorStand.class, self -> {
            self.setSmall(true);
            self.setInvisible(true);
            self.setMarker(true);
            self.getEquipment().setHelmet(dropletItem);
        });
    }
    
    public class Droplet implements Located, Removable, EntityCollector {
        
        private final HariantPlayer player;
        private final Location location;
        private final Entity droplet;
        private int tick;
        
        Droplet(@NotNull HariantPlayer player, @NotNull Location location) {
            this.player = player;
            this.location = location;
            this.droplet = createDroplet(location);
            
            player.getPlayerTeam().getPlayers().forEach(teammate -> {
                Glowing.setGlowing(teammate.getHandle(), droplet, PacketTeamColor.GREEN);
            });
        }
        
        public boolean tick() {
            // Ignore collision for first couple ticks
            if (tick++ < dropletNoPickupTick) {
                return false;
            }
            
            final HariantEntity entity = collectNearbyEntities(dropletRadius)
                    .min(Comparator.comparingDouble(_entity -> _entity.distanceToSquared(location)))
                    .orElse(null);
            
            if (entity != null) {
                // If the entity is a teammate, heal them
                if (player.isSelfOrTeammate(entity)) {
                    
                    // If the teammate is full health, ignore them
                    if (entity.isFullHealth()) {
                        return false;
                    }
                    
                    entity.heal(HealingSource.create(player.getMaxHealth() * dropletHealingOfMaxNyxHealth.doubleValue(), player));
                }
                // Otherwise damage nyx and decrement entity energy
                else {
                    // De-buff Max Health
                    entity.getAttributes().addModifier(new AttributeModifierDualVerdict(player));
                    
                    // If entity is a player, decrement energy
                    if (entity instanceof HariantPlayer playerEntity && playerEntity.getHero().getUltimateTalent().getUltimateResourceType() == UltimateResourceType.ENERGY) {
                        
                        // Fx to player
                        playerEntity.playSound(Sound.ENTITY_BEE_HURT, 0.5f);
                        playerEntity.getHandle().sendHurtAnimation(0);
                    }
                }
                
                return true;
            }
            
            // Animate droplet
            final double y = Math.sin(Math.toRadians(player.localTicks() * 5)) * 0.2;
            
            final Location dropletLocation = droplet.getLocation();
            dropletLocation.setY(location.y() + y);
            dropletLocation.setYaw(dropletLocation.getYaw() + 5);
            
            droplet.teleport(dropletLocation);
            
            player.spawnWorldParticle(dropletLocation.add(0, 1, 0), Particle.PORTAL, 1, 0.2, 0.2, 0.2, 0.015f);
            return false;
        }
        
        @Override
        public @NotNull Location getLocation() {
            return droplet.getLocation().add(0, 1, 0);
        }
        
        @Override
        public void remove() {
            droplet.remove();
        }
    }
    
    private class AttributeModifierDualVerdict extends AttributeModifier {
        AttributeModifierDualVerdict(@NotNull HariantPlayer player) {
            super(TalentDualVerdict.this, player, dropletMaxHealthDecreaseDuration.intValue());
            
            of(AttributeType.MAX_HEALTH, AttributeModifierType.ADDITIVE, -dropletMaxHealthDecrease.doubleValue());
        }
    }
    
}