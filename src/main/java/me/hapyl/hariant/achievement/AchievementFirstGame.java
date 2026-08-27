package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementFirstGame extends AchievementImpl {
    
    public AchievementFirstGame(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("First Game"),
                Component.text("Play your very first game.")
        );
    }
    
}