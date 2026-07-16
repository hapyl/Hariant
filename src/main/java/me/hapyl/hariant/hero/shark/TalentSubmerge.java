package me.hapyl.hariant.hero.shark;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.block.display.DisplayModel;
import me.hapyl.eterna.module.component.Keybind;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.StreamRules;
import me.hapyl.hariant.entity.VanillaAttributeModifier;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class TalentSubmerge extends Talent {
    
    private final @DisplayField AttributeScaling damage = AttributeScaling.create(Map.of(
            AttributeType.ATTACK, 83.2,
            AttributeType.ELEMENTAL_MASTERY, 62.4
    ));
    
    private final @DisplayField Decimal elementalApplication = Decimal.ofElementalApplication(ElementType.WATER, 250);
    
    private final @DisplayField Decimal speed = Decimal.ofBlocksPerSecond(10);
    private final @DisplayField Decimal sneakingSpeedMultiplier = Decimal.ofPercentage(50);
    private final @DisplayField Decimal radius = Decimal.ofValue(0.8);
    private final @DisplayField Decimal damagePeriod = Decimal.ofSeconds(0.5f);
    private final @DisplayField Decimal knockbackStrength = Decimal.ofValue(0.6);
    
    private final DisplayModel model = BDEngine.parse(
            "/summon block_display ~-0.5 ~ ~-0.5 {Passengers:[{id:\"minecraft:item_display\",item:{id:\"minecraft:prismarine_shard\",Count:1},item_display:\"none\",transformation:[0f,0f,1f,0f,0.8660254038f,0.5f,0f,0.3125f,-0.5f,0.8660254038f,0f,0.0625f,0f,0f,0f,1f]}]}"
    );
    
    private final List<? extends VanillaAttributeModifier> vanillaModifiers = List.of(
            VanillaAttributeModifier.create(Key.ofString("submerge_scale"), Attribute.SCALE, VanillaAttributeModifier.Operation.FLAT, -100),
            VanillaAttributeModifier.create(Key.ofString("submerge_step_height"), Attribute.STEP_HEIGHT, VanillaAttributeModifier.Operation.FLAT, 0.5)
    );
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.create(
            this,
            DeathMessage.create("{player} was sharked to death by [{killer}]")
    );
    
    private final Key cooldownKey = Key.ofString("submerge_cooldown");
    
    public TalentSubmerge(@NotNull Key key) {
        super(key, Component.text("Submerge"), Icon.ofMaterial(Material.PRISMARINE_SHARD));
        
        setTalentType(TalentType.MOVEMENT);
        
        setDurationSeconds(1.2f);
        setCooldownSeconds(8);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Swiftly submerge underground and rush forward, revealing a hidden shark fin."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Hitting an "))
                         .append(Component.text("enemy", Colors.RED))
                         .append(Component.text(" with the fin deals "))
                         .appendNewline()
                         .append(ElementType.WATER.asComponentDamage())
                         .append(Component.text(", applies "))
                         .append(ElementType.WATER.asComponent())
                         .append(Component.text(" anomaly and knockbacks the enemy."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Hold ", Colors.DARK_GRAY))
                         .append(Keybind.SNEAK.asComponent().color(Colors.DARK_GRAY))
                         .append(Component.text(" to rush slower.", Colors.DARK_GRAY))
        );
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        player.delegate(new Submerge(player), DelegateType.PERSISTENT);
        
        // Fx
        player.playWorldSound(Sound.AMBIENT_UNDERWATER_ENTER, 3, 1.0f);
        
        return Response.ok();
    }
    
    public class Submerge extends HariantTickingTask implements KnockbackSource, EntityCollector {
        
        private final HariantPlayer player;
        private final DisplayEntity displayEntity;
        private final DamageSource damageSource;
        
        public Submerge(@NotNull HariantPlayer player) {
            super(Scheduler.ofTimer());
            
            this.player = player;
            this.displayEntity = model.spawnInterpolated(player.getLocation());
            this.damageSource = new SubmergeDamageSource(player, damage.getScaledValue(player));
            
            // Prepare player
            vanillaModifiers.forEach(player::addVanillaAttributeModifier);
            player.hide(StreamRules.NOT_SELF);
        }
        
        @Override
        public void run(int tick) {
            if (tick > getDuration()) {
                this.cancel();
                return;
            }
            
            // Rush forward
            final double speed = TalentSubmerge.this.speed.doubleValue() * (player.isSneaking() ? sneakingSpeedMultiplier.doubleValue() : 1.0);
            
            player.setVelocity(player.getLocation().getDirection().setY(0).normalize().multiply(speed).setY(-1));
            
            // Affect entities
            final Location location = player.getLocation().add(0, 0.3, 0);
            
            player.collectNearbyEntities(location, radius)
                  .filter(player::canAffect)
                  .forEach(entity -> {
                      entity.damage(damageSource);
                      entity.knockback(this);
                  });
            
            // Sync fin
            location.setYaw(location.getYaw() + (float) (Math.sin(Math.toRadians(tick * 20)) * 15));
            location.setPitch(0.0f);
            
            player.spawnWorldParticle(location, Particle.FALLING_WATER, 1 , 0.2, 0.2, 0.2, 0f);
            
            displayEntity.teleport(location);
        }
        
        @Override
        public void onCancel() {
            // Reset players
            vanillaModifiers.forEach(player::removeVanillaAttributeModifier);
            player.show(StreamRules.NOT_SELF);
            
            displayEntity.remove();
        }
        
        @Override
        public @NotNull Location getLocation() {
            return player.getLocation();
        }
        
        @Override
        public double x() {
            return player.x();
        }
        
        @Override
        public double z() {
            return player.z();
        }
        
        @Override
        public double strength() {
            return knockbackStrength.doubleValue();
        }
        
    }
    
    private class SubmergeDamageSource extends DamageSourceImpl {
        SubmergeDamageSource(@Nullable HariantEntity source, double damage) {
            super(damageSourceIdentity, source, DamageType.TALENT, ElementType.WATER, DamageComponent.ofCommon(), Set.of(), damage, elementalApplication.doubleValue(), cooldownKey, damagePeriod.intValue());
        }
    }
    
}
