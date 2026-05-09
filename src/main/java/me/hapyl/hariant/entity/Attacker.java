package me.hapyl.hariant.entity;

import me.hapyl.hariant.weapon.NormalAttackRanged;
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
     * Gets the ranged {@link NormalAttackRanged}, which is optional and used for projectile scaling.
     *
     * @return the ranged attack of this entity.
     */
    @Nullable
    default NormalAttackRanged getRangedAttack() {
        return null;
    }
    
}
