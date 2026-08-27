package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementBlastKnightPerfectTiming extends AchievementHeroImpl {
    
    AchievementBlastKnightPerfectTiming(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Perfect Timing"),
                Component.empty()
                         .append(Component.text("Parry an incoming attack using "))
                         .append(TalentRegistry.QUANTUM_SHIELD)
                         .append(Component.text(".")),
                HeroRegistry.BLAST_KNIGHT
        );
    }
    
}