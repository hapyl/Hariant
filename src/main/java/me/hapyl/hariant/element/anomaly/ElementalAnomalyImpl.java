package me.hapyl.hariant.element.anomaly;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.annotate.AutoRegisteredListener;
import me.hapyl.hariant.element.Element;
import me.hapyl.hariant.ui.ComponentDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@AutoRegisteredListener
public abstract class ElementalAnomalyImpl implements ElementalAnomaly {
    
    private final Key key;
    
    private final Component prefix;
    private final Component name;
    private final Style style;
    
    private Component description;
    
    public ElementalAnomalyImpl(@NotNull Key key, @NotNull Component prefix, @NotNull Component name, @NotNull Style style) {
        this.key = key;
        this.prefix = prefix;
        this.name = name;
        this.description = Described.defaultValue();
        this.style = style;
        
        AutoRegisteredListener.Registry.register(this);
    }
    
    public ElementalAnomalyImpl(@NotNull Key key, @NotNull Component name, @NotNull Element element) {
        this(key, element.getPrefix(), name, element.getStyle());
    }
    
    @Override
    @NotNull
    public final Key getKey() {
        return key;
    }
    
    @NotNull
    @Override
    public Component getPrefix() {
        return prefix;
    }
    
    @NotNull
    @Override
    public Component getPrefixStyled() {
        return prefix.style(getStyle());
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public Component getDescription() {
        return description;
    }
    
    @NotNull
    @Override
    public Style getStyle() {
        return style;
    }
    
    @Override
    public void display(@NotNull Location location) {
        ComponentDisplay.ofAscend(this.asComponent(), location, 40, 1.0f);
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
        
        final ElementalAnomalyImpl that = (ElementalAnomalyImpl) object;
        return Objects.equals(this.key, that.key);
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return prefix.style(style).appendSpace().append(name.style(style));
    }
    
}
