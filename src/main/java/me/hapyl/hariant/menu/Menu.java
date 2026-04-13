package me.hapyl.hariant.menu;

import me.hapyl.eterna.module.inventory.ItemStacks;
import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.PlayerMenu;
import me.hapyl.eterna.module.inventory.menu.PlayerMenuTitle;
import me.hapyl.eterna.module.inventory.menu.PlayerMenuType;
import me.hapyl.hariant.HariantLogger;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Menu extends PlayerMenu {
    
    public Menu(@NotNull Player player, @NotNull PlayerMenuTitle title, @NotNull ChestSize chestSize) {
        super(player, title, PlayerMenuType.chest(chestSize));
    }
    
    public abstract void updateMenu();
    
    @Nullable
    public MenuReturn menuReturn() {
        return null;
    }
    
    @Override
    public final void onOpen() {
        this.fillRow(0, ItemStacks.blackBar());
        this.fillRow(this.getMenuHeight() - 1, ItemStacks.blackBar());
        
        final MenuReturn menuReturn = this.menuReturn();
        
        if (menuReturn != null) {
            this.setReturnButton(menuReturn.menuName(), menuReturn::menu);
        }
        
        this.setCloseButton();
        this.updateMenu();
    }
    
}
