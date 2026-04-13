package me.hapyl.hariant.entity.damage;

import me.hapyl.eterna.module.annotate.Immutable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DamageFlagged {
    
    @NotNull
    @Immutable
    List<? extends DamageFlag> getDamageFlags();
    
    default boolean isFlagged(@NotNull DamageFlag damageFlag) {
        return getDamageFlags().contains(damageFlag);
    }
    
}
