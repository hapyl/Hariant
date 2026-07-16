package me.hapyl.hariant.menu;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.PlayerMenuTitle;
import me.hapyl.eterna.module.inventory.menu.PlayerPageMenu;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.eterna.module.inventory.menu.pattern.SlotPattern;
import me.hapyl.eterna.module.inventory.menu.pattern.SlotPatternApplier;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.LinkedList;
import java.util.List;

public abstract class MenuPage<T> extends Menu {
    
    private static final int NUMBER_OF_ITEMS_PER_PAGE = 28;
    
    private static final int SLOT_NO_CONTENTS = 22;
    private static final int SLOT_BUTTON_PREVIOUS = 51;
    private static final int SLOT_BUTTON_NEXT = 52;
    
    private LinkedList<? extends T> contents;
    private int currentPage;
    
    public MenuPage(@NotNull Player player, @NotNull PlayerMenuTitle title) {
        super(player, title, ChestSize.SIZE_6);
        
        this.contents = Lists.newLinkedList();
        this.currentPage = 1;
    }
    
    public void setContents(@NotNull List<? extends T> contents) {
        this.contents = new LinkedList<>(contents);
    }
    
    @NotNull
    public abstract ItemBuilder createBuilder(@NotNull T t);
    
    public abstract void onClick(@NotNull T t, @NotNull ClickType clickType);
    
    @Override
    public final void openMenu() {
        // Default to open the current page of the menu
        this.openMenu(this.currentPage);
    }
    
    public void openMenu(@Range(from = 1, to = Integer.MAX_VALUE) int page) {
        this.currentPage = page;
        super.openMenu();
    }
    
    public @NotNull ItemStack getItemNoContents() {
        return PlayerPageMenu.ITEM_EMPTY_CONTENTS;
    }
    
    @Override
    @OverridingMethodsMustInvokeSuper
    public void updateMenu() {
        if (contents.isEmpty()) {
            setItem(SLOT_NO_CONTENTS, getItemNoContents());
        }
        else {
            final SlotPatternApplier slotPatternApplier = newSlotPatternApplier(SlotPattern.INNER_LEFT_TO_RIGHT, ChestSize.SIZE_2, ChestSize.SIZE_5);
            
            final int startIndex = (currentPage - 1) * NUMBER_OF_ITEMS_PER_PAGE;
            final int endIndex = Math.min(startIndex + NUMBER_OF_ITEMS_PER_PAGE, contents.size());
            
            for (int i = startIndex; i < endIndex; i++) {
                final T content = contents.get(i);
                
                slotPatternApplier.add(
                        createBuilder(content).asIcon(),
                        (menu, player, clickType, slot, hotbarNumber) -> onClick(content, clickType)
                );
            }
            
            slotPatternApplier.apply();
        }
        
        // Set previous arrow
        if (currentPage > 1) {
            setItem(
                    SLOT_BUTTON_PREVIOUS,
                    PlayerPageMenu.ITEM_ARROW_PREVIOUS,
                    PlayerMenuAction.of(player -> this.openMenu(currentPage - 1))
            );
        }
        
        // Set next arrow
        if (currentPage < (int) Math.ceil((double) contents.size() / NUMBER_OF_ITEMS_PER_PAGE)) {
            setItem(
                    SLOT_BUTTON_NEXT,
                    PlayerPageMenu.ITEM_ARROW_NEXT,
                    PlayerMenuAction.of(player -> this.openMenu(currentPage + 1))
            );
        }
    }
    
}
