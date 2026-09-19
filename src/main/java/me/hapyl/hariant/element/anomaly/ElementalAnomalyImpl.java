package me.hapyl.hariant.element.anomaly;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.annotate.AutoRegisteredListener;
import me.hapyl.hariant.element.ElementType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@AutoRegisteredListener
public abstract class ElementalAnomalyImpl implements ElementalAnomaly {
    
    private final Key key;
    private final ElementType elementType;
    
    private final Component prefix;
    private final Component name;
    private final Style style;
    private final ElementalPotency potency;
    
    private Component description;
    
    ElementalAnomalyImpl(@NotNull Key key, @NotNull ElementType element, @NotNull Component name, @NotNull ElementalPotency potency) {
        this.key = key;
        this.elementType = element;
        this.prefix = element.getPrefix();
        this.name = name;
        this.description = Described.defaultValue();
        this.style = element.getStyle();
        this.potency = potency;
        
        AutoRegisteredListener.Registry.register(this);
    }
    
    @Override
    public final @NotNull Key getKey() {
        return key;
    }
    
    @Override
    public final @NotNull ElementType getElementType() {
        return elementType;
    }
    
    @Override
    public @NotNull Component getPrefix() {
        return prefix;
    }
    
    @Override
    public @NotNull Component getPrefixStyled() {
        return prefix.style(style);
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public @NotNull Component getDescription() {
        return description;
    }
    
    @Override
    public @NotNull Style getStyle() {
        return style;
    }
    
    @Override
    public void setDescription(@NotNull Component description) {
        this.description = description;
    }
    
    public @NotNull Component getNameStyled() {
        return name.style(style);
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
        
        final ElementalAnomalyImpl that = (ElementalAnomalyImpl) object;
        return Objects.equals(this.key, that.key);
    }
    
    @Override
    public @NotNull Component asComponent() {
        return prefix.style(style).appendSpace().append(name.style(style));
    }
    
    @Override
    public @NotNull ElementalPotency getPotency() {
        return potency;
    }
    
}
