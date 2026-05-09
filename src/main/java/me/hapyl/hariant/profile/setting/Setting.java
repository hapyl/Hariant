package me.hapyl.hariant.profile.setting;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

public interface Setting<I> extends Keyed, Named, Described, Icon {
    
    @NotNull
    @Override
    Key getKey();
    
    @Override
    @NotNull
    Component getName();
    
    @NotNull
    @Override
    Component getDescription();
    
    @Override
    @NotNull
    ItemBuilder createBuilder();
    
    @NotNull
    SettingCategory getCategory();
    
    @NotNull
    I defaultValue();
    
    @NotNull
    I getValue(@NotNull Document document);
    
    void setValue(@NotNull Document document, @NotNull I value);
    
    @NotNull
    static Setting<Boolean> ofBoolean(
            @NotNull Key key,
            @NotNull Component name,
            @NotNull Component description,
            @NotNull Icon icon,
            @NotNull SettingCategory category,
            boolean defaultValue
    ) {
        return new SettingImpl<>(key, name, description, icon, category, Boolean.class, defaultValue);
    }
    
    @NotNull
    static Setting<Integer> ofInteger(
            @NotNull Key key,
            @NotNull Component name,
            @NotNull Component description,
            @NotNull Icon icon,
            @NotNull SettingCategory category,
            int defaultValue
    ) {
        return new SettingImpl<>(key, name, description, icon, category, Integer.class, defaultValue);
    }
    
    @NotNull
    static <E extends Enum<E>> Setting<E> ofEnum(
            @NotNull Key key,
            @NotNull Component name,
            @NotNull Component description,
            @NotNull Icon icon,
            @NotNull SettingCategory category,
            @NotNull E defaultValue
    ) {
        return new SettingImplEnum<>(key, name, description, icon, category, defaultValue);
    }
    
}
