package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.hero.archer.TalentHawkeye;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementArcherBullseye extends AchievementHeroImpl {
    
    AchievementArcherBullseye(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Bullseye"),
                Component.empty()
                         .append(Component.text("Hit an enemy with a "))
                         .append(TalentHawkeye.HAWKEYE_ARROW)
                         .append(Component.text(".")),
                HeroRegistry.ARCHER
        );
    }
    
}