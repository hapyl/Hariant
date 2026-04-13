package me.hapyl.hariant.entity.damage;

public enum DamageResult {
    
    /**
     * The entity has successfully taken the damage and lived.
     */
    OK,
    
    /**
     * The entity has successfully taken the damage and has died.
     */
    DEAD,
    
    /**
     * The entity was immune to the damage and did not any damage.
     */
    IMMUNE
    
}
