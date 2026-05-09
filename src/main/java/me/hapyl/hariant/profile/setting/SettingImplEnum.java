package me.hapyl.hariant.profile.setting;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Enums;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

public final class SettingImplEnum<E extends Enum<E>> extends SettingAbstractImpl<E> {
    
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
}
