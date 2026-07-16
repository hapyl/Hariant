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
    
    public int maxStackSize() {
        return 9999;
    }
    
    public final @NotNull Component format(@NotNull PlayerDatabase database) {
        return this.format(database.inventory.getResource(this));
    }
    
    public @NotNull Component format(long amount) {
        return Component.text("%,d".formatted(amount));
    }
    
    @Override
    public @NotNull HoverEvent<?> createHoverEvent() {
        return createItem().asHoverEvent();
    }
    
}
