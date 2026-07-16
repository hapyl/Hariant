package me.hapyl.hariant.element.anomaly;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstance;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.term.EnumTerminology;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public final class ElementalAnomalyShock extends ElementalAnomalyImpl {
    
    @DisplayField private final Decimal energyDrainOfMaxEnergy = Decimal.ofPercentage(30);
    @DisplayField private final Decimal energyPlayerTransferPercentOfDrainedEnergy = Decimal.ofPercentage(50);
    
    private final double explosionRadius = 3;
    private final double baseDamage = 30;
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.create(
            Key.ofString("shocked"),
            Component.text("Shocked"),
            DeathMessage.createWithDefaultKiller("{player} was shocked to death")
    );
    
    ElementalAnomalyShock() {
        super(Key.ofString("shock"), Component.text("Shock"), ElementType.ELECTRIC);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Creates an explosion in small "))
                         .append(EnumTerminology.AREA_OF_EFFECT)
                         .append(Component.text(" that deals "))
                         .append(ElementType.ELECTRIC.asComponentDamage())
                         .append(Component.text(" and drains "))
                         .append(energyDrainOfMaxEnergy)
                         .append(Component.text(" of max "))
                         .append(UltimateResourceType.ENERGY)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("If the source of "))
                         .append(this.getName().style(ElementType.ELECTRIC.getStyle()))
                         .append(Component.text(" is a player, transfer "))
                         .append(energyPlayerTransferPercentOfDrainedEnergy)
                         .append(Component.text(" of total "))
                         .append(UltimateResourceType.ENERGY)
                         .append(Component.text(" drained."))
        );
    }
    
    @Override
    public void trigger(@NotNull HariantEntity entity, @Nullable HariantEntity source) {
        final Iterator<HariantEntity> iterator = entity.collectNearbyEntities(explosionRadius)
                                                       .filter(_entity -> source == null || source.canAffect(_entity))
                                                       .iterator();
        
        final double damage = calculateDamage(source);
        final DamageSource damageSource = DamageSource.builder(damageSourceIdentity, damage)
                                                      .source(source)
                                                      .elementType(ElementType.ELECTRIC)
                                                      .damageType(DamageType.ANOMALY)
                                                      .build();
        
        double totalEnergyDrained = 0;
        
        while (iterator.hasNext()) {
            final HariantEntity affectedEntity = iterator.next();
            
            affectedEntity.damage(damageSource);
            
            // Decrement energy if entity is a player
            if (affectedEntity instanceof HariantPlayer player) {
                final TalentUltimate ultimateTalent = player.getHero().getUltimateTalent();
                final UltimateResourceType ultimateResourceType = ultimateTalent.getUltimateResourceType();
                
                if (ultimateResourceType != UltimateResourceType.ENERGY) {
                    continue;
                }
                
                final double ultimateDrain = Math.min(player.getUltimateResource(), ultimateTalent.getMaximumCost() * energyDrainOfMaxEnergy.doubleValue());
                totalEnergyDrained += ultimateDrain;
                
                player.decrementUltimateResource(ultimateDrain);
                
                // Fx
                player.playWorldSound(Sound.BLOCK_CHAIN_BREAK, 0.5f);
                player.playWorldSound(Sound.BLOCK_CHAIN_BREAK, 0.75f);
                player.playWorldSound(Sound.BLOCK_CHAIN_BREAK, 2.0f);
            }
        }
        
        // Transfer energy if the source is a player
        if (totalEnergyDrained > 0 && source instanceof HariantPlayer player && player.getHero().getUltimateTalent().getUltimateResourceType() == UltimateResourceType.ENERGY) {
            player.incrementUltimateResource(totalEnergyDrained * energyPlayerTransferPercentOfDrainedEnergy.doubleValue());
            
            // Fx
            player.playSound(Sound.BLOCK_BEACON_POWER_SELECT, 1.25f);
        }
        
        // Fx
        final Location location = entity.getMidpointLocation();
        
        entity.spawnWorldParticle(location, Particle.WAX_ON, 50, 0, 0, 0, 100f);
        entity.playWorldSound(location, Sound.ENTITY_COPPER_GOLEM_DEATH, 1.25f);
    }
    
    public double calculateDamage(@Nullable HariantEntity source) {
        if (source == null) {
            return baseDamage;
        }
        
        final AttributesInstance attributes = source.getAttributes();
        final double attack = attributes.get(AttributeType.ATTACK);
        final double elementalMastery = attributes.get(AttributeType.ELEMENTAL_MASTERY);
        
        return baseDamage * (1 + (attack / 500 + elementalMastery / 1000));
    }
    
}