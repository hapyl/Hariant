package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.util.Definition;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementBlastKnightFullReserves extends AchievementHeroImpl {
    
    AchievementBlastKnightFullReserves(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Full Reserves"),
                Component.empty()
                         .append(Component.text("Use "))
                         .append(TalentRegistry.QUANTUM_DISCHARGE)
                         .append(Component.text(" at the maximum "))
                         .appendNewline()
                         .append(Definition.QUANTUM_ENERGY)
                         .append(Component.text(" for the strongest explosion.")),
                HeroRegistry.BLAST_KNIGHT
        );
    }
    
}