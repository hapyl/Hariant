package me.hapyl.hariant.inventory.item;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.FlavorText;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractItem implements Keyed, Named, Described, FlavorText, ItemCreator {
    
    private final Key key;
    private final Component name;
    private final Icon icon;
    
    @NotNull private Component description;
    @NotNull private Component flavorText;
    
    public AbstractItem(@NotNull Key key, @NotNull Component name, @NotNull Icon icon) {
        this.key = key;
        this.name = name;
        this.icon = icon;
        this.description = Described.defaultValue();
        this.flavorText = Component.empty();
    }
    
    public abstract int maxStackSize();
    
    @NotNull
    @Override
    public ItemBuilder createBuilder() {
        final ItemBuilder builder = icon.createBuilder();
        builder.setName(name);
        builder.addLore();
        
        // Append description
        builder.addWrappedLore(description);
        
        return builder;
    }
    
    @NotNull
    @Override
    public Component getFlavorText() {
        return flavorText;
    }
    
    @Override
    public void setFlavorText(@NotNull Component flavorText) {
        this.flavorText = flavorText;
    }
    
    @NotNull
    @Override
    public final Key getKey() {
        return key;
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    public Icon getIcon() {
        return icon;
    }
    
    @NotNull
    @Override
    public Component getDescription() {
        return description;
    }
    
    @Override
    public void setDescription(@NotNull Component description) {
        this.description = description;
    }
    
    @Override
    public final int hashCode() {
        return Objects.hashCode(this.key);
    }
    
    @Override
    public final boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final AbstractItem that = (AbstractItem) object;
        return Objects.equals(this.key, that.key);
    }
}
