package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementArcherTriplet extends AchievementHeroImpl {
    
    private static final int NUMBER_OF_ARROWS_TO_HIT = 3;
    
    AchievementArcherTriplet(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Triplet!"),
                Component.empty()
                         .append(Component.text("Hit enemies with all three arrows using "))
                         .append(TalentRegistry.TRIPLE_SHOT)
                         .append(Component.text(".")),
                HeroRegistry.ARCHER
        );
        
        setTier(AchievementTier.TIER_2);
    }
    
    @Override
    public int uniqueIdCounterValue() {
        return NUMBER_OF_ARROWS_TO_HIT;
    }
    
}