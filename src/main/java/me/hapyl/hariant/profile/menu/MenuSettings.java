package me.hapyl.hariant.profile.menu;

import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.menu.Menu;
import me.hapyl.hariant.menu.MenuReturn;
import me.hapyl.hariant.profile.setting.Setting;
import me.hapyl.hariant.profile.setting.SettingCategory;
import me.hapyl.hariant.profile.setting.SettingEntry;
import me.hapyl.hariant.profile.setting.Settings;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MenuSettings extends Menu {
    
    private static final int[][] SETTING_SLOTS = {
            { 19, 28 },
            { 20, 29 },
            { 21, 30 },
            { 22, 31 },
            { 23, 32 },
            { 24, 33 },
            { 25, 34 }
    };
    
    private static final ItemStack NO_SETTINGS_IN_CURRENT_CATEGORY = new ItemBuilder(Material.CAULDRON)
            .setName(Component.text("No Settings!"))
            .addLore()
            .addWrappedLore(Component.text("There are currently no settings in this category, try another!"))
            .asIcon();
    
    private SettingCategory category;
    
    public MenuSettings(@NotNull Player player) {
        super(player, () -> Component.text("Settings"), ChestSize.SIZE_6);
        
        this.category = SettingCategory.GAMEPLAY;
        this.openMenu();
    }
    
    @Override
    public @Nullable MenuReturn menuReturn() {
        return MenuReturn.create(Component.text("Your Profile"), () -> new MenuPlayerProfile(player));
    }
    
    @Override
    public void updateMenu() {
        final SettingEntry settingEntry = Hariant.getPlayerDatabase(player).settings;
        
        // Set the category
        for (SettingCategory settingCategory : SettingCategory.values()) {
            final int slot = settingCategory.getSlot();
            final boolean isCurrentCategory = category == settingCategory;
            
            final ItemBuilder builder = settingCategory.createBuilder();
            builder.addLore();
            
            if (isCurrentCategory) {
                builder.addLore(Component.text("Currently selected!", Colors.ERROR));
                
                setItem(
                        slot,
                        builder.asIcon(),
                        PlayerMenuAction.builder()
                                        .left(player -> {
                                            HariantLogger.error(player, Component.text("Already selected!"));
                                            HariantLogger.sound(player, Sound.ENTITY_VILLAGER_NO, 1.0f);
                                        })
                                        .build());
            }
            else {
                builder.addLore(ButtonComponents.left("select"));
                
                setItem(
                        slot,
                        builder.asIcon(),
                        PlayerMenuAction.builder()
                                        .left(player -> {
                                            category = settingCategory;
                                            this.openMenu();
                                        })
                                        .build()
                );
            }
        }
        
        // Display the settings under the category
        final List<Setting<?>> settings = Settings.listCategory(category);
        
        if (settings.isEmpty()) {
            setItem(22, NO_SETTINGS_IN_CURRENT_CATEGORY);
        }
        else {
            for (int i = 0; i < SETTING_SLOTS.length; i++) {
                if (i >= settings.size()) {
                    return;
                }
                
                final Setting<?> setting = settings.get(i);
                final int[] slots = SETTING_SLOTS[i];
                
                final ItemStack settingIcon = setting.menuFormat(settingEntry, setting.createBuilder()).asIcon();
                final ItemStack settingButton = setting.menuFormat(settingEntry, setting.menuButton(settingEntry)).asIcon();
                
                final PlayerMenuAction menuAction = (menu, player, clickType, slot, hotbarNumber) -> setting.menuClick(player, settingEntry, MenuSettings.this, clickType);
                
                setItem(slots[0], settingIcon, menuAction);
                setItem(slots[1], settingButton, menuAction);
            }
        }
    }
    
}