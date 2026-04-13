package me.hapyl.hariant.game;

import org.jetbrains.annotations.NotNull;

public interface GameInstanceHandler {
    
    void handleInstanceCreated(@NotNull GameInstance gameInstance);
    
    void handlerInstanceDestroyed(@NotNull GameInstance gameInstance);
    
}
