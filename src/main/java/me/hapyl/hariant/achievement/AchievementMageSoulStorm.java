package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.text.NumberToWord;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementMageSoulStorm extends AchievementHeroImpl {
    
    private static final int NUMBER_OF_TIMES_TO_HIT = 5;
    
    AchievementMageSoulStorm(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Soul Stormed"),
                Component.empty()
                         .append(Component.text("Hit the same enemy "))
                         .append(Component.text(NumberToWord.toWord(NUMBER_OF_TIMES_TO_HIT).toLowerCase(), Colors.RED))
                         .append(Component.text(" times using "))
                         .append(TalentRegistry.SOUL_STORM)
                         .append(Component.text(".")),
                HeroRegistry.MAGE
        );
    }
    
    public static void progress(@NotNull HariantPlayer player, int numberOfTimesHit) {
        if (numberOfTimesHit < NUMBER_OF_TIMES_TO_HIT) {
            return;
        }
        
        AchievementRegistry.MAGE_SOUL_STORM.progress(player.getProfile());
    }
    
}