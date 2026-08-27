package me.hapyl.hariant.profile.setting;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.menu.Menu;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface Setting<I> extends Keyed, Named, Described, Icon {
    
    @Override
    @NotNull Key getKey();
    
    @Override
    @NotNull Component getName();
    
    @Override
    @NotNull Component getDescription();
    
    @Override
    @NotNull ItemBuilder createBuilder();
    
    @NotNull SettingCategory getCategory();
    
    @NotNull I defaultValue();
    
    @NotNull I getValue(@NotNull Document document);
    
    void setValue(@NotNull Document document, @NotNull I value);
    
    @NotNull ItemBuilder createButton(@NotNull SettingEntry settingEntry);
    
    @NotNull ItemBuilder menuFormat(@NotNull SettingEntry settingEntry, @NotNull ItemBuilder builder);
    
    void menuClick(@NotNull Player player, @NotNull SettingEntry settingEntry, @NotNull Menu menu, @NotNull ClickType clickType);
    
    default void setMenuItem(@NotNull Menu menu, int slot1, int slot2, @NotNull SettingEntry settingEntry) {
        final ItemStack settingIcon = this.menuFormat(settingEntry, this.createBuilder()).asIcon();
        final ItemStack settingButton = this.menuFormat(settingEntry, this.createButton(settingEntry)).asIcon();
        
        final PlayerMenuAction menuAction = (_, player, clickType, _, _) -> this.menuClick(player, settingEntry, menu, clickType);
        
        menu.setItem(slot1, settingIcon, menuAction);
        menu.setItem(slot2, settingButton, menuAction);
    }
    
}
