package me.hapyl.hariant.attribute;

import me.hapyl.hariant.util.decimal.DecimalFormat;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class AttributeScalingImpl implements AttributeScaling {
    
    private final Component format;
    
    public AttributeScalingImpl(@NotNull Component format) {
        this.format = format;
    }
    
    @Override
    public @NotNull Component format() {
        return format;
    }
    
    protected static @NotNull Component createFormat(@NotNull Map<? extends AttributeType, ? extends Double> scalingMap, final double flat) {
        final Component component = scalingMap.entrySet()
                                              .stream()
                                              .sorted(Map.Entry.comparingByKey())
                                              .map(entry -> {
                                                  return Component.empty()
                                                                  .append(DecimalFormat.PERCENTAGE.format(entry.getValue()))
                                                                  .appendSpace()
                                                                  .append(entry.getKey().abbreviation());
                                              })
                                              .collect(Component.toComponent(Component.text(" + ")));
        
        return flat > 0
               ? component.append(Component.text(" + %,.0f".formatted(flat)))
               : component;
    }
    
}