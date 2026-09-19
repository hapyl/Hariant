package me.hapyl.hariant.hero.pytaria;

import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.component.Keybind;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.math.PiHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.util.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.HariantDurationTask;
import me.hapyl.hariant.term.EnumTerminology;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.Models;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class TalentFlowerEscape extends Talent {
    
    private final @DisplayField AttributeScaling flowerPulseDamage = AttributeScaling.create(AttributeType.ATTACK, 105);
    private final @DisplayField AttributeScaling flowerExplosionDamage = AttributeScaling.create(AttributeType.ATTACK, 185);
    
    private final @DisplayField Decimal flowerPulsePeriod = Decimal.ofSeconds(1f);
    private final @DisplayField Decimal flowerRadius = Decimal.ofValue(2.5);
    private final @DisplayField Decimal escapeMagnitude = Decimal.ofValue(-1.25);
    private final @DisplayField Decimal escapeY = Decimal.ofValue(0.5);
    
    private final @DisplayField Decimal elementalApplicationPulse = Decimal.ofElementalApplication(ElementType.PHYSICAL, 200); // 150
    private final @DisplayField Decimal elementalApplicationExplosion = Decimal.ofElementalApplication(ElementType.PHYSICAL, 250); // 250
    
    private final Color tulipColor = Color.fromRGB(221, 0, 14);
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.createOfNamed(
            this,
            Key.ofString("flower_escape_damage_source"),
            DeathMessage.create("{player} could not escape [{killer}'s grasp]")
    );
    
    public TalentFlowerEscape(@NotNull Key key) {
        super(key, Component.text("Flower Escape"), Icon.ofMaterial(Material.RED_TULIP));
        
        this.setCooldownSeconds(12);
        this.setDurationSeconds(6);
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Throw a dealy "))
                         .append(Component.text("flower", Colors.LIGHT_PURPLE))
                         .append(Component.text(" at your current location and dash backwards."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("The "))
                         .append(Component.text("flower", Colors.LIGHT_PURPLE))
                         .append(Component.text(" continuously pulses, dealing "))
                         .append(ElementType.PHYSICAL.asComponentDamage())
                         .append(Component.text(" in small "))
                         .append(EnumTerminology.AREA_OF_EFFECT)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("After "))
                         .append(this.getDurationFormatted())
                         .append(Component.text(", the "))
                         .append(Component.text("flower", Colors.LIGHT_PURPLE))
                         .append(Component.text(" explodes violently, dealing greater "))
                         .append(ElementType.PHYSICAL.asComponentDamage())
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Hold ", Colors.DARK_GRAY))
                         .append(Component.keybind(Keybind.SNEAK, Colors.DARK_GRAY))
                         .append(Component.text(" to dash forward instead.", Colors.DARK_GRAY))
        );
    }
    
    @NotNull
    @Override
    public TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @NotNull
    @Override
    public Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        player.delegate(new FlowerEscape(player), DelegateType.PERSISTENT);
        return Response.ok();
    }
    
    class FlowerEscapeDamageSource extends DamageSourceImpl {
        FlowerEscapeDamageSource(@NotNull HariantEntity source, boolean isPulse) {
            super(
                    damageSourceIdentity,
                    source,
                    DamageType.TALENT,
                    ElementType.PHYSICAL,
                    DamageComponents.ofCommon(),
                    Set.of(),
                    isPulse ? flowerPulseDamage.getScaledValue(source) : flowerExplosionDamage.getScaledValue(source),
                    isPulse ? elementalApplicationPulse.doubleValue() : elementalApplicationExplosion.doubleValue()
            );
        }
    }
    
    private class FlowerEscape extends HariantDurationTask {
        
        private final HariantPlayer player;
        private final Location location;
        private final DisplayEntity flower;
        
        private final DamageSource damageSourceExplosion;
        private final DamageSource damageSourcePulse;
        
        private final double flowerRadiusAsDouble;
        
        FlowerEscape(@NotNull HariantPlayer player) {
            super(TalentFlowerEscape.this);
            
            this.player = player;
            this.location = player.getLocation().add(0, 0.25, 0);
            this.location.setYaw(0.0f);
            this.location.setPitch(0.0f);
            this.flower = Models.FLOWER_ESCAPE.spawn(location);
            this.damageSourcePulse = new FlowerEscapeDamageSource(player, true);
            this.damageSourceExplosion = new FlowerEscapeDamageSource(player, false);
            this.flowerRadiusAsDouble = flowerRadius.doubleValue();
            
            // Apply velocity
            final Vector vector = player.getDirection().normalize().multiply(escapeMagnitude.doubleValue());
            
            // Inverse if player is sneaking
            if (player.isSneaking()) {
                vector.multiply(-1);
            }
            
            vector.setY(escapeY.doubleValue());
            
            player.setVelocity(vector);
        }
        
        @Override
        public void run(int tick, int duration) {
            // Deal damage at periods
            if (modulo(flowerPulsePeriod.intValue())) {
                final boolean lastTick = tick == duration - 1;
                
                dealDamage(lastTick ? damageSourceExplosion : damageSourcePulse);
            }
            
            // Fx
            final double flowerY = Math.sin(Math.toRadians(tick) * 6) * 0.2;
            
            location.setYaw(location.getYaw() + 5);
            
            LocationHelper.offset(location, 0, flowerY, 0, flower::teleport);
            
            for (double d = 0; d < PiHelper.TWO_PI + 0.1; d += Math.PI * 0.2) {
                final double radians = Math.toRadians(tick);
                
                final double x = Math.sin(d + radians * 2) * flowerRadiusAsDouble;
                final double y = 0.25;
                final double z = Math.cos(d + radians * 2) * flowerRadiusAsDouble;
                
                LocationHelper.offset(location, x, y, z, () -> {
                    player.spawnWorldParticle(location, Particle.TINTED_LEAVES, 1, 0.05, 0.05, 0.05, 0.1f, tulipColor);
                });
            }
            
        }
        
        @Override
        public void onCancel() {
            flower.remove();
        }
        
        @Override
        public void onLastTick() {
            // Fx
            player.playWorldSound(location, Sound.ITEM_TOTEM_USE, 2.0f);
            
            player.spawnParticle(location, Particle.EXPLOSION, 5, 1, 0.5, 1, 1);
        }
        
        private void dealDamage(@NotNull DamageSource damageSource) {
            // Deal damage
            player.collectNearbyEntities(location, flowerRadius)
                  .filter(player::canAffect)
                  .forEach(entity -> entity.damage(damageSource));
            
            // Fx
            final float pitch = 0.5f + (1.25f * (float) progress());
            
            player.playWorldSound(location, Sound.BLOCK_AZALEA_BREAK, pitch);
            player.playWorldSound(location, Sound.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, pitch);
            player.playWorldSound(location, Sound.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH, pitch);
            
            // Randomly decrement the location for SWEEP particle
            for (int i = 0; i < 10; ++i) {
                final double randomX = player.getRandom().nextSignedDouble(flowerRadiusAsDouble);
                final double randomZ = player.getRandom().nextSignedDouble(flowerRadiusAsDouble);
                
                LocationHelper.offset(location, randomX, 0, randomZ, () -> player.spawnWorldParticle(location, Particle.SWEEP_ATTACK, 0, 5, 5, 5, 1));
            }
        }
        
    }
}
