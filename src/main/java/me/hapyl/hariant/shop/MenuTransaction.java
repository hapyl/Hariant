package me.hapyl.hariant.shop;

import me.hapyl.eterna.module.inventory.menu.PlayerMenu;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.shop.transaction.Transaction;
import me.hapyl.hariant.shop.transaction.TransactionException;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

public abstract class MenuTransaction extends ShopTransaction implements PlayerMenuAction {
    
    @Override
    public final void use(@NotNull PlayerMenu menu, @NotNull Player player, @NotNull ClickType clickType, int slot, int hotbarNumber) {
        if (super.closed()) {
            menu.closeMenu();
            return;
        }
        
        super.process(player);
    }
    
    @Override
    public abstract @NotNull Transaction payment();
    
    @Override
    public abstract void deliver(@NotNull Player player, @NotNull PlayerDatabase playerDatabase) throws TransactionException;
    
}