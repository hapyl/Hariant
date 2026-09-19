package me.hapyl.hariant.element.anomaly;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.Attributable;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.element.ElementalAnomalySource;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import me.hapyl.hariant.event.HariantHealEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class ElementalAnomalyBleed extends ElementalAnomalyImpl implements Listener {
    
    private final int bleedDuration = Tick.fromSeconds(4);
    private final int bleedPeriod = Tick.fromSeconds(0.75f);
    
    private final double bleedDamage = 20;
    
    private final Component componentBleeding = Component.empty()
                                                         .append(Component.text("\uD83E\uDE78 ", Colors.EFFECT_BLEED, TextDecoration.BOLD))
                                                         .append(Component.text("You are bleeding!", Colors.ERROR));
    
    private final Component componentNoLongerBleeding = Component.empty()
                                                                 .append(Component.text("\uD83E\uDE78 ", Colors.EFFECT_BLEED, TextDecoration.BOLD))
                                                                 .append(Component.text("The bleeding has stopped!", Colors.SUCCESS));
    
    private final Particle.DustTransition dustTransition = new Particle.DustTransition(
            org.bukkit.Color.fromRGB(125, 1, 20),
            org.bukkit.Color.fromRGB(194, 14, 41),
            1
    );
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.createOfNamed(
            this, Key.ofString("bleed_damage_source"),
            DeathMessage.createWithDefaultKiller("{player} bled to death")
    );
    
    ElementalAnomalyBleed() {
        super(Key.ofString("bleed"), ElementType.PHYSICAL, Component.text("Bleed"), new ElementalPotency(1.2, 0.2));
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Causes the affected entity to start bleeding, taking "))
                         .append(ElementType.PHYSICAL.asComponentDamage())
                         .append(Component.text(" over time."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("A single instance of healing clears this effect."))
        );
    }
    
    @EventHandler
    public void handleHariantHealEvent(HariantHealEvent ev) {
        final HariantEntity entity = ev.getEntity();
        
        if (ev.getActualHealing() <= 0 || !entity.isElementalAnomalyActive(ElementalAnomalyType.BLEED)) {
            return;
        }
        
        entity.endElementalAnomaly(ElementalAnomalyType.BLEED);
        ev.setCancelled(true);
    }
    
    @Override
    public @NotNull ElementalAnomalyInstance newInstance(@NotNull ElementalAnomalySource anomalySource, @NotNull HariantEntity entity, @Nullable HariantEntity source) {
        final int duration = this.calculateBleedDuration(source);
        final double damage = this.calculateBleedDamage(source);
        
        return new ElementalAnomalyInstanceBleed(anomalySource, entity, source,  duration, damage);
    }
    
    public int calculateBleedDuration(@Nullable Attributable attributable) {
        if (attributable == null) {
            return bleedDuration;
        }
        
        return (int) (bleedDuration * (1 + attributable.getAttributes().get(AttributeType.ELEMENTAL_MASTERY) / 500));
    }
    
    public double calculateBleedDamage(@Nullable Attributable attributable) {
        if (attributable == null) {
            return bleedDamage;
        }
        
        final Attributes attributes = attributable.getAttributes();
        
        final double attack = attributes.get(AttributeType.ATTACK);
        final double elementalMastery = attributes.get(AttributeType.ELEMENTAL_MASTERY);
        
        return bleedDamage * (1 + (attack / 500 + elementalMastery / 1000));
    }
    
    public class ElementalAnomalyInstanceBleed extends ElementalAnomalyInstance {
        
        private final DamageSource damageSource;
        
        ElementalAnomalyInstanceBleed(@NotNull ElementalAnomalySource anomalySource, @NotNull HariantEntity entity, @Nullable HariantEntity source, int duration, double damage) {
            super(anomalySource, entity, source, duration);
            
            this.damageSource = new ElementalAnomalyBleedDamageSource(source, damage);
        }
        
        @Override
        public void tick() {
            super.tick();
            
            if (currentTick() % bleedPeriod == 0) {
                entity.damage(damageSource);
            }
            
            entity.spawnWorldParticle(entity.getLocation().add(0, 0.2, 0), Particle.DUST_COLOR_TRANSITION, 1, 0.2, 0.2, 0.2, 0.015f, dustTransition);
        }
        
        @Override
        public void onStart() {
            super.onStart();
            
            entity.sendMessage(componentBleeding);
            entity.playWorldSound(Sound.ENTITY_ZOMBIE_INFECT, 1.0f);
        }
        
        @Override
        public void onEnd() {
            super.onEnd();
            
            entity.sendMessage(componentNoLongerBleeding);
            entity.playWorldSound(Sound.ENTITY_HORSE_SADDLE, 1.25f);
        }
        
    }
    
    public class ElementalAnomalyBleedDamageSource extends DamageSourceImpl {
        
        ElementalAnomalyBleedDamageSource(@Nullable HariantEntity source, double damage) {
            super(damageSourceIdentity, source, DamageType.ANOMALY, ElementType.PHYSICAL, DamageComponents.ofAnomaly(), Set.of(), damage, 0);
        }
        
    }
    
    
}