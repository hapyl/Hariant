package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyBurn;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDeathEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.hero.inferno.InfernoDemonType;
import me.hapyl.hariant.hero.inferno.TalentDemonsplitQuazii;
import me.hapyl.hariant.hero.inferno.TalentDemonsplitTyphoeus;
import me.hapyl.hariant.hero.inferno.TalentInfernalWrath;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.util.Definition;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class AchievementInfernoFullIgnition extends AchievementHeroImpl implements Listener {
    
    AchievementInfernoFullIgnition(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Full Ignition"),
                Component.empty()
                         .append(Component.text("Defeat an enemy with "))
                         .append(ElementalAnomalyType.BURN)
                         .append(Component.text(" anomaly, triggered by "))
                         .append(TalentRegistry.INFERNAL_WRATH)
                         .append(Component.text(", while they are affected by "))
                         .append(InfernoDemonType.QUAZII.getName())
                         .append(Component.text("'s "))
                         .append(Definition.DECAY)
                         .append(Component.text(" or "))
                         .append(InfernoDemonType.TYPHOEUS.getName())
                         .append(Component.text("'s "))
                         .append(TalentDemonsplitTyphoeus.HELLFIRE_AURA_NAME.color(Colors.HELL))
                         .append(Component.text(".")),
                HeroRegistry.INFERNO
        );
        
        setTier(AchievementTier.TIER_3);
    }
    
    @EventHandler
    public void handleHariantDeathEvent(HariantDeathEvent ev) {
        if (!(ev.getDamageInstance().getDamageSource() instanceof ElementalAnomalyBurn.ElementalAnomalyBurnDamageSource damageSource)) {
            return;
        }
        
        if (!(damageSource.getAnomalySource() instanceof TalentInfernalWrath.InfernalWrathAnomalySource anomalySource)) {
            return;
        }
        
        if (!(anomalySource.getSource() instanceof HariantPlayer player)) {
            return;
        }
        
        // Check whether entity has either Quazii decay or Typhoeus hellfire aura
        final HariantEntity entity = ev.getEntity();
        
        if (!entity.hasHealthMutator(TalentDemonsplitQuazii.QuaziiDecay.class) && !entity.getAttributes().hasModifier(TalentDemonsplitTyphoeus.HELLFIRE_AURA_MODIFIER_KEY)) {
            return;
        }
        
        this.progress(player.getProfile());
    }
    
}