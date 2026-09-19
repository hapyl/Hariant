package me.hapyl.hariant.hero.nyx;

import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.location.Coordinates;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.WarningType;
import me.hapyl.hariant.entity.cooldown.HariantCooldown;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.util.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.BoundingBoxBlueprint;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.Models;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class TalentWitherPath extends Talent {
    
    private final @DisplayField Decimal maximumDistance = Decimal.ofValue(20);
    
    private final @DisplayField Decimal maxHealthDecrease = Decimal.ofPercentage(10);
    private final @DisplayField Decimal maxHealthDecreaseDuration = Decimal.ofSeconds(8);
    
    private final @DisplayField AttributeScaling spikeDamage = AttributeScaling.create(AttributeType.ATTACK, 43);
    private final @DisplayField Decimal spikeElementalApplication = Decimal.ofElementalApplication(ElementType.AETHER, 250);
    private final @DisplayField Decimal spikeKnockbackStrength = Decimal.ofValue(0.8);
    
    private final @DisplayField Decimal roseBloomDelay = Decimal.ofSeconds(0.5f);
    
    private final @DisplayField BoundingBoxBlueprint spikeBoundingBox = BoundingBoxBlueprint.define(1, 2, 1);
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.createOfNamed(
            this,
            Key.ofString("wither_path_damage_source"),
            DeathMessage.create("{player} was spiked to death [by {killer}]")
    );
    
    private final HariantCooldown damageCooldown = HariantCooldown.ofSeconds(Key.ofString("wither_path_damage_cooldown"), 0.5f);
    
    private final Key modifierKey = Key.ofString("wither_path_modifier");
    
    public TalentWitherPath(@NotNull Key key) {
        super(key, Component.text("Wither Path"), Icon.ofMaterial(Material.WITHER_ROSE));
        
        setDurationSeconds(1.6f);
        setCooldownSeconds(8);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Launch a path of wither roses forward."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("After a short delay, the roses bloom into "))
                         .append(Component.text("spikes", Colors.VOID))
                         .append(Component.text(" that deal "))
                         .append(ElementType.AETHER.asComponentDamage())
                         .append(Component.text(", "))
                         .append(Component.text("impair", Colors.ARCHETYPE_HEXBANE))
                         .append(Component.text(" and knockback "))
                         .append(Component.text("enemies", Colors.RED))
                         .append(Component.text("."))
        );
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        player.delegate(new WitherPath(player), DelegateType.INTERRUPTABLE);
        return Response.ok();
    }
    
    private class WitherPath extends HariantTickingTask {
        
        private final HariantPlayer player;
        private final Location location;
        private final Vector direction;
        private final DamageSource damageSource;
        
        private double distance;
        
        WitherPath(@NotNull HariantPlayer player) {
            super(Scheduler.ofTimer());
            
            this.player = player;
            this.location = player.getLocation();
            this.direction = location.getDirection().normalize().setY(0);
            this.damageSource = new DamageSourceWitherPath(player, spikeDamage.getScaledValue(player));
        }
        
        @Override
        public void run(int tick) {
            final boolean playFx = modulo(3);
            
            for (int i = 0; i < 3; i++) {
                if (distance++ > maximumDistance.doubleValue()) {
                    this.cancel();
                    return;
                }
                
                final double x = direction.getX() * distance;
                final double y = direction.getY() * distance;
                final double z = direction.getZ() * distance;
                
                LocationHelper.offset(location, x, y, z, () -> {
                    player.delegate(new WitherSpike(player, location, damageSource, playFx), DelegateType.PERSISTENT);
                });
            }
        }
        
    }
    
    private class WitherSpike extends HariantTickingTask implements EntityCollector, Coordinates {
        
        private static final Color OUTLINE_COLOR = Color.fromRGB(91, 5, 171);
        private static final BlockData SPIKE_BLOCK_DATA = Material.OBSIDIAN.createBlockData();
        
        private final HariantPlayer player;
        private final Location location;
        
        private final DisplayEntity displayRose;
        private final DisplayEntity displaySpike;
        
        private final BoundingBox boundingBox;
        private final DamageSource damageSource;
        
        private final boolean playFx;
        
        WitherSpike(@NotNull HariantPlayer player, @NotNull Location location, @NotNull DamageSource damageSource, boolean playFx) {
            super(Scheduler.ofTimer());
            
            this.player = player;
            
            this.location = LocationHelper.anchor(LocationHelper.copyOf(location));
            this.location.add(player.random.nextDouble(), player.random.nextDouble() * 0.5, player.random.nextDouble());
            this.location.setYaw(location.getYaw() + player.random.nextFloat(160, 200));
            this.location.setPitch(player.random.nextFloat() * 15);
            
            this.displayRose = Models.WITHER_ROSE.spawn(this.location);
            this.displaySpike = Models.WITHER_SPIKE.spawn(LocationHelper.copyOf(this.location).subtract(0, 10, 0));
            
            this.boundingBox = spikeBoundingBox.create(this.location);
            this.damageSource = damageSource;
            this.playFx = playFx;
            
            // Play rose fx
            if (playFx) {
                player.playWorldSound(this.location, Sound.BLOCK_SWEET_BERRY_BUSH_PLACE, 0.5f);
            }
        }
        
        @Override
        public void run(int tick) {
            // Cleanup
            if (tick > getDuration()) {
                this.cancel();
                return;
            }
            
            // Bloom into a spike
            if (tick == roseBloomDelay.intValue()) {
                displaySpike.teleport(location);
                
                // Affect entities
                collectNearbyEntities(boundingBox)
                        .filter(player::canAffect)
                        .forEach(entity -> {
                            // Deal damage
                            entity.damage(damageSource);
                            entity.getAttributes().addModifierIfAbsent(new AttributeModifierWitherPath(player));
                            entity.knockback(KnockbackSource.create(this, spikeKnockbackStrength.doubleValue()));
                        });
                
                // Fx
                if (playFx) {
                    player.playWorldSound(location, Sound.ENTITY_EVOKER_FANGS_ATTACK, 0.75f);
                }
            }
            else if (tick < roseBloomDelay.intValue()) {
                // Display warning
                collectNearbyEntities(boundingBox)
                        .filter(player::canAffect)
                        .forEach(entity -> {
                            entity.showWarning(WarningType.WARNING, roseBloomDelay.intValue());
                        });
            }
            
        }
        
        @Override
        public void onCancel() {
            displayRose.remove();
            displaySpike.remove();
            
            // Fx
            player.spawnWorldParticle(location, Particle.BLOCK, 10, 0.5, 1, 0.5, 0.075f, SPIKE_BLOCK_DATA);
            player.playWorldSound(location, Sound.BLOCK_STONE_BREAK, 0.75f);
        }
        
        @Override
        public @NotNull Location getLocation() {
            return location;
        }
        
        @Override
        public @NotNull Color outlineColor() {
            return OUTLINE_COLOR;
        }
        
        @Override
        public double x() {
            return location.x();
        }
        
        @Override
        public double y() {
            return location.y();
        }
        
        @Override
        public double z() {
            return location.z();
        }
        
    }
    
    private class DamageSourceWitherPath extends DamageSourceImpl {
        DamageSourceWitherPath(@NotNull HariantPlayer player, double damage) {
            super(damageSourceIdentity, player, DamageType.TALENT, ElementType.AETHER, DamageComponents.ofCommon(), Set.of(), damage, spikeElementalApplication.doubleValue(), damageCooldown);
        }
    }
    
    private class AttributeModifierWitherPath extends AttributeModifier {
        AttributeModifierWitherPath(@NotNull HariantPlayer player) {
            super(modifierKey, TalentWitherPath.this, player, maxHealthDecreaseDuration.intValue());
            
            of(AttributeType.MAX_HEALTH, AttributeModifierType.ADDITIVE, -maxHealthDecrease.doubleValue());
        }
    }
    
}