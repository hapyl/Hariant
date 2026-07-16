package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class HariantTalentUltimateEvent extends HariantPlayerEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final TalentUltimate talent;
    private final double resourceConsumed;
    
    @ApiStatus.Internal
    public HariantTalentUltimateEvent(@NotNull HariantPlayer player, @NotNull TalentUltimate talent, double resourceConsumed) {
        super(player);
        
        this.talent = talent;
        this.resourceConsumed = resourceConsumed;
    }
    
    public @NotNull TalentUltimate getTalent() {
        return talent;
    }
    
    public @NotNull UltimateResourceType getResourceType() {
        return talent.getUltimateResourceType();
    }
    
    public double getResourceConsumed() {
        return resourceConsumed;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
    
}
