package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.hero.HeroRegistry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementMageSoulStorm extends AchievementHeroImpl {
    
    AchievementMageSoulStorm(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Soul Stormed"),
                Component.empty(),
                HeroRegistry.MAGE
        );
        
        // FIXME (xanyjl @ Thursday, September 3) ->
    }
    
}