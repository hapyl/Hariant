package me.hapyl.hariant.entity.damage;

import me.hapyl.hariant.entity.damage.component.DamageComponentCritical;

public enum DamageFlag {
    
    /**
     * Whether the damage cannot deal lethal damage.
     */
    CANNOT_KILL,
    
    /**
     * Whether the damage ignores invulnerability frames.
     */
    IGNORES_INVULNERABILITY,
    
    /**
     * Whether the damage ignores shields.
     *
     * <p>
     * Note that some shields may ignore this flag.
     * </p>
     */
    PIERCING_DAMAGE,
    
    /**
     * Whether the damage should guarantee to be critical (only if {@link DamageComponentCritical} is present).
     */
    FORCE_CRITICAL,
    
    ;
    
}
