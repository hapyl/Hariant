package me.hapyl.hariant.inventory.drop;

import me.hapyl.hariant.inventory.item.Item;
import me.hapyl.hariant.profile.PlayerProfile;
import org.jetbrains.annotations.NotNull;

public final class DroppableItemImpl extends DroppableImpl {
    
    private final Item item;
    
    DroppableItemImpl(@NotNull Item item, final int weight) {
        super(item.getKey(), weight, item.getNameStyledWithRarity(), Amount.fixed(1));
        
        this.item = item;
    }
    
    @Override
    public @NotNull Drop drop(@NotNull PlayerProfile profile, int amount) {
        return profile.getDatabase().inventory.createItem(item);
    }
    
}
