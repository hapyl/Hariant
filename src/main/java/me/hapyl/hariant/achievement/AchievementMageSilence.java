package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.event.HariantTalentEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class AchievementMageSilence extends AchievementHeroImpl implements Listener {
    
    private static final int GOAL = 50;
    
    AchievementMageSilence(@NotNull Key key) {
        super(
                key,
                GOAL,
                Component.text("Silence!"),
                Component.empty()
                         .append(Component.text("Silence enemies "))
                         .append(Component.text("%,d".formatted(GOAL), Colors.NUMBER))
                         .append(Component.text(" times using "))
                         .append(TalentRegistry.ARCANE_MUTE)
                         .append(Component.text(".")),
                HeroRegistry.MAGE
        );
        
        setTier(AchievementTier.TIER_2);
    }
    
    @EventHandler
    public void handleHariantTalentEvent(HariantTalentEvent ev) {
        if (!ev.getResponse().isOk() || !ev.getTalent().equals(TalentRegistry.ARCANE_MUTE)) {
            return;
        }
        
        this.progress(ev.getPlayer().getProfile());
    }
    
}
