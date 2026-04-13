package me.hapyl.hariant.inventory.drop;

import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.inventory.item.Resource;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class DroppableResourceImpl implements Droppable {
    
    private final Resource resource;
    private final Component name;
    private final Amount amount;
    private final int weight;
    
    DroppableResourceImpl(@NotNull Resource resource, final int weight, @NotNull Amount amount) {
        this.resource = resource;
        this.name = resource.getName().append(Component.text(" (Resource)", NamedTextColor.DARK_GRAY));
        this.weight = weight;
        this.amount = amount;
    }
    
    @Range(from = 0, to = Integer.MAX_VALUE)
    @Override
    public int getWeight() {
        return weight;
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public Amount getAmount() {
        return amount;
    }
    
    @NotNull
    @Override
    public Drop drop(@NotNull PlayerProfile profile) {
        final PlayerDatabase database = profile.getDatabase();
        final int amount = this.amount.amount();
        
        database.inventory.addResource(resource, amount);
        
        return new Drop(this, amount);
    }
    
}
