package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.text.NumberToWord;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.util.Counter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementMageSoulHarvested extends AchievementHeroImpl {
    
    private static final int NUMBER_OF_ENEMIES_TO_HIT = 3;
    
    AchievementMageSoulHarvested(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Soul Harvested"),
                Component.empty()
                         .append(Component.text("Harvest souls from "))
                         .append(Component.text(NumberToWord.toWord(NUMBER_OF_ENEMIES_TO_HIT).toLowerCase(), Colors.NUMBER))
                         .append(Component.text(" enemies using the explosion of "))
                         .append(TalentRegistry.SOUL_FOG)
                         .append(Component.text(".")),
                HeroRegistry.MAGE
        );
        
        setTier(AchievementTier.TIER_2);
    }
    
    public static void progress(@NotNull PlayerProfile profile, @NotNull Counter numberOfEnemiesHit) {
        if (numberOfEnemiesHit.intValue() < NUMBER_OF_ENEMIES_TO_HIT) {
            return;
        }
        
        AchievementRegistry.MAGE_SOUL_HARVESTED.progress(profile);
    }
    
}
