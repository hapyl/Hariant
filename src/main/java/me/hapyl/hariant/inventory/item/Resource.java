package me.hapyl.hariant.inventory.item;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.inventory.drop.Drop;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

public class Resource extends AbstractItem implements Drop {
    
    public Resource(@NotNull Key key, @NotNull Component name, @NotNull Icon icon) {
        super(key, name, icon);
    }
    
    @Override
    public int maxStackSize() {
        return 1_000;
    }
    
    @NotNull
    public Component format(@NotNull PlayerDatabase database) {
        return Component.text("%,d".formatted(database.inventory.getResource(this)));
    }
    
    @Override
    public @NotNull HoverEvent<?> createHoverEvent() {
        return createItem().asHoverEvent();
    }
    
}
