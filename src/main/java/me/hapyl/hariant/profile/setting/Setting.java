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
    I defaultValue();
    
    @NotNull
    I getValue(@NotNull Document document);
    
    default void setValue(@NotNull Document document, @NotNull I value) {
        document.put(this.getKeyAsString(), value);
    }
    
}
