package me.hapyl.hariant.hero.inferno;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.text.Strings;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.achievement.AchievementInfernoRottenToTheCore;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.heal.HealingSource;
import me.hapyl.hariant.entity.mutator.HealthMutatorDecay;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.hero.Race;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.util.field.DisplayField;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class TalentDemonsplitQuazii extends TalentDemonsplit implements Listener {
    
    private final @DisplayField Decimal everyNthHitTriggeredDecay = Decimal.ofValue(6);
    private final @DisplayField Decimal decayWorthOfMaxHealth = Decimal.ofPercentage(20);
    private final @DisplayField Decimal decayDuration = Decimal.ofSeconds(8);
    private final @DisplayField Decimal healingPerDecayTriggeredOfMaxHealth = Decimal.ofPercentage(20);
    
    public TalentDemonsplitQuazii(@NotNull Key key) {
        super(key, InfernoDemonType.QUAZII);
        
        setTalentType(TalentType.IMPAIR);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handleHariantDamageEvent(HariantDamageEvent ev) {
        if (!(demonEntityFromDamageEventOrNull(ev) instanceof InfernoDemonEntityQuazii quazii)) {
            return;
        }
        
        quazii.processDamage(ev.getEntity());
    }
    
    @Override
    public @NotNull InfernoDemonEntity newInstance(@NotNull HariantPlayer player, @NotNull InfernoDemonType demonType) {
        return new InfernoDemonEntityQuazii(player);
    }
    
    @Override
    public @NotNull Component describeAbility() {
        return Component.empty()
                        .append(Component.text("Every "))
                        .append(Component.text(Strings.stNdTh(everyNthHitTriggeredDecay.intValue()), Colors.NUMBER))
                        .append(Component.text(" hit on an enemy applies "))
                        .appendNewline()
                        .append(HealthMutatorDecay.COMPONENT)
                        .append(Component.text(", which temporary reduces their "))
                        .append(AttributeType.MAX_HEALTH)
                        .append(Component.text("."));
    }
    
    @Override
    public @NotNull Component describeReform() {
        return Component.empty()
                        .append(Component.text("Heal for "))
                        .append(healingPerDecayTriggeredOfMaxHealth)
                        .append(Component.text(" of "))
                        .append(AttributeType.MAX_HEALTH)
                        .append(Component.text(" for each "))
                        .append(HealthMutatorDecay.COMPONENT)
                        .append(Component.text(" applied."));
    }
    
    public class InfernoDemonEntityQuazii extends InfernoDemonEntity {
        
        private final Map<HariantEntity, Integer> numberOfHitsPerEnemy;
        
        private int numberOfDecaysTriggered;
        private double totalAmountOfDecay;
        
        public InfernoDemonEntityQuazii(@NotNull HariantPlayer player) {
            super(player, InfernoDemonType.QUAZII, TalentDemonsplitQuazii.this);
            
            this.numberOfHitsPerEnemy = Maps.newHashMap();
        }
        
        public void processDamage(@NotNull HariantEntity entity) {
            final int numberOfHits = numberOfHitsPerEnemy.merge(entity, 1, Integer::sum);
            
            if (numberOfHits % everyNthHitTriggeredDecay.intValue() == 0) {
                final double maxHealth = entity.getMaxHealth();
                final double decay = maxHealth * decayWorthOfMaxHealth.doubleValue();
                
                // If mutator successfully applied, increment total decay amount
                if (entity.addHealthMutator(new QuaziiDecay(decay, decayDuration.intValue()))) {
                    numberOfDecaysTriggered++;
                    totalAmountOfDecay += decay;
                }
            }
        }
        
        @Override
        public void onForm(@NotNull HariantPlayer player, @NotNull HeroDataInferno data) {
            InfernoDemon.drawParticleBox(player, location -> player.spawnWorldParticle(location, Particle.SMOKE, 1, 0), 2.3);
        }
        
        @Override
        public void onReform(@NotNull HariantPlayer player, @NotNull HeroDataInferno data) {
            super.onReform(player, data);
            
            final double maxHealth = player.getMaxHealth();
            final double healing = numberOfDecaysTriggered * healingPerDecayTriggeredOfMaxHealth.doubleValue() * maxHealth;
            
            if (healing > 0) {
                player.heal(HealingSource.create(healing, this.getName()));
                player.messageInfo(
                        Component.empty()
                                 .append(Race.DEMON.getPrefixStyled())
                                 .appendSpace()
                                 .append(getDemonType().getName())
                                 .append(Component.text(" healed for %.0f!".formatted(healing), Colors.GREEN))
                );
            }
            
            // Achievement
            AchievementInfernoRottenToTheCore.progress(player, totalAmountOfDecay);
        }
        
    }
    
    public static class QuaziiDecay extends HealthMutatorDecay {
        
        QuaziiDecay(double amount, int duration) {
            super(amount, duration);
        }
        
    }
    
}
