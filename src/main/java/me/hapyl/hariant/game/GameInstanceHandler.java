package me.hapyl.hariant.game;

import org.jetbrains.annotations.NotNull;

public interface GameInstanceHandler {
    
    void handleInstanceCreated(@NotNull GameInstance gameInstance);
    
    void handleInstanceDestroyed(@NotNull GameInstance gameInstance, @NotNull WinResult winResult);
    
}
