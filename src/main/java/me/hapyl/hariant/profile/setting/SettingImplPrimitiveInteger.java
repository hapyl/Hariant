package me.hapyl.hariant.profile.setting;

import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.sign.SignInput;
import me.hapyl.eterna.module.inventory.sign.SignResponse;
import me.hapyl.eterna.module.inventory.sign.SignType;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.menu.Menu;
import me.hapyl.hariant.task.InternalTasks;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

public final class SettingImplPrimitiveInteger extends SettingImplPrimitive<Integer> {
    
    private final int minValue;
    private final int maxValue;
    
    SettingImplPrimitiveInteger(
            @NotNull Key key,
            @NotNull Component name,
            @NotNull Component description,
            @NotNull Icon icon,
            @NotNull SettingCategory category,
            int defaultValue,
            int minValue,
            int maxValue
    ) {
        super(key, name, description, icon, category, Integer.class, defaultValue);
        
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    @Override
    public @NotNull ItemBuilder menuButton(@NotNull SettingEntry settingEntry) {
        return new ItemBuilder(Material.LIGHT_BLUE_DYE).setName(Component.text("Enter Number"));
    }
    
    @Override
    public void menuClick(@NotNull Player player, @NotNull SettingEntry settingEntry, @NotNull Menu menu, @NotNull ClickType clickType) {
        new SignInput(
                player,
                SignType.WARPED,
                SignInput.DASHED_LINE,
                "Enter Number",
                "(%s-%s)".formatted(minValue, maxValue)
        ) {
            @Override
            public void onResponse(@NotNull SignResponse response) {
                InternalTasks.now(() -> {
                    final int value = response.get(0).toInt();
                    
                    if (value < minValue) {
                        HariantLogger.error(player, Component.text("Value cannot be lower than %s!".formatted(minValue)));
                        return;
                    }
                    
                    if (value > maxValue) {
                        HariantLogger.error(player, Component.text("Value cannot be higher than %s!".formatted(maxValue)));
                        return;
                    }
                    
                    settingEntry.setValue(SettingImplPrimitiveInteger.this, value);
                    menu.openMenu();
                });
            }
        };
    }
    
    @NotNull
    @Override
    public ItemBuilder menuFormat(@NotNull SettingEntry settingEntry, @NotNull ItemBuilder builder) {
        final int value = settingEntry.getValue(this);
        
        builder.addLore()
               .addLore(Component.empty().append(Component.text("Current Value: ")).append(Component.text(value, Colors.NUMBER)))
               .addLore()
               .addLore(ButtonComponents.left("enter number"));
        
        return builder;
    }
    
}
