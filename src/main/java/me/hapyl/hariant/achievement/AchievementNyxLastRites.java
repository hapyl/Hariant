package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyIntangibility;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDeathEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.hero.nyx.TalentImpalement;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class AchievementNyxLastRites extends AchievementHeroImpl implements Listener {
    
    AchievementNyxLastRites(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Last Rites"),
                Component.empty()
                         .append(Component.text("Defeat an enemy with "))
                         .append(ElementalAnomalyType.INTANGIBILITY)
                         .append(Component.text(", triggered by the final slash of the "))
                         .append(Component.text("overcharged", Colors.ULTIMATE_OVERCHARGE))
                         .append(Component.text(" version of "))
                         .append(TalentRegistry.IMPALEMENT)
                         .append(Component.text(".")),
                HeroRegistry.NYX
        );
        
        setTier(AchievementTier.TIER_3);
    }
    
    @EventHandler
    public void handleHariantDeathEvent(HariantDeathEvent ev) {
        final DamageInstance damageInstance = ev.getDamageInstance();
        
        if (!(damageInstance.getDamageSource() instanceof ElementalAnomalyIntangibility.IntangibilityDamageSource damageSource)) {
            return;
        }
        
        if (!(damageSource.getAnomalySource() instanceof TalentImpalement.ImpalementAnomalySource)) {
            return;
        }
        
        if (!(damageSource.getSource() instanceof HariantPlayer player)) {
            return;
        }
        
        this.progress(player.getProfile());
    }
    
}