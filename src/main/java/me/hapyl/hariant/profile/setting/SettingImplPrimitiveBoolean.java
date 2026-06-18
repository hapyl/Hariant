package me.hapyl.hariant.profile.setting;

import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.menu.Menu;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

public final class SettingImplPrimitiveBoolean extends SettingImplPrimitive<Boolean> {
    
    SettingImplPrimitiveBoolean(@NotNull Key key, @NotNull Component name, @NotNull Component description, @NotNull Icon icon, @NotNull SettingCategory category, boolean defaultValue) {
        super(key, name, description, icon, category, Boolean.class, defaultValue);
    }
    
    @Override
    public @NotNull ItemBuilder menuButton(@NotNull SettingEntry settingEntry) {
        final boolean enabled = settingEntry.getValue(this);
        
        return new ItemBuilder(enabled ? Material.LIME_DYE : Material.GRAY_DYE).setName(Component.text("Toggle Setting"));
    }
    
    @Override
    public void menuClick(@NotNull Player player, @NotNull SettingEntry settingEntry, @NotNull Menu menu, @NotNull ClickType clickType) {
        settingEntry.setValue(this, !settingEntry.getValue(this));
        menu.openMenu();
    }
    
    @Override
    public @NotNull ItemBuilder menuFormat(@NotNull SettingEntry settingEntry, @NotNull ItemBuilder builder) {
        final boolean enabled = settingEntry.getValue(this);
        
        builder.addLore()
               .addLore(
                       enabled
                       ? Component.text("This setting is currently enabled!", Colors.SUCCESS)
                       : Component.text("This setting is currently disabled!", Colors.ERROR)
               )
               .addLore()
               .addLore(ButtonComponents.left(enabled ? "disable" : "enable"));
        
        return builder;
    }
    
}