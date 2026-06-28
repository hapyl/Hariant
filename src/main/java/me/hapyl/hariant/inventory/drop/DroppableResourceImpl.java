package me.hapyl.hariant.inventory.drop;

import me.hapyl.hariant.inventory.item.Resource;
import me.hapyl.hariant.profile.PlayerProfile;
import org.jetbrains.annotations.NotNull;

public final class DroppableResourceImpl extends DroppableImpl {
    
    private final Resource resource;
    
    DroppableResourceImpl(@NotNull Resource resource, final int weight, @NotNull Amount amount) {
        super(resource.getKey(), weight, resource.getNameStyledWithRarity(), amount);
        
        this.resource = resource;
    }
    
    @Override
    public @NotNull Drop drop(@NotNull PlayerProfile profile, int amount) {
        profile.getDatabase().inventory.addResource(resource, amount);
        
        return resource;
    }
    
}