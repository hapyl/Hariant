package me.hapyl.hariant.event;

import me.hapyl.hariant.game.GameInstance;
import me.hapyl.hariant.game.GameInstanceImpl;
import me.hapyl.hariant.game.GameInstanceState;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HariantGameInstanceStateEvent extends HariantEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final GameInstance gameInstance;
    private final GameInstanceState state;
    
    public HariantGameInstanceStateEvent(@NotNull GameInstance gameInstance, @NotNull GameInstanceState state) {
        this.gameInstance = gameInstance;
        this.state = state;
    }
    
    public @NotNull GameInstance getGameInstance() {
        return gameInstance;
    }
    
    public @NotNull GameInstanceState getState() {
        return state;
    }
    
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
