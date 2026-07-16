package me.hapyl.hariant.element.anomaly;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstance;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.event.HariantHealEvent;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public final class ElementalAnomalyBleed extends ElementalAnomalyImpl implements Listener {
    
    private final Key modifierKey = Key.ofString("bleed");
    
    private final Decimal vitalityReduction = Decimal.ofAttribute(AttributeType.VITALITY, 50);
    
    private final int bleedDuration = Tick.fromSeconds(6);
    private final int bleedPeriod = Tick.fromSeconds(0.75f);
    
    private final double bleedDamage = 25;
    
    private final Component componentBleeding = Component.empty()
                                                         .append(Component.text("\uD83E\uDE78 ", Colors.EFFECT_BLEED, TextDecoration.BOLD))
                                                         .append(Component.text("You are bleeding!", Colors.ERROR));
    
    private final Component componentNoLongerBleeding = Component.empty()
                                                                 .append(Component.text("\uD83E\uDE78 ", Colors.EFFECT_BLEED, TextDecoration.BOLD))
                                                                 .append(Component.text("The bleeding has stopped!", Colors.SUCCESS));
    
    private final Particle.DustTransition dustTransition = new Particle.DustTransition(
            org.bukkit.Color.fromRGB(125, 1, 20),
            org.bukkit.Color.fromRGB(194, 14, 41),
            2
    );
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.create(
            this,
            DeathMessage.createWithDefaultKiller("{player} bled to death")
    );
    
    ElementalAnomalyBleed() {
        super(Key.ofString("bleed"), Component.text("Bleed"), ElementType.PHYSICAL);
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Causes the affected entity to start bleeding, taking "))
                         .append(ElementType.PHYSICAL.asComponentDamage())
                         .append(Component.text(" over time and reducing their "))
                         .append(AttributeType.VITALITY)
                         .append(Component.text(" by "))
                         .append(vitalityReduction)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("A single instance of healing clears this effect."))
        );
    }
    
    @EventHandler
    public void handleHariantHealEvent(HariantHealEvent ev) {
        final HariantEntity entity = ev.getEntity();
        final AttributesInstance attributes = entity.getAttributes();
        
        if (ev.getActualHealing() <= 0 || !attributes.removeModifier(modifierKey)) {
            return;
        }
        
        ev.setCancelled(true);
    }
    
    @Override
    public void trigger(@NotNull HariantEntity entity, @Nullable HariantEntity source) {
        final int duration = this.calculateBleedDuration(source);
        final double damage = this.calculateBleedDamage(source);
        
        entity.getAttributes().addModifier(new ElementalAnomalyBleedAttributeModifier(modifierKey, source != null ? source : entity, duration, damage));
    }
    
    @Override
    public boolean isAnomalyActive(@NotNull HariantEntity entity) {
        return entity.getAttributes().hasModifier(modifierKey);
    }
    
    public int calculateBleedDuration(@Nullable HariantEntity source) {
        if (source == null) {
            return bleedDuration;
        }
        
        return (int) (bleedDuration * (1 + source.getAttributes().get(AttributeType.ELEMENTAL_MASTERY) / 500));
    }
    
    public double calculateBleedDamage(@Nullable HariantEntity source) {
        if (source == null) {
            return bleedDamage;
        }
        
        final AttributesInstance attributes = source.getAttributes();
        
        final double attack = attributes.get(AttributeType.ATTACK);
        final double elementalMastery = attributes.get(AttributeType.ELEMENTAL_MASTERY);
        
        return bleedDamage * (1 + (attack / 500 + elementalMastery / 1000));
    }
    
    public class ElementalAnomalyBleedAttributeModifier extends AttributeModifier {
        
        private final DamageSource damageSource;
        
        ElementalAnomalyBleedAttributeModifier(@NotNull Key key, @NotNull HariantEntity applier, int duration, double damage) {
            super(key, ElementalAnomalyBleed.this.getName(), applier, duration);
            
            // Reduce vitality
            this.of(AttributeType.VITALITY, AttributeModifierType.FLAT, -vitalityReduction.doubleValue());
            
            // Create damage source
            this.damageSource = new ElementalAnomalyBleedDamageSource(applier, damage);
        }
        
        @Override
        public void onApply(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int duration) {
            entity.sendMessage(componentBleeding);
            entity.playWorldSound(Sound.ENTITY_ZOMBIE_INFECT, 1.0f);
        }
        
        @Override
        public void onRemove(@NotNull HariantEntity entity, @NotNull HariantEntity applier) {
            entity.sendMessage(componentNoLongerBleeding);
            entity.playWorldSound(Sound.ENTITY_HORSE_SADDLE, 1.25f);
        }
        
        @Override
        public void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick) {
            if (tick % bleedPeriod == 0) {
                entity.damage(damageSource);
            }
            
            // Fx always
            entity.spawnWorldParticle(entity.getMidpointLocation(), Particle.DUST_COLOR_TRANSITION, 1, 0.2, 0.2, 0.2, 0.015f, dustTransition);
        }
    }
    
    public class ElementalAnomalyBleedDamageSource extends DamageSourceImpl {
        ElementalAnomalyBleedDamageSource(@Nullable HariantEntity source, double damage) {
            super(
                    damageSourceIdentity,
                    source,
                    DamageType.ANOMALY,
                    ElementType.PHYSICAL,
                    List.of(DamageComponent.elemental()),
                    Set.of(),
                    damage,
                    0
            );
        }
    }
}