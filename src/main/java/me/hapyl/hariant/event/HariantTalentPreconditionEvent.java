package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Talent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HariantTalentPreconditionEvent extends HariantPlayerEvent implements CancellableWithReason {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final Talent talent;
    
    private @Nullable Cancel cancel;
    
    public HariantTalentPreconditionEvent(@NotNull HariantPlayer player, @NotNull Talent talent) {
        super(player);
        
        this.talent = talent;
    }
    
    @NotNull
    public Talent getTalent() {
        return talent;
    }
    
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    @Override
    public boolean isCancelled() {
        return cancel != null;
    }
    
    @Override
    public void setCancel(@NotNull Cancel cancel) {
        this.cancel = cancel;
    }
    
    public @Nullable Cancel getCancel() {
        return cancel;
    }
    
    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}