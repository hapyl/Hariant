package me.hapyl.hariant.attribute;

import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.util.decimal.DecimalFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public class AttributeImpl implements Attribute {
    
    private final Component prefix;
    private final Component name;
    private final Component description;
    private final Style style;
    private final DecimalFormat format;
    
    AttributeImpl(@NotNull Component prefix, @NotNull Component name, @NotNull Component description, @NotNull TextColor color, @NotNull DecimalFormat format) {
        this.prefix = prefix;
        this.name = name;
        this.description = description;
        this.style = Style.style(color);
        this.format = format;
    }
    
    @Override
    public boolean isBase() {
        return false;
    }
    
    @Override
    public void update(@NotNull HariantEntity entity, double newValue) {
    }
    
    @Override
    public double defaultValue() {
        return 0;
    }
    
    @Override
    public double minValue() {
        return 0;
    }
    
    @Override
    public double maxValue() {
        return 100;
    }
    
    @NotNull
    @Override
    public Component getPrefix() {
        return prefix;
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @Override
    @NotNull
    public Style getStyle() {
        return style;
    }
    
    @NotNull
    @Override
    public Component getDescription() {
        return description;
    }
    
    @NotNull
    @Override
    public Component abbreviation() {
        return name;
    }
    
    @NotNull
    @Override
    public Component format(double value) {
        return format.format(value);
    }
}
