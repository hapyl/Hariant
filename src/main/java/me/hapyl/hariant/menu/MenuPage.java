package me.hapyl.hariant.menu;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.PlayerMenuTitle;
import me.hapyl.eterna.module.inventory.menu.pattern.SlotPattern;
import me.hapyl.eterna.module.inventory.menu.pattern.SlotPatternApplier;
import me.hapyl.hariant.Colors;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.LinkedList;
import java.util.List;

public abstract class MenuPage<T> extends Menu {
    
    private static final int NO_CONTENTS_SLOT = 22;
    
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
    @OverridingMethodsMustInvokeSuper
    public void updateMenu() {
        if (contents.isEmpty()) {
            setItem(
                    NO_CONTENTS_SLOT,
                    new ItemBuilder(Material.STRUCTURE_VOID)
                            .setName(Component.text("Nothing to Show!", Colors.ERROR))
                            .asIcon()
            );
        }
        else {
            final SlotPatternApplier slotPatternApplier = newSlotPatternApplier(SlotPattern.INNER_LEFT_TO_RIGHT, ChestSize.SIZE_2, ChestSize.SIZE_5);
            
            for (T content : contents) {
                slotPatternApplier.add(
                        createBuilder(content).asIcon(),
                        (menu, player, clickType, slot, hotbarNumber) -> onClick(content, clickType)
                );
            }
            
            slotPatternApplier.apply();
            
            // FIXME (xanyjl @ Friday, May 29) -> Cannot switch pages
            
            // Set arrows
        }
    }
    
}
