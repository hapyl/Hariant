package me.hapyl.hariant.entity.player;

import org.jetbrains.annotations.NotNull;

public interface LifecyclePlayer {
    
    void onCreate(@NotNull HariantPlayer player);
    
    void onDestroy(@NotNull HariantPlayer player);
    
}
