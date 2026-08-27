package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementAlchemistLocalBrewery extends AchievementHeroImpl {
    
    AchievementAlchemistLocalBrewery(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Local Brewery"),
                Component.empty()
                         .append(Component.text("Brew a toxic concoction using "))
                         .append(TalentRegistry.ALCHEMICAL_CAULDRON)
                         .append(Component.text(".")),
                HeroRegistry.ALCHEMIST
        );
    }
    
}
