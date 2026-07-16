package me.hapyl.hariant.inventory.item;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.database.Instantiable;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.registry.Registrable;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Item extends AbstractItem implements Instantiable, Registrable {
    
    public Item(@NotNull Key key, @NotNull Component name, @NotNull Icon icon) {
        super(key, name, icon);
    }
    
    @NotNull
    @Override
    public ItemInstance newInstance(@NotNull PlayerDatabase database, @NotNull UUID uuid) {
        return new ItemInstance(database, this, uuid);
    }
    
}