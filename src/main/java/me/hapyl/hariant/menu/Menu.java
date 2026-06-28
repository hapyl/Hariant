package me.hapyl.hariant.menu;

import me.hapyl.eterna.module.inventory.ItemStacks;
import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.PlayerMenu;
import me.hapyl.eterna.module.inventory.menu.PlayerMenuTitle;
import me.hapyl.eterna.module.inventory.menu.PlayerMenuType;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Menu extends PlayerMenu {
    
    public final PlayerProfile playerProfile;
    
    public Menu(@NotNull Player player, @NotNull PlayerMenuTitle title, @NotNull ChestSize chestSize) {
        if (chestSize == ChestSize.SIZE_1 || chestSize == ChestSize.SIZE_2) {
            throw new IllegalArgumentException("Sizes %s and %s are not supported!".formatted(ChestSize.SIZE_1, ChestSize.SIZE_2));
        }
        
        super(player, title, PlayerMenuType.chest(chestSize));
        this.playerProfile = Hariant.getPlayerProfile(player);
    }
    
    public abstract void updateMenu();
    
    @Nullable
    public MenuReturn menuReturn() {
        return null;
    }
    
    public void setHeader(int slot, @NotNull ItemStack itemStack, @Nullable PlayerMenuAction action) {
        setHeaderOrFooter(slot, true, itemStack, action);
    }
    
    public void setHeader(int slot, @NotNull ItemStack itemStack) {
        this.setHeader(slot, itemStack, null);
    }
    
    public void setFooter(int slot, @NotNull ItemStack itemStack, @Nullable PlayerMenuAction action) {
        setHeaderOrFooter(slot, false, itemStack, action);
    }
    
    public void setFooter(int slot, @NotNull ItemStack itemStack) {
        this.setFooter(slot, itemStack, null);
    }
    
    @Override
    public int getReturnButtonSlot() {
        return getMenuSize() - 8;
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
    
    @Override
    public void openMenu() {
        if (Hariant.entityExists(player.getUniqueId())) {
            HariantLogger.error(player, Component.text("Menus cannot be opened in current state!"));
            return;
        }
        
        super.openMenu();
    }
    
    private void setHeaderOrFooter(int slot, boolean header, @NotNull ItemStack itemStack, @Nullable PlayerMenuAction action) {
        final int actualSlot = header
                               ? Math.clamp(slot, 0, 8)
                               : Math.clamp(firstFooterSlot() + slot, firstFooterSlot(), getMenuSize() - 1);
        
        setItem0(actualSlot, itemStack, action);
    }
    
    private int firstFooterSlot() {
        return getMenuSize() - 9;
    }
    
}
