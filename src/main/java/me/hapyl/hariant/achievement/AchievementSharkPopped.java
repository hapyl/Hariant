package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDeathEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.hero.shark.TalentBubbleTrap;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class AchievementSharkPopped extends AchievementHeroImpl implements Listener {
    
    AchievementSharkPopped(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Popped"),
                Component.empty()
                        .append(Component.text("Defeat an enemy by "))
                        .append(TalentBubbleTrap.BubbleTrap.TRAP_NAME)
                        .append(Component.text(" pop DMG using "))
                        .append(TalentRegistry.BUBBLE_TRAP)
                        .append(Component.text(".")),
                HeroRegistry.SHARK
        );
        
        setTier(AchievementTier.TIER_2);
    }
    
    @EventHandler
    public void handleHariantDeathEvent(HariantDeathEvent ev) {
        if (!(ev.getDamageInstance().getDamageSource() instanceof TalentBubbleTrap.BubbleDamageSource damageSource) || !(damageSource.getSource() instanceof HariantPlayer player)) {
            return;
        }
        
        this.progress(player.getProfile());
    }
    
}