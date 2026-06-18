package me.hapyl.hariant.inventory.drop;

import me.hapyl.hariant.profile.PlayerProfile;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a generated {@link Droppable}.
 */
public final class Drop {
    
    private final Droppable droppable;
    private final DropTier dropTier;
    
    private final double dropChance;
    private final int amount;
    
    Drop(@NotNull Droppable droppable, @NotNull DropTier dropTier, double dropChance, int amount) {
        this.droppable = droppable;
        this.amount = amount;
        this.dropChance = dropChance;
        this.dropTier = dropTier;
    }
    
    public @NotNull Droppable getDroppable() {
        return droppable;
    }
    
    public @NotNull DropTier getDropTier() {
        return dropTier;
    }
    
    public double getDropChance() {
        return dropChance;
    }
    
    public int getAmount() {
        return amount;
    }
    
    public void drop(@NotNull PlayerProfile profile) {
        droppable.drop(profile, this);
    }
    
}