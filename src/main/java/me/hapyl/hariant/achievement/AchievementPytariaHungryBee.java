package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementPytariaHungryBee extends AchievementHeroImpl {
    
    AchievementPytariaHungryBee(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Hungry Bee"),
                Component.empty()
                         .append(Component.text("Have a bee summoned by "))
                         .append(TalentRegistry.FEEL_THE_BREEZE)
                         .append(Component.text(" deal damage to an enemy affected by "))
                         .append(TalentRegistry.ROSE_IVY)
                         .append(Component.text(".")),
                HeroRegistry.PYTARIA
        );
        
        setTier(AchievementTier.TIER_2);
    }
    
}
