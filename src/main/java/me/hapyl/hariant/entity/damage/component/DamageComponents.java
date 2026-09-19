package me.hapyl.hariant.entity.damage.component;

import me.hapyl.hariant.attribute.instance.snapshot.AttributesSnapshot;
import me.hapyl.hariant.entity.damage.DamageInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public enum DamageComponents implements DamageComponent {
    
    DEFENSE(new DamageComponentDefense()),
    ELEMENTAL_RESISTANCE(new DamageComponentElementalResistance()),
    ELEMENTAL_DAMAGE_BONUS(new DamageComponentElementalDamageBonus()),
    CRITICAL(new DamageComponentCritical());
    
    private static final @Unmodifiable List<? extends DamageComponent> COMMON;
    private static final @Unmodifiable List<? extends DamageComponent> TRUE_DAMAGE;
    private static final @Unmodifiable List<? extends DamageComponent> ENVIRONMENT_DAMAGE;
    private static final @Unmodifiable List<? extends DamageComponent> ANOMALY;

    static {
        /* A list of commons components, which is used for most damage sources. */
        COMMON = List.of(DEFENSE, ELEMENTAL_RESISTANCE, ELEMENTAL_DAMAGE_BONUS, CRITICAL);
        
        /* A list of true damage components, which ignores DEF. */
        TRUE_DAMAGE = List.of(ELEMENTAL_RESISTANCE, ELEMENTAL_DAMAGE_BONUS, CRITICAL);
        
        /* A list of environment components, which only scales of DEF and Elemental RES. */
        ENVIRONMENT_DAMAGE = List.of(DEFENSE, ELEMENTAL_RESISTANCE);
        
        /* A list of anomaly components, which only scales of Elemental RES & DMG bonus. */
        ANOMALY = List.of(ELEMENTAL_RESISTANCE, ELEMENTAL_DAMAGE_BONUS);
    }
    
    private final DamageComponent damageComponent;
    
    DamageComponents(@NotNull DamageComponent damageComponent) {
        this.damageComponent = damageComponent;
    }
    
    @Override
    public @NotNull String identify() {
        return damageComponent.identify();
    }
    
    @Override
    public double multiplier(@NotNull DamageInstance damageInstance, @NotNull AttributesSnapshot entity, @NotNull AttributesSnapshot attacker) {
        return damageComponent.multiplier(damageInstance, entity, attacker);
    }
    
    public static @NotNull @Unmodifiable List<? extends DamageComponent> ofCommon() {
        return COMMON;
    }
    
    public static @NotNull @Unmodifiable List<? extends DamageComponent> ofTrueDamage() {
        return TRUE_DAMAGE;
    }
    
    public static @NotNull @Unmodifiable List<? extends DamageComponent> ofEnvironmentDamage() {
        return ENVIRONMENT_DAMAGE;
    }
    
    public static @NotNull @Unmodifiable List<? extends DamageComponent> ofAnomaly() {
        return ANOMALY;
    }
    
}