package me.hapyl.hariant.daily;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.database.PlayerDatabase;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface Daily extends Named {
    
    @Override
    @NotNull Component getName();
    
    @NotNull DailyDescription getDescription();
    
    @NotNull DailyTier getTier();
    
    int getMinimumGoal();
    
    int getMaximumGoal();
    
    boolean canGenerate(@NotNull PlayerDatabase database);
    
    default int getRandomGoal() {
        final int min = this.getMinimumGoal();
        final int max = this.getMaximumGoal();
        
        if (min == max) {
            return min;
        }
        
        return Hariant.getRandom().nextInt(min, max + 1);
    }
    
}