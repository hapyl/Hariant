package me.hapyl.hariant.entity.damage;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.text.Capitalizable;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@FunctionalInterface
public interface EnvironmentDamageSupplier {
    
    @NotNull
    EnvironmentDamage supply(@NotNull HariantEntity entity);
    
    @NotNull
    static Map.Entry<org.bukkit.damage.DamageType, EnvironmentDamageSupplier> entry(
            @NotNull org.bukkit.damage.DamageType damageType,
            @NotNull ElementType elementType,
            @NotNull DeathMessage deathMessage,
            double damage
    ) {
        final String key = damageType.getKey().getKey();
        final Component damageTypeName = Component.text(Capitalizable.capitalize(key.replace("_", " ")));
        
        return Map.entry(
                damageType,
                entity -> new EnvironmentDamage(
                        DamageSourceIdentity.create(
                                // This should never throw since vanilla damage keys are always in Eterna key format
                                Key.ofString(key),
                                damageTypeName,
                                deathMessage
                        ),
                        elementType,
                        damage
                )
        );
    }
    
    @NotNull
    static EnvironmentDamageSupplier fallDamage() {
        return new EnvironmentDamageSupplier() {
            private static final double BASE_FALL_DAMAGE = 100;
            private static final double FALL_DAMAGE_EXPONENT = 0.25;
            
            private static final DamageSourceIdentity DAMAGE_SOURCE_IDENTITY = DamageSourceIdentity.create(
                    Key.ofString("fall"),
                    Component.text("Fall"),
                    DeathMessage.createWithDefaultKiller("{player} fell to their death")
            );
            
            @NotNull
            @Override
            public EnvironmentDamage supply(@NotNull HariantEntity entity) {
                final double fallDamage = this.calculateFallDamage(entity);
                
                return new EnvironmentDamage(DAMAGE_SOURCE_IDENTITY, ElementType.PHYSICAL, fallDamage);
            }
            
            public double calculateFallDamage(@NotNull HariantEntity entity) {
                final float fallDistance = entity.getHandle().getFallDistance();
                
                return BASE_FALL_DAMAGE * Math.pow(Math.max(0, fallDistance - HariantConstants.FALL_DAMAGE_SAFE_FALL_DISTANCE), FALL_DAMAGE_EXPONENT);
            }
        };
    }
    
}
