package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementAlchemistSpecialDelivery extends AchievementHeroImpl {
    
    private static final int ELEMENTAL_REACTIONS_TO_TRIGGER = 7;
    
    AchievementAlchemistSpecialDelivery(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Special Delivery"),
                Component.empty()
                         .append(Component.text("Trigger seven "))
                         .append(Component.text("elemental anomalies", Colors.ATTRIBUTE_ELEMENTAL_MASTERY))
                         .append(Component.text(" at the same time using "))
                         .append(TalentRegistry.BUNDLE_O_POTIONS)
                         .append(Component.text(".")),
                HeroRegistry.ALCHEMIST
        );
        
        setTier(AchievementTier.TIER_3);
    }
    
    @Override
    public int uniqueIdCounterValue() {
        return ELEMENTAL_REACTIONS_TO_TRIGGER;
    }
    
}