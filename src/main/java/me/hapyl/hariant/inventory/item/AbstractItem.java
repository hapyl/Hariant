package me.hapyl.hariant.inventory.item;

import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.registry.Registrable;
import me.hapyl.hariant.util.FlavorText;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractItem implements Keyed, Named, Described, FlavorText, ItemCreator, Registrable {
    
    protected final Key key;
    protected final Component name;
    protected final Icon icon;
    
    protected @NotNull Component description;
    protected @NotNull Component flavorText;
    protected @NotNull Rarity rarity;
    protected @NotNull ItemCategory category;
    
    public AbstractItem(@NotNull Key key, @NotNull Component name, @NotNull Icon icon) {
        this.key = key;
        this.name = name;
        this.icon = icon;
        this.description = Described.defaultValue();
        this.flavorText = Component.empty();
        this.rarity = Rarity.ONE_STAR;
        this.category = ItemCategory.ACCOUNT_RESOURCE;
    }
    
    public @NotNull Rarity getRarity() {
        return rarity;
    }
    
    public void setRarity(@NotNull Rarity rarity) {
        this.rarity = rarity;
    }
    
    public @NotNull ItemCategory getCategory() {
        return category;
    }
    
    public void setCategory(@NotNull ItemCategory category) {
        this.category = category;
    }
    
    @NotNull
    @Override
    public ItemBuilder createBuilder() {
        final ItemBuilder builder = icon.createBuilder();
        builder.setName(name);
        builder.addLore(rarity.asComponent().color(Colors.DARK_GRAY));
        builder.addLore();
        
        // Append description
        builder.addWrappedLore(description, HariantConstants.COMPONENT_STYLER_DESCRIPTION);
        
        if (Component.IS_NOT_EMPTY.test(flavorText)) {
            builder.addLore();
            builder.addWrappedLore(flavorText, HariantConstants.COMPONENT_STYLER_FLAVOR_TEXT);
        }
        
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
    public Component getName() {
        return name;
    }
    
    public @NotNull Component getNameStyled() {
        return name.style(rarity.getStyle());
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
    
    @Override
    public String toString() {
        return Components.toString(name);
    }
    
    public @NotNull Component getNameStyledWithRarity() {
        return name.style(rarity.getStyle());
    }
    
    @Override
    public void onRegister() {
    }
    
    @Override
    public final void onUnregister() {
        throw new IllegalStateException("Cannot unregister %s!".formatted(this.getClass().getSimpleName()));
    }
    
}