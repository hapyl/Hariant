package me.hapyl.hariant.event;

import me.hapyl.hariant.inventory.drop.DropTable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HariantLootGenerationEvent extends HariantEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final DropTable dropTable;
    private int rollAmount;
    
    public HariantLootGenerationEvent(@NotNull DropTable dropTable, int rollAmount) {
        this.dropTable = dropTable;
        this.rollAmount = rollAmount;
    }
    
    public @NotNull DropTable getDropTable() {
        return dropTable;
    }
    
    public int getRollAmount() {
        return rollAmount;
    }
    
    public void setRollAmount(final int rollAmount) {
        this.rollAmount = Math.max(1, rollAmount);
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
