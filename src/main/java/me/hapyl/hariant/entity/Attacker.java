package me.hapyl.hariant.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an entity that has {@link NormalAttack}.
 */
public interface Attacker {
    
    /**
     * Gets the melee {@link NormalAttack}, must always exist.
     *
     * @return the melee attack of this entity.
     */
    @NotNull
    NormalAttack getMeleeAttack();
    
    
    /**
     * Gets the ranged {@link NormalAttack}, which is optional and used for projectile scaling.
     *
     * @return the ranged attack of this entity.
     */
    @Nullable
    default NormalAttack getRangedAttack() {
        return null;
    }
    
}
