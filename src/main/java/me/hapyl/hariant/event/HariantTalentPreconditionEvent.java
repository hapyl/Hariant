package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Talent;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HariantTalentPreconditionEvent extends HariantPlayerEvent implements Cancellable {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final Talent talent;
    
    private Cancel cancel;
    
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
    @Deprecated(forRemoval = true)
    public void setCancelled(boolean cancel) {
        this.setCancelled(Cancel.INSTANCE);
    }
    
    public void setCancelled(@NotNull Cancel cancel) {
        this.cancel = cancel;
    }
    
    @Nullable
    public Cancel getCancel() {
        return cancel;
    }
    
    @NotNull
    public Component getCancelReason() {
        return cancel != null ? cancel.reason : Cancel.INSTANCE.reason;
    }
    
    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
    @NotNull
    public static Cancel cancel(@NotNull Component reason) {
        return new Cancel(reason);
    }
    
    public static final class Cancel {
        private static final Cancel INSTANCE = new Cancel(Component.text("Cannot execute talent right now!"));
        
        private final Component reason;
        
        Cancel(@NotNull Component reason) {
            this.reason = reason;
        }
        
        @NotNull
        public Component reason() {
            return reason;
        }
    }
    
}
