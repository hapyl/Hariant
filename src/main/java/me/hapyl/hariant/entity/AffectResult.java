package me.hapyl.hariant.entity;

import me.hapyl.hariant.entity.player.HariantPlayer;

/**
 * Represents the affect result from one {@link HariantEntity} to another.
 */
public enum AffectResult {
    
    /**
     * Defines that this {@link HariantEntity} can affect the other.
     */
    CAN_AFFECT,
    
    /**
     * Defines that this {@link HariantEntity} cannot affect the other because it is the same entity.
     */
    CANNOT_AFFECT_SELF,
    
    /**
     * Defines that this {@link HariantEntity} cannot affect the other because the entity has died.
     *
     * <p>
     * The dead check differs for {@link HariantPlayer}, because it's managed by their {@link PlayerState}, rather than
     * checking for underlying entity death.
     * </p>
     */
    CANNOT_AFFECT_DEAD,
    
    /**
     * Defines that this {@link HariantEntity} cannot affect the other because they're in the same team.
     */
    CANNOT_AFFECT_TEAMMATE,
    
    /**
     * Defines that this {@link HariantEntity} cannot affect the other because it's invisible and cannot be seen by this entity.
     */
    CANNOT_AFFECT_INVISIBLE,
    
    /**
     * Defines that this {@link HariantEntity} cannot affect the other because it's a {@link HariantMarkerEntity}.
     */
    CANNOT_AFFECT_MARKER
    
}
