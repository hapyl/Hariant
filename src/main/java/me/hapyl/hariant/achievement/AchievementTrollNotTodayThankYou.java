package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantInvulnerabilityEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class AchievementTrollNotTodayThankYou extends AchievementHeroImpl implements Listener {
    
    AchievementTrollNotTodayThankYou(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Not Today, Thank You!"),
                Component.empty()
                         .append(Component.text("Dodge lethal damage using "))
                         .append(TalentRegistry.PANIC_ROLL)
                         .append(Component.text(".")),
                HeroRegistry.TROLL
        );
        
        setTier(AchievementTier.TIER_2);
    }
    
    @EventHandler
    public void handleHariantInvulnerabilityEvent(HariantInvulnerabilityEvent ev) {
        if (!(ev.getEntity() instanceof HariantPlayer player) || TalentRegistry.PANIC_ROLL.getInvulnerabilitySource() != ev.getInvulnerabilitySource()) {
            return;
        }
        
        final double health = ev.getEntity().getHealth();
        final double damage = ev.getDamageInstance().getDamage();
        
        // We cannot check for `DamageInstance#isLethal` because it's not calculated yet; hence manual calculations
        if (damage < health) {
            return;
        }
        
        this.progress(player.getProfile());
    }
    
}
