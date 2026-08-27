package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementBlastKnightGuardianAngel extends AchievementHeroImpl {
    
    AchievementBlastKnightGuardianAngel(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Guardian Angel"),
                Component.empty()
                         .append(Component.text("Save an ally from lethal DMG by splitting the DMG using "))
                         .append(TalentRegistry.QUANTUM_WARD)
                         .append(Component.text(".")),
                HeroRegistry.BLAST_KNIGHT
        );
        
        setTier(AchievementTier.TIER_2);
    }
    
}
