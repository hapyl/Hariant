package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.text.NumberToWord;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.hero.archer.TalentHawkeye;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementArcherNoLuckAllSkill extends AchievementHeroImpl {
    
    private static final int HAWKEYE_ARROW_REQUIREMENT = 3;
    
    AchievementArcherNoLuckAllSkill(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("No Luck All Skill"),
                Component.empty()
                         .append(Component.text("Shoot "))
                         .append(Component.text(NumberToWord.toWord(HAWKEYE_ARROW_REQUIREMENT).toLowerCase()))
                         .appendSpace()
                         .append(TalentHawkeye.HAWKEYE_ARROW)
                         .append(Component.text(" in a row.")),
                HeroRegistry.ARCHER
        );
        
        setTier(AchievementTier.TIER_2);
        setHidden(true);
    }
    
    @Override
    public int uniqueIdCounterValue() {
        return HAWKEYE_ARROW_REQUIREMENT;
    }
    
}