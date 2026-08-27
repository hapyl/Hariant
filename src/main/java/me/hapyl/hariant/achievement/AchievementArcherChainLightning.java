package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementArcherChainLightning extends AchievementHeroImpl {
    
    AchievementArcherChainLightning(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Chain Lightning"),
                Component.empty()
                         .append(Component.text("Cause chain reaction between three enemies using "))
                         .append(TalentRegistry.CHAIN_LIGHTNING)
                         .append(Component.text(".")),
                HeroRegistry.ARCHER
        );
        
        setTier(AchievementTier.TIER_3);
    }
    
}
