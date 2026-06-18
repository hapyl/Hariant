package me.hapyl.hariant.inventory.drop;

import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.inventory.item.Item;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class DroppableItemImpl extends DroppableImpl {
    
    private final Item item;
    
    DroppableItemImpl(@NotNull Item item, final int weight) {
        super(item.getKey(), weight, item.getNameStyledWithRarity(), Amount.fixed(1));
        
        this.item = item;
    }
    
    @Override
    public void drop(@NotNull PlayerProfile profile, @NotNull Drop drop) {
        final PlayerDatabase database = profile.getDatabase();
        
        database.inventory.createItem(item.newInstance(database, UUID.randomUUID()));
    }
    
    @Override
    public @NotNull HoverEvent<?> createHoverEvent() {
        return item.createItem().asHoverEvent();
    }
    
}
