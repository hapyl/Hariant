package me.hapyl.hariant.profile.setting;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public abstract class SettingImpl<I> implements Setting<I> {
    
    protected final I defaultValue;
    
    private final Key key;
    private final Component name;
    private final Component description;
    private final Icon icon;
    private final SettingCategory category;
    
    SettingImpl(@NotNull Key key, @NotNull Component name, @NotNull Component description, @NotNull Icon icon, @NotNull SettingCategory category, @NotNull I defaultValue) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.defaultValue = defaultValue;
        this.category = category;
    }
    
    @Override
    @NotNull
    public Key getKey() {
        return key;
    }
    
    @Override
    @NotNull
    public Component getName() {
        return name;
    }
    
    @Override
    @NotNull
    public Component getDescription() {
        return description;
    }
    
    @Override
    @NotNull
    public ItemBuilder createBuilder() {
        final ItemBuilder builder = icon.createBuilder();
        
        builder.setName(name);
        builder.addLore();
        builder.addWrappedLore(description);
        
        return builder;
    }
    
    @NotNull
    @Override
    public SettingCategory getCategory() {
        return category;
    }
    
    @NotNull
    @Override
    public I defaultValue() {
        return defaultValue;
    }
    
}
