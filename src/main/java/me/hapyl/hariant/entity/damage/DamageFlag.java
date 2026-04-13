package me.hapyl.hariant.entity.damage;

public enum DamageFlag {
    
    /**
     * Whether the damage cannot deal lethal damage.
     */
    CANNOT_KILL,
    
    /**
     * Whether the damage ignores invulnerability frames and does not apply attack cooldown.
     */
    IGNORES_INTERNAL_COOLDOWN,
    
    /**
     * Whether the damage ignores shields.
     */
    PIERCING_DAMAGE,
    
    ;
    
    
}
