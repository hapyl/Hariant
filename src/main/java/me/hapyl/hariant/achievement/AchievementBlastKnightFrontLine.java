package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementBlastKnightFrontLine extends AchievementHeroImpl {
    
    private static final double GOAL = 100_000;
    
    AchievementBlastKnightFrontLine(@NotNull Key key) {
        super(
                key,
                GOAL,
                Component.text("Front Line"),
                Component.empty()
                         .append(Component.text("Split a total of "))
                         .append(Component.text("%,.0f".formatted(GOAL), Colors.RED))
                         .append(Component.text(" DMG using "))
                         .append(TalentRegistry.QUANTUM_WARD)
                         .append(Component.text(".")),
                HeroRegistry.BLAST_KNIGHT
        );
        
        setTier(AchievementTier.TIER_3);
    }
    
}