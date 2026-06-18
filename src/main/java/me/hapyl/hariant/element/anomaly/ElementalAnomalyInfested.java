package me.hapyl.hariant.element.anomaly;

import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.WarningType;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ElementalAnomalyInfested extends ElementalAnomalyImpl {
    
    @DisplayField private final Decimal defenseReduction = Decimal.ofPercentage(40);
    @DisplayField private final Decimal defenseReductionDuration = Decimal.ofSeconds(10);
    
    private final int cloudDuration = Tick.fromSeconds(8);
    private final int cloudDamagePeriod = 15;
    
    private final double cloudRadius = 3;
    private final double cloudDamage = 2;
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.create(
            Key.ofString("toxic_cloud"),
            Component.text("Toxic Cloud"),
            DeathMessage.create("{player} was poisoned to death [by {killer}]")
    );
    
    ElementalAnomalyInfested() {
        super(Key.ofString("infested"), Component.text("Infested"), ElementType.TOXIC);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Creates a toxic field that deals continuous "))
                         .append(ElementType.TOXIC.asComponentDamage())
                         .append(Component.text("."))
                         .appendNewline()
                         .append(Component.text("The toxic damage cannot kill.", Colors.DARK_GRAY))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Enemies, who enter the field have their "))
                         .append(AttributeType.DEFENSE)
                         .append(Component.text(" reduced by "))
                         .append(defenseReduction)
                         .append(Component.text(" for "))
                         .append(defenseReductionDuration)
                         .append(Component.text("."))
        );
    }
    
    @Override
    public void trigger(@NotNull HariantEntity entity, @Nullable HariantEntity source) {
        // Calculate cloud duration and damage
        final int duration = calculateDuration(source);
        final double damage = calculateDamage(source);
        
        final Location location = entity.getLocation().add(0, 0.25, 0);
        final DamageSource damageSource = DamageSource.builder(damageSourceIdentity, damage)
                                                      .source(source)
                                                      .elementType(ElementType.TOXIC)
                                                      .damageType(DamageType.ANOMALY)
                                                      .damageFlag(DamageFlag.CANNOT_KILL)
                                                      .build();
        
        
        new HariantTickingTask(Scheduler.ofTimer()) {
            @Override
            public void run(int tick) {
                if (tick > duration) {
                    this.cancel();
                    return;
                }
                
                // Affect entities
                entity.collectNearbyEntities(cloudRadius)
                      .filter(entity -> source == null || source.canAffect(entity))
                      .forEach(entity -> {
                          // Deal damage
                          if (tick % cloudDamagePeriod == 0) {
                              entity.damage(damageSource);
                              
                              // Apply modifier
                              entity.getAttributes().addModifier(new AttributeModifierInfested(source != null ? source : entity));
                          }
                          
                          // Display warning
                          entity.showWarning(WarningType.DANGER, 5);
                      });
                
                // Fx
                final double radians = Math.toRadians(tick * 5);
                
                final double x = Math.sin(radians) * cloudRadius;
                final double y = Math.sin(Math.toRadians(tick * 10)) * 0.2;
                final double z = Math.cos(radians) * cloudRadius;
                
                LocationHelper.offset(location, x, y, z, this::drawParticle);
                LocationHelper.offset(location, -x, y, -z, this::drawParticle);
                
                // Inner fx
                final double offset = Math.min(radians, cloudRadius * 0.5);
                
                entity.spawnWorldParticle(location, Particle.TRIAL_OMEN, 10, offset, offset, offset, 0.125f);
            }
            
            private void drawParticle(@NotNull Location location) {
                entity.spawnWorldParticle(
                        location,
                        Particle.DUST_COLOR_TRANSITION,
                        10,
                        0, 0, 0,
                        0.25f,
                        new Particle.DustTransition(
                                Color.fromRGB(161, 237, 128),
                                Color.fromRGB(40, 125, 4),
                                1
                        )
                );
            }
        };
    }
    
    public int calculateDuration(@Nullable HariantEntity source) {
        if (source == null) {
            return cloudDuration;
        }
        
        final double elementalMastery = source.getAttributes().get(AttributeType.ELEMENTAL_MASTERY);
        
        return (int) (cloudDuration * (1 + elementalMastery / 1000));
    }
    
    public double calculateDamage(@Nullable HariantEntity source) {
        if (source == null) {
            return cloudDamage;
        }
        
        final double elementalMastery = source.getAttributes().get(AttributeType.ELEMENTAL_MASTERY);
        
        return cloudDamage * (1 + elementalMastery / 50);
    }
    
    public class AttributeModifierInfested extends AttributeModifier {
        AttributeModifierInfested(@NotNull HariantEntity applier) {
            super(ElementalAnomalyInfested.this.getKey(), ElementalAnomalyInfested.this.getName(), applier, defenseReductionDuration.intValue());
            
            of(AttributeType.DEFENSE, AttributeModifierType.ADDITIVE, -defenseReduction.doubleValue());
        }
    }
    
}
