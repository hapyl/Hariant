package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.hero.HeroRegistry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementAlchemistIThinkIDrankTooMuch extends AchievementHeroImpl {
    
    AchievementAlchemistIThinkIDrankTooMuch(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("I Think I Drank Too Much"),
                Component.empty()
                        .append(Component.text("Die from drinking too many abyssal potions.")),
                HeroRegistry.ALCHEMIST
        );
        
        setTier(AchievementTier.TIER_2);
    }
    
}