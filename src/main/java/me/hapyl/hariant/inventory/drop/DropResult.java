package me.hapyl.hariant.inventory.drop;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class DropResult {
    
    private final Drop drop;
    private final int amount;
    private final double chance;
    private final DropTier dropTier;
    
    DropResult(@NotNull Drop drop, int amount, double chance, @NotNull DropTier dropTier) {
        this.drop = drop;
        this.amount = amount;
        this.chance = chance;
        this.dropTier = dropTier;
    }
    
    public @NotNull Drop getDrop() {
        return drop;
    }
    
    public int getAmount() {
        return amount;
    }
    
    public double getChance() {
        return chance;
    }
    
    public @NotNull DropTier getDropTier() {
        return dropTier;
    }
    
    @Override
    public final boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final DropResult that = (DropResult) object;
        return Objects.equals(this.drop.getKey(), that.drop.getKey());
    }
    
    @Override
    public final int hashCode() {
        return Objects.hashCode(this.drop.getKey());
    }
    
}