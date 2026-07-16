package me.hapyl.hariant.entity.trap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Trappable {
    
    boolean trap(@NotNull Trap trap);
    
    boolean untrap(@NotNull TrapEscape trapEscape);
    
    @Nullable Trap getTrap();
    
    boolean isTrapped();
    
}
