package me.hapyl.hariant.daily;

import me.hapyl.hariant.database.PlayerDatabase;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class DailyImpl implements Daily {
    
    private final Component name;
    private final DailyDescription description;
    private final DailyTier tier;
    
    private final int min;
    private final int max;
    
    DailyImpl(@NotNull Component name, @NotNull DailyDescription description, @NotNull DailyTier tier, int min, int max) {
        this.name = name;
        this.description = description;
        this.tier = tier;
        this.min = min;
        this.max = max;
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public @NotNull DailyDescription getDescription() {
        return description;
    }
    
    @Override
    public @NotNull DailyTier getTier() {
        return tier;
    }
    
    @Override
    public int getMinimumGoal() {
        return min;
    }
    
    @Override
    public int getMaximumGoal() {
        return max;
    }
    
    @Override
    public boolean canGenerate(@NotNull PlayerDatabase database) {
        return true;
    }
    
}