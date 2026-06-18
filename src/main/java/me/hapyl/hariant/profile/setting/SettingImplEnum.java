package me.hapyl.hariant.profile.setting;

import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Enums;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.menu.Menu;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

public final class SettingImplEnum<E extends Enum<E> & ComponentLike> extends SettingImpl<E> {
    
    private static final Style CONSTANT_STYLE = Style.style(Colors.DARK_GRAY);
    private static final Style CONSTANT_STYLE_CURRENT = Style.style(Colors.GRAY);
    
    private static final Component ARROW_PREFIX = Component.text(" ➥ ", Colors.GREEN);
    
    private final Class<E> enumClass;
    
    SettingImplEnum(
            @NotNull Key key,
            @NotNull Component name,
            @NotNull Component description,
            @NotNull Icon icon,
            @NotNull SettingCategory category,
            @NotNull E defaultValue
    ) {
        super(key, name, description, icon, category, defaultValue);
        
        this.enumClass = defaultValue.getDeclaringClass();
    }
    
    @NotNull
    @Override
    public E getValue(@NotNull Document document) {
        // We store as a lower-case enum name, so get the string and read from the enum class
        final String value = document.get(getKeyAsString(), "");
        final E enumValue = Enums.byName(enumClass, value);
        
        return enumValue != null ? enumValue : defaultValue;
    }
    
    @Override
    public void setValue(@NotNull Document document, @NotNull E value) {
        document.put(getKeyAsString(), value.name().toLowerCase());
    }
    
    @NotNull
    @Override
    public ItemBuilder menuButton(@NotNull SettingEntry settingEntry) {
        return new ItemBuilder(Material.PURPLE_DYE).setName(Component.text("Select Value"));
    }
    
    @Override
    public void menuClick(@NotNull Player player, @NotNull SettingEntry settingEntry, @NotNull Menu menu, @NotNull ClickType clickType) {
        final E value = settingEntry.getValue(this);
        final E newValue = clickType == ClickType.LEFT
                           ? Enums.getNextValue(enumClass, value)
                           : Enums.getPreviousValue(enumClass, value);
        
        settingEntry.setValue(this, newValue);
        menu.openMenu();
    }
    
    @NotNull
    @Override
    public ItemBuilder menuFormat(@NotNull SettingEntry settingEntry, @NotNull ItemBuilder builder) {
        final E value = settingEntry.getValue(this);
        
        builder.addLore();
        builder.addLore(Component.text("Available Values:", Colors.WHITE, TextDecoration.BOLD));
        
        for (final E enumConstant : enumClass.getEnumConstants()) {
            final boolean isCurrentValue = enumConstant == value;
            final Component component = enumConstant.asComponent();
            
            if (isCurrentValue) {
                builder.addLore(Component.empty().append(ARROW_PREFIX).append(component.style(CONSTANT_STYLE_CURRENT)));
            }
            else {
                builder.addLore(Component.empty().append(Component.text("    ")).append(component.style(CONSTANT_STYLE)));
            }
        }
        
        builder.addLore();
        builder.addLore(ButtonComponents.left("cycle"));
        builder.addLore(ButtonComponents.right("cycle backwards"));
        
        return builder;
    }
    
}