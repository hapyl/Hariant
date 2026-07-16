package me.hapyl.hariant.entity;

/**
 * Represents the affect result from one {@link HariantEntity} to another.
 */
public enum AffectResult {
    
    /**
     * Defines that this entity can be affected.
     */
    CAN_AFFECT,
    
    /**
     * Defines that this entity cannot be affected by another because it's the same entity.
     */
    CANNOT_AFFECT_SELF,
    
    /**
     * Defines that this entity cannot be affected by another because it's dead.
     *
     * <p>
     * For players, they're considered dead even if they're respawning.
     * </p>
     */
    CANNOT_AFFECT_DEAD,
    
    /**
     * Defines that this entity cannot be affected by another because they're in the same team.
     */
    CANNOT_AFFECT_TEAMMATE,
    
    /**
     * Defines that this entity cannot be affected by another because it's invisible and the other cannot see it.
     */
    CANNOT_AFFECT_INVISIBLE,
    
    /**
     * Defines that this entity cannot be affected by another because it has invulnerability frames.
     */
    CANNOT_AFFECT_INVULNERABLE
    
}
