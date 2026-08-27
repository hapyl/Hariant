package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.hero.nyx.TalentDualVerdict;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementNyxDoubleDuty extends AchievementHeroImpl {
    
    AchievementNyxDoubleDuty(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Double Duty"),
                Component.empty()
                         .append(Component.text("Have both a "))
                         .append(TalentDualVerdict.DROPLET_GOOD)
                         .append(Component.text(" droplet be picked up by an "))
                         .append(Component.text("ally", Colors.GREEN))
                         .append(Component.text(" and a "))
                         .append(TalentDualVerdict.DROPLET_BAD)
                         .append(Component.text(" droplet be picked up by an "))
                         .append(Component.text("enemy", Colors.RED))
                         .append(Component.text(" from the same "))
                         .append(TalentRegistry.DUAL_VERDICT)
                         .append(Component.text(".")),
                HeroRegistry.NYX
        );
        
        setTier(AchievementTier.TIER_2);
    }
    
}
