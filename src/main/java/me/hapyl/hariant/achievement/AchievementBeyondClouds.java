package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementBeyondClouds extends AchievementImpl {
    
    AchievementBeyondClouds(@NotNull Key key) {
        super(key, 1);
        
        setName(Component.text("Beyond Clouds"));
        setDescription(Component.text("Die from falling out of a certain kingdom in the clouds."));
        
        setHidden(true);
    }
}
