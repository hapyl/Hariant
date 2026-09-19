package me.hapyl.hariant.util.field;

import com.google.common.collect.Lists;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.util.ComponentFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;

public final class DisplayFieldInstance implements ComponentLike {
    
    private final Component component;
    
    /**
     * {@see #create(Component, Component)}
     */
    DisplayFieldInstance(@NotNull Component fieldName, @NotNull Component fieldValue) {
        this.component = Component.empty()
                                  .append(Component.text(" "))
                                  .append(fieldName.color(Colors.WHITE))
                                  .append(Component.text(" "))
                                  .append(fieldValue.color(Colors.GRAY));
    }
    
    @Override
    public @NotNull Component asComponent() {
        return component;
    }
    
    public static @NotNull DisplayFieldInstance create(@NotNull Component fieldName, @NotNull Component fieldValue) {
        return new DisplayFieldInstance(fieldName, fieldValue);
    }
    
    public static @NotNull List<DisplayFieldInstance> parse(@NotNull DisplayFieldProvider provider) {
        final List<DisplayFieldInstance> displayFields = Lists.newArrayList();
        
        // Parse provider fields via iterating over the class hierarchy in reverse order
        try {
            for (Class<?> clazz : getClassHierarchyReversed(provider)) {
                for (Field field : clazz.getDeclaredFields()) {
                    final DisplayField displayField = field.getAnnotation(DisplayField.class);
                    
                    if (displayField == null) {
                        continue;
                    }
                    
                    field.setAccessible(true);
                    final Object fieldValue = field.get(provider);
                    
                    if (!(fieldValue instanceof ComponentFormatter formatter)) {
                        throw new IllegalArgumentException("Display field %s (%s) in %s must implement %s!".formatted(field.getName(), field.getType().getSimpleName(), clazz.getSimpleName(), ComponentFormatter.class.getSimpleName()));
                    }
                    
                    displayFields.add(DisplayFieldInstance.create(Component.text(formatFieldName(field, displayField)), formatter.format()));
                }
            }
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Error parsing display fields in %s: %s".formatted(provider.getClass().getSimpleName(), e.getMessage()), e);
        }
        
        // Append display fields from providers `initDisplayFields`
        provider.initDisplayFields(displayFields);
        
        return displayFields;
    }
    
    private static @NotNull String formatFieldName(@NotNull Field field, @NotNull DisplayField annotation) {
        if (!annotation.name().isEmpty()) {
            return annotation.name();
        }
        
        final String fieldName = field.getName();
        final StringBuilder builder = new StringBuilder();
        
        final int length = fieldName.length();
        
        for (int i = 0; i < length; i++) {
            final char ch = fieldName.charAt(i);
            
            // First char is always uppercase
            if (i == 0) {
                builder.append(Character.toUpperCase(ch));
            }
            else {
                builder.append(ch);
                
                // If there is a next char, and it's either uppercase or a digit, append a space
                if (i + 1 < length) {
                    final char nextCh = fieldName.charAt(i + 1);
                    
                    if (Character.isUpperCase(nextCh) || Character.isDigit(nextCh)) {
                        builder.append(" ");
                    }
                }
            }
        }
        
        return builder.toString();
    }
    
    private static @NotNull List<Class<?>> getClassHierarchyReversed(@NotNull DisplayFieldProvider provider) {
        final List<Class<?>> hierarchy = Lists.newArrayList();
        Class<?> clazz = provider.getClass();
        
        while (true) {
            final Class<?> superClass = clazz.getSuperclass();
            
            // If super class is not `DisplayFieldProvider`, it means the current class is the parent,
            // so stop here, we don't need the parent because it cannot have display fields
            if (!DisplayFieldProvider.class.isAssignableFrom(superClass)) {
                break;
            }
            
            // Since we need a reverse order of the hierarchy, add the clazz as first element, which is
            // not the best approach, but it doesn't really matter at such small scale
            hierarchy.addFirst(clazz);
            clazz = superClass;
        }
        
        return hierarchy;
    }
    
}
