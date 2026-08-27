package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.hero.mage.WeaponSoulEaterUltimate;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementMageSoulStorm extends AchievementHeroImpl {
    
    AchievementMageSoulStorm(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Soul Stormed"),
                Component.empty()
                         .append(Component.text("Convert "))
                         .append(TalentRegistry.SOUL_STORM.getMaximumSoulsConsumed())
                         .append(Component.text(" souls into "))
                         .append(WeaponSoulEaterUltimate.WeaponRangeProjectileTypeRestlessSoul.NAME.color(Colors.RESTLESS_SOUL))
                         .append(Component.text("s", Colors.RESTLESS_SOUL))
                         .append(Component.text(" using "))
                         .append(TalentRegistry.SOUL_STORM)
                         .append(Component.text(".")),
                HeroRegistry.MAGE
        
        );
    }
    
}