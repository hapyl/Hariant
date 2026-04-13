package me.hapyl.hariant.element.anomaly;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.annotate.AutoRegisteredListener;
import me.hapyl.hariant.ui.ComponentDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@AutoRegisteredListener
public abstract class ElementalAnomalyImpl implements ElementalAnomaly {
    
    private static final Style DEFAULT_ANOMALY_STYLE = Style.style(Colors.ATTRIBUTE_ELEMENTAL_MASTERY);
    
    private final Key key;
    private final Component name;
    
    private Component description;
    
    protected ElementalAnomalyImpl(@NotNull Key key, @NotNull Component name) {
        this.key = key;
        this.name = name;
        this.description = Described.defaultValue();
        
        AutoRegisteredListener.Registry.register(this);
    }
    
    @Override
    @NotNull
    public final Key getKey() {
        return key;
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
        return DEFAULT_ANOMALY_STYLE;
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
        return name.style(this.getStyle());
    }
    
    @Override
    public void display(@NotNull Location location) {
        ComponentDisplay.ofAscend(this.asComponent(), location, 40, 1.0f);
    }
    
}
