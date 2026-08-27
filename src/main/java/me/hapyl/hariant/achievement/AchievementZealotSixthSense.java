package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDeathEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.hero.zealot.TalentReckoning;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class AchievementZealotSixthSense extends AchievementHeroImpl implements Listener {
    
    AchievementZealotSixthSense(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Sixth Sense"),
                Component.empty()
                        .append(Component.text("Defeat an enemy using "))
                        .append(TalentRegistry.RECKONING)
                        .append(Component.text(".")),
                HeroRegistry.ZEALOT
        );
        
        setTier(AchievementTier.TIER_3);
    }
    
    @EventHandler
    public void handleHariantDeathEvent(HariantDeathEvent ev) {
        final DamageSource damageSource11 = ev.getDamageInstance().getDamageSource();
        
        if (!(damageSource11 instanceof TalentReckoning.ReckoningDamageSource damageSource)) {
            return;
        }
        
        if (!(damageSource.getSource() instanceof HariantPlayer player)) {
            return;
        }
        
        this.progress(player.getProfile());
    }
    
}
