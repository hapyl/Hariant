package me.hapyl.hariant.entity.damage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public interface DamageFlagged {
    
    @NotNull
    @Unmodifiable
    Set<? extends DamageFlag> getDamageFlags();
    
    default boolean isFlagged(@NotNull DamageFlag damageFlag) {
        return getDamageFlags().contains(damageFlag);
    }
    
}
