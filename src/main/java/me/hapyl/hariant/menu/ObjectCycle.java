package me.hapyl.hariant.menu;

import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.inventory.item.ItemCreator;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public abstract class ObjectCycle<E extends Named> implements ItemCreator {
    
    private static final Component COMPONENT_POINTER_CURRENT = Component.text(" ➥ ", Colors.GREEN);
    private static final Component COMPONENT_POINTER = Component.text("    ");
    
    private final List<? extends E> values;
    private int pointer;
    
    public ObjectCycle(@NotNull Collection<? extends E> values) {
        this.values = List.copyOf(values);
        this.pointer = 0;
    }
    
    public ObjectCycle(@NotNull E[] values) {
        this(List.of(values)); // Implicit immutable list copy
    }
    
    public @NotNull Component getName(@NotNull E e) {
        return e.getName();
    }
    
    public @NotNull ItemBuilder createBaseBuilder() {
        return new ItemBuilder(Material.FILLED_MAP).setName(Component.text("Default Cycle"));
    }
    
    @Override
    public final @NotNull ItemBuilder createBuilder() {
        final ItemBuilder builder = this.createBaseBuilder();
        
        // Append values
        int index = 0;
        
        for (E value : values) {
            final boolean current = pointer == index++;
            
            builder.addLore(
                    Component.empty()
                             .append(current ? COMPONENT_POINTER_CURRENT : COMPONENT_POINTER)
                             .append(this.getName(value))
            );
        }
        
        return builder;
    }
    
    public @NotNull ItemBuilder createBuilderDefaultCycle() {
        return this.createBuilder()
                   .addLore()
                   .addLore(ButtonComponents.left("cycle"))
                   .addLore(ButtonComponents.right("cycle backwards"));
    }
    
    public abstract void onCycle(@NotNull E e);
    
    public void cycleNext() {
        this.pointer = (pointer + 1) % values.size();
        this.onCycle(values.get(pointer));
    }
    
    public void cyclePrevious() {
        this.pointer = (pointer - 1 + values.size()) % values.size();
        this.onCycle(values.get(pointer));
    }
    
    public @NotNull E currentValue() {
        return values.get(pointer);
    }
    
}
