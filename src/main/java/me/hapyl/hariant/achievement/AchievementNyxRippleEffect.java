package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementNyxRippleEffect extends AchievementHeroImpl {
    
    AchievementNyxRippleEffect(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Ripple Effect"),
                Component.empty()
                         .append(Component.text("Trigger "))
                         .append(TalentRegistry.REVERBERATION)
                         .append(Component.text(" effect from an ally's "))
                         .append(Component.text("impair", Colors.ARCHETYPE_HEXBANE))
                         .append(Component.text(" on an enemy.")),
                HeroRegistry.NYX
        );
    }
    
}
