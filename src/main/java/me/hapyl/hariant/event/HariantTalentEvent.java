package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HariantTalentEvent extends HariantPlayerEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final Talent talent;
    private final Response response;
    
    public HariantTalentEvent(@NotNull HariantPlayer player, @NotNull Talent talent, @NotNull Response response) {
        super(player);
        
        this.talent = talent;
        this.response = response;
    }
    
    @NotNull
    public Talent getTalent() {
        return talent;
    }
    
    @NotNull
    public Response getResponse() {
        return response;
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
