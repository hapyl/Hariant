package me.hapyl.hariant.inventory.drop;

import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.inventory.item.Resource;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

public final class DroppableResourceImpl extends DroppableImpl {
    
    private final Resource resource;
    
    DroppableResourceImpl(@NotNull Resource resource, final int weight, @NotNull Amount amount) {
        super(resource.getKey(), weight, resource.getNameStyledWithRarity(), amount);
        
        this.resource = resource;
    }
    
    @Override
    public void drop(@NotNull PlayerProfile profile, @NotNull Drop drop) {
        final PlayerDatabase database = profile.getDatabase();
        
        database.inventory.addResource(resource, drop.getAmount());
    }
    
    @Override
    public @NotNull HoverEvent<?> createHoverEvent() {
        return resource.createBuilder().asIcon().asHoverEvent();
    }
    
}
