package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.text.RomanNumber;
import me.hapyl.hariant.Colors;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AchievementChampion extends AchievementImpl {
    
    AchievementChampion(@NotNull Key key, int index, int goal, @NotNull AchievementTier tier, @Nullable Achievement parent) {
        super(
                key,
                goal,
                Component.text("Champion " + RomanNumber.toRoman(index)),
                Component.empty()
                        .append(Component.text("Win a total of "))
                        .append(Component.text("%,d".formatted(goal), Colors.GOLD))
                        .append(Component.text(" times."))
        );
        
        setCategory(AchievementCategory.GENESIS);
        setTier(tier);
        setParent(parent);
    }
    
    @Override
    public boolean isProgressCapped() {
        return false;
    }
    
}
