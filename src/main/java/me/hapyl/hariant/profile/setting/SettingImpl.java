package me.hapyl.hariant.profile.setting;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

public class SettingImpl<I> implements Setting<I> {
    
    private final Key key;
    private final Component name;
    private final Component description;
    private final Icon icon;
    private final Class<I> valueClass;
    private final I defaultValue;
    
    private SettingImpl(
            @NotNull Key key,
            @NotNull Component name,
            @NotNull Component description,
            @NotNull Icon icon,
            @NotNull Class<I> valueClass,
            @NotNull I defaultValue
    ) {
        if (valueClass != Boolean.class && valueClass != Integer.class) {
            throw new IllegalArgumentException("Setting can only be Boolean or Integer, not %s!".formatted(valueClass.getSimpleName()));
        }
        
        this.key = key;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.valueClass = valueClass;
        this.defaultValue = defaultValue;
    }
    
    @NotNull
    @Override
    public Key getKey() {
        return key;
    }
    
    @Override
    @NotNull
    public Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public Component getDescription() {
        return description;
    }
    
    @Override
    @NotNull
    public ItemBuilder createBuilder() {
        return icon.createBuilder();
    }
    
    @NotNull
    @Override
    public I defaultValue() {
        return defaultValue;
    }
    
    @NotNull
    @Override
    public I getValue(@NotNull Document document) {
        final I i = document.get(this.getKeyAsString(), valueClass);
        
        return i != null ? i : defaultValue();
    }
    
    @NotNull
    public static Setting<Boolean> ofBoolean(@NotNull Key key, @NotNull Component name, @NotNull Component description, @NotNull Icon icon, boolean defaultValue) {
        return new SettingImpl<>(key, name, description, icon, Boolean.class, defaultValue);
    }
    
    @NotNull
    public static Setting<Integer> ofInteger(@NotNull Key key, @NotNull Component name, @NotNull Component description, @NotNull Icon icon, int defaultValue) {
        return new SettingImpl<>(key, name, description, icon, Integer.class, defaultValue);
    }
}
