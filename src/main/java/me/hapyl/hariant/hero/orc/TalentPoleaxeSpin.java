package me.hapyl.hariant.hero.orc;

import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.VanillaAttributeModifier;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.Models;
import me.hapyl.hariant.util.decimal.Decimal;
import me.hapyl.hariant.util.field.DisplayField;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Set;

public final class TalentPoleaxeSpin extends TalentPoleaxe {
    
    static final Component TALENT_NAME = Component.text("Poleaxe Spin");
    
    private final @DisplayField AttributeScaling damage = AttributeScaling.create(AttributeType.ATTACK, 185);
    private final @DisplayField Decimal damagePeriod = Decimal.ofSeconds(0.5f);
    
    private final @DisplayField Decimal collisionRadius = Decimal.ofValue(0.7);
    private final @DisplayField Decimal knockbackStrength = Decimal.ofValue(0.5);
    
    private final @DisplayField Decimal numberOfRotations = Decimal.ofValue(3);
    private final @DisplayField Decimal rotationSpeed = Decimal.ofAngle(20);
    
    private final double rotationSpeedRadians = Math.toRadians(rotationSpeed.doubleValue());
    
    private final VanillaAttributeModifier vanillaAttributeModifier = VanillaAttributeModifier.create(
            Key.ofString("poleaxe_spin_fov"),
            Attribute.MOVEMENT_SPEED,
            VanillaAttributeModifier.Operation.MULTIPLICATIVE,
            -0.5
    );
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.createOfNamed(
            this, Key.ofString("poleaxe_spin_damage_source"),
            DeathMessage.create("{player} was killed [by {killer}'s] spinning Poleaxe")
    );
    
    private final Key cooldownKey = Key.ofString("poleaxe_spin_icd");
    
    public TalentPoleaxeSpin(@NotNull Key key) {
        super(key, TALENT_NAME, Icon.ofMaterial(Material.NETHERITE_AXE));
        
        setTalentType(TalentType.DAMAGE);
        
        setCooldownSeconds(10);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Spin your poleaxe around yourself."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Colliding with an "))
                         .append(Component.text("enemy", Colors.RED))
                         .append(Component.text(" deals "))
                         .appendNewline()
                         .append(ElementType.PHYSICAL.asComponentDamage())
                         .append(Component.text(" and knocks them back."))
                         .appendNewline()
                         .appendNewline()
                         .append(createCooldownComponent(TalentPoleaxeDash.TALENT_NAME))
        );
    }
    
    @Override
    public @NotNull TalentPoleaxe otherTalent() {
        return TalentRegistry.POLEAXE_DASH;
    }
    
    @Override
    public void execute(@NotNull HariantPlayer player) {
        player.delegate(new PoleaxeSpin(player), DelegateType.PERSISTENT);
    }
    
    public class PoleaxeSpin extends HariantTickingTask {
        
        private static final Vector3f ROTATION = new Vector3f(0, (float) Math.toRadians(90), 0);
        
        private final HariantPlayer player;
        private final DisplayEntity display;
        
        private final double thetaThreshold;
        private final double thetaOffset;
        
        private double theta;
        
        PoleaxeSpin(@NotNull HariantPlayer player) {
            super(Scheduler.ofTimer());
            
            this.player = player;
            this.display = Models.ORC_WEAPON.spawn(player.getLocation());
            this.display.setRotation(ROTATION);
            
            this.thetaThreshold = Math.PI * 2 * numberOfRotations.intValue();
            this.thetaOffset = -Math.toRadians(player.getYaw());
            
            // Add a FoV modifier
            this.player.addVanillaAttributeModifier(vanillaAttributeModifier);
            
            // Fx
            this.player.playWorldSound(Sound.ITEM_TRIDENT_RIPTIDE_3, 3, 0.0f);
        }
        
        @Override
        public void run(int tick) {
            if (this.theta > this.thetaThreshold) {
                this.cancel();
                return;
            }
            
            // Rotate poleaxe
            final Location location = player.getMidpointLocation();
            
            final double x = Math.sin(this.theta + this.thetaOffset) * 1.5;
            final double z = Math.cos(this.theta + this.thetaOffset) * 1.5;
            
            location.add(x, 0, z);
            
            // Check for collision
            player.collectNearbyEntities(location, collisionRadius)
                  .filter(player::canAffect)
                  .forEach(entity -> {
                      entity.damage(new PoleaxeSpinDamageSource(player));
                      
                      // Deal knockback unless resisted
                      if (!entity.hasEffectResistance(AssistSource.create(player, TalentPoleaxeSpin.this))) {
                          entity.knockback(KnockbackSource.create(player, knockbackStrength.doubleValue()));
                      }
                  });
            
            // Sync entity
            location.setDirection(player.getLocation().toVector().subtract(location.toVector()).normalize());
            location.setPitch(-90);
            
            this.display.teleport(location);
            
            // Increment theta
            this.theta += rotationSpeedRadians;
        }
        
        @Override
        public void onCancel() {
            this.display.remove();
            this.player.removeVanillaAttributeModifier(vanillaAttributeModifier);
        }
        
    }
    
    public class PoleaxeSpinDamageSource extends DamageSourceImpl {
        
        PoleaxeSpinDamageSource(@NotNull HariantEntity source) {
            super(damageSourceIdentity, source, DamageType.TALENT, ElementType.PHYSICAL, DamageComponents.ofCommon(), Set.of(), damage.getScaledValue(source), 0, cooldownKey, damagePeriod.intValue());
        }
        
    }
    
}