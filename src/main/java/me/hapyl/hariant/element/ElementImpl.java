package me.hapyl.hariant.element;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.util.decimal.DecimalFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class ElementImpl implements Element {
    
    private final Key key;
    private final Component prefix;
    private final Component name;
    private final Style style;
    private final DecimalFormat format;
    private final Particle.Spell particle;
    
    ElementImpl(@NotNull Key key, @NotNull Component prefix, @NotNull Component name, @NotNull TextColor color) {
        this.key = key;
        this.prefix = prefix;
        this.name = name;
        this.style = Style.style(color);
        this.format = FLAT;
        this.particle = new Particle.Spell(Color.fromRGB(color.value()), 0.1f);
    }
    
    @Override
    @NotNull
    public final Key getKey() {
        return key;
    }
    
    @Override
    @NotNull
    public Component getPrefix() {
        return prefix;
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public Style getStyle() {
        return style;
    }
    
    @NotNull
    @Override
    public Component format(double value) {
        return format.format(value).style(style);
    }
    
    @Override
    public void tickEntity(@NotNull HariantEntity entity) {
        // Don't spawn particles if the entity is invisible
        if (entity.isInvisible()) {
            return;
        }
        
        // Default implementation displays particles with the color of the element
        entity.spawnWorldParticle(entity.getLocation(), Particle.EFFECT, 1, 0.2, 0.2, 0.2, 1f, particle);
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
        
        final ElementImpl that = (ElementImpl) object;
        return Objects.equals(this.key, that.key);
    }
}
