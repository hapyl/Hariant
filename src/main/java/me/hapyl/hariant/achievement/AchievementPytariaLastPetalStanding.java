package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementPytariaLastPetalStanding extends AchievementHeroImpl {
    
    AchievementPytariaLastPetalStanding(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Last Petal Standing"),
                Component.empty()
                         .append(Component.text("Use "))
                         .append(TalentRegistry.FLOWER_BREEZE)
                         .append(Component.text(" to lower your health to the very minimum.")),
                HeroRegistry.PYTARIA
        );
    }
    
}
