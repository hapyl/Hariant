package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementTrollBlastOff extends AchievementHeroImpl {
    
    private static final int TICK_THRESHOLD = 20;
    
    AchievementTrollBlastOff(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Blast Off!"),
                Component.empty()
                         .append(Component.text("Lift enemies into the sky using "))
                         .append(TalentRegistry.REPULSOR)
                         .append(Component.text(" twice in quick succession.")),
                HeroRegistry.TROLL
        );
        
        setTier(AchievementTier.TIER_2);
    }
    
    public static void progress(@NotNull HariantPlayer player, int lastExecution) {
        final int localTicks = player.localTicks();
        
        if (lastExecution > 0 && localTicks - lastExecution <= TICK_THRESHOLD) {
            AchievementRegistry.TROLL_BLAST_OFF.progress(player.getProfile());
        }
    }
    
    
}
