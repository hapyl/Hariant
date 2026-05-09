package me.hapyl.hariant.profile.setting;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

public final class SettingImpl<I> extends SettingAbstractImpl<I> {
    
    private final Class<I> valueClass;
    
    SettingImpl(
            @NotNull Key key,
            @NotNull Component name,
            @NotNull Component description,
            @NotNull Icon icon,
            @NotNull SettingCategory category,
            @NotNull Class<I> valueClass,
            @NotNull I defaultValue
    ) {
        if (valueClass != Boolean.class && valueClass != Integer.class) {
            throw new IllegalArgumentException("Setting can only be Boolean or Integer, not %s!".formatted(valueClass.getSimpleName()));
        }
        
        super(key, name, description, icon, category, defaultValue);
        
        this.valueClass = valueClass;
    }
    
    @NotNull
    @Override
    public I getValue(@NotNull Document document) {
        final I i = document.get(this.getKeyAsString(), valueClass);
        
        return i != null ? i : defaultValue();
    }
    
    @Override
    public void setValue(@NotNull Document document, @NotNull I value) {
        document.put(this.getKeyAsString(), value);
    }
    
}
