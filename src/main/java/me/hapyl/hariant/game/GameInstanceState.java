package me.hapyl.hariant.game;

/**
 * Represents a game instance state.
 */
public enum GameInstanceState {
    
    /**
     * Defines that the game instance exists but have not yet started.
     */
    PREPARING,
    
    /**
     * Defines that the game is currently in progress.
     */
    IN_PROGRESS,
    
    /**
     * Defines that the game has finished, but instance not yet destroyed.
     */
    POST_GAME,
    
    /**
     * Defines that the game has completely ended; no modifications to the instance in this state are permitted.
     */
    FINISHED
    
}
