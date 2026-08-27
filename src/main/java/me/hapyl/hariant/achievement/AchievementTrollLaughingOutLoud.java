package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.hero.HeroRegistry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementTrollLaughingOutLoud extends AchievementHeroImpl {
    
    AchievementTrollLaughingOutLoud(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("LOL!"),
                Component.text("Perform a special trolling technique against an enemy player."),
                HeroRegistry.TROLL
        );
        
        setTier(AchievementTier.TIER_2);
    }
    
}