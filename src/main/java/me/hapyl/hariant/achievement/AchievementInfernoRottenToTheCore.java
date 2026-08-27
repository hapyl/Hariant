package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.hero.inferno.InfernoDemonType;
import me.hapyl.hariant.util.Definition;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementInfernoRottenToTheCore extends AchievementHeroImpl {
    
    private static final int DECAY_TO_APPLY = 2000;
    
    AchievementInfernoRottenToTheCore(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Rotten to the Core"),
                Component.empty()
                        .append(Component.text("Apply "))
                        .append(Component.text("%,d".formatted(DECAY_TO_APPLY), Colors.RED))
                        .append(Component.text(" worth of "))
                        .append(Definition.DECAY)
                        .append(Component.text(" to enemies within a single use of "))
                        .append(InfernoDemonType.QUAZII.getName())
                        .append(Component.text(" death beam.")),
                HeroRegistry.INFERNO
        );
        
        setTier(AchievementTier.TIER_3);
    }
    
    public static void progress(@NotNull HariantPlayer player, double totalDecay) {
        if (totalDecay < DECAY_TO_APPLY) {
            return;
        }
        
        AchievementRegistry.INFERNO_ROTTEN_TO_THE_CORE.progressFamily(player.getProfile());
    }
    
}