package me.hapyl.hariant.hero.nyx;

import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScalingSingle;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.element.ElementalAnomalySourceImpl;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.WarningType;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.util.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.ultimate.ChargeLevel;
import me.hapyl.hariant.talent.ultimate.TalentUltimateOvercharge;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.task.HariantTask;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.util.Arithmetic;
import me.hapyl.hariant.util.BoundingBoxBlueprint;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.Models;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class TalentImpalement extends TalentUltimateOvercharge {
    
    private static final Component BULLET = Component.text("› ", Colors.DARK_GRAY);
    
    private final @DisplayField Decimal radius = Decimal.ofValue(4);
    private final @DisplayField Decimal radiusOvercharged = Decimal.ofValue(5);
    
    private final @DisplayField AttributeScalingArithmetic damage = new AttributeScalingArithmetic(AttributeType.ATTACK, 54);
    private final @DisplayField AttributeScalingArithmetic damageOvercharged = new AttributeScalingArithmetic(AttributeType.ATTACK, 81);
    
    private final @DisplayField Decimal duration = Decimal.ofSeconds(2f);
    private final @DisplayField Decimal durationOvercharged = Decimal.ofSeconds(2.8f);
    
    private final @DisplayField Decimal elementalApplication = Decimal.ofElementalApplication(ElementType.AETHER, 100);
    private final @DisplayField Decimal elementalApplicationOvercharged = Decimal.ofElementalApplication(ElementType.AETHER, 125);
    
    private final @DisplayField Decimal portalOpeningDuration = Decimal.ofSeconds(0.8f);
    private final @DisplayField Decimal portalSpearPeriod = Decimal.ofSeconds(0.1f);
    
    private final @DisplayField BoundingBoxBlueprint portalBoundingBox = BoundingBoxBlueprint.define(radius.doubleValue(), 10, radius.doubleValue());
    
    private final DamageSourceIdentity damageSourceImpalement = DamageSourceIdentity.createOfNamed(this, Key.ofString("impalement_damage_source"), DeathMessage.create("{player} was impaled to death [by {killer}]"));
    private final int portalClosingDuration = 10;
    
    public TalentImpalement(@NotNull Key key) {
        super(key, Component.text("Impalement"), Icon.ofMaterial(Material.DRIED_KELP), UltimateResourceType.ENERGY, 60, 100);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Open a "))
                         .append(Component.text("Void Portal", Colors.VOID))
                         .append(Component.text(" in front of you."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("After a short casting time, "))
                         .append(Component.text("spears", Colors.GOLD))
                         .append(Component.text(" will rise from the "))
                         .append(Component.text("portal", Colors.VOID))
                         .append(Component.text(", dealing rapid "))
                         .append(ElementType.AETHER.asComponentAreaOfEffectDamage())
                         .append(Component.text(" and applying "))
                         .appendNewline()
                         .append(ElementType.AETHER)
                         .append(Component.text(" anomaly."))
        );
    }
    
    @Override
    public @NotNull Component overchargeDescription() {
        return Component.empty()
                        .append(bullet(
                                Component.empty()
                                         .append(Component.text("Increases the radius by "))
                                         .append(differenceInPercent(radius, radiusOvercharged, Colors.SUCCESS))
                                         .append(Component.text("."))
                        ))
                        .appendNewline()
                        .append(bullet(
                                Component.empty()
                                         .append(Component.text("Increases the damage by "))
                                         .append(differenceInPercent(damage, damageOvercharged, Colors.ERROR))
                                         .append(Component.text("."))
                        ))
                        .appendNewline()
                        .append(bullet(
                                Component.empty()
                                         .append(Component.text("Increases the duration by "))
                                         .append(differenceInPercent(duration, durationOvercharged, Colors.NUMBER))
                                         .append(Component.text("."))
                        ))
                        .appendNewline()
                        .append(bullet(
                                Component.empty()
                                         .append(Component.text("Increases the elemental application by "))
                                         .append(differenceInPercent(elementalApplication, elementalApplicationOvercharged, Colors.ATTRIBUTE_ELEMENTAL_MASTERY))
                                         .append(Component.text("."))
                        ))
                        .appendNewline()
                        .append(bullet(
                                Component.empty()
                                         .append(Component.text("Adds a final slash, that "))
                                         .append(Component.text("forcefully", Colors.GRAY, TextDecoration.UNDERLINED))
                                         .append(Component.text(" triggers "))
                                         .append(ElementalAnomalyType.INTANGIBILITY)
                                         .append(Component.text("."))
                        ));
    }
    
    @Override
    public @NotNull Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, @NotNull ChargeLevel chargeLevel) {
        return Executable.execute(() -> {
            final Location location = LocationHelper.anchor(player.getLocationInFront(2));
            location.setYaw(0);
            location.setPitch(0);
            
            new Impalement(player, location, chargeLevel);
        });
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    private static <T extends Arithmetic<T>> @NotNull Component differenceInPercent(@NotNull T a, @NotNull T b, @NotNull TextColor textColor) {
        return Component.text("%.0f%%".formatted((b.divide(a) - 1) * 100), textColor);
    }
    
    private static @NotNull Component bullet(@NotNull Component component) {
        return Component.empty().append(BULLET).append(component);
    }
    
    public class Impalement extends HariantTickingTask implements EntityCollector {
        
        private static final Color OUTLINE_COLOR = Color.fromRGB(46, 63, 191);
        private static final Particle.DustOptions DUST_PURPLE = new Particle.DustOptions(Color.fromRGB(90, 10, 140), 0.8f);
        
        private final HariantPlayer player;
        private final Location location;
        private final ChargeLevel chargeLevel;
        private final TextDisplay portal;
        
        private final double portalRadius;
        
        private final int portalOpeningDelay;
        private final int portalDurationWithOpeningDelay;
        private final int portalClosingDelayWait;
        private final int portalClosingDelay;
        
        private final DamageSource damageSource;
        private final BoundingBox boundingBox;
        
        Impalement(@NotNull HariantPlayer player, @NotNull Location location, @NotNull ChargeLevel chargeLevel) {
            super(Scheduler.ofTimer(1));
            
            this.player = player;
            this.location = location;
            this.chargeLevel = chargeLevel;
            this.portal = player.getWorld().spawn(location, TextDisplay.class, self -> {
                self.setShadowStrength(64);
                self.setShadowRadius(0);
            });
            
            this.portalRadius = chargeLevel.either(radius, radiusOvercharged).doubleValue();
            this.portalOpeningDelay = portalOpeningDuration.intValue();
            this.portalDurationWithOpeningDelay = chargeLevel.either(duration, durationOvercharged).intValue() + portalOpeningDelay;
            this.portalClosingDelayWait = portalDurationWithOpeningDelay + 10; // Add a small window where portal stays open and does nothing
            this.portalClosingDelay = portalClosingDuration + portalClosingDelayWait;
            
            this.damageSource = new DamageSourceImpalement(
                    player,
                    chargeLevel.either(damage, damageOvercharged).getScaledValue(player),
                    chargeLevel.either(elementalApplication, elementalApplicationOvercharged).doubleValue()
            );
            
            this.boundingBox = portalBoundingBox.create(location);
            
            // Fx
            player.playWorldSound(location, Sound.ENTITY_WARDEN_ROAR, 1.25f);
        }
        
        @Override
        public void onCancel() {
            portal.remove();
        }
        
        @Override
        public void run(int tick) {
            WarningType warningType = null;
            
            // Open the portal
            if (tick <= portalOpeningDelay) {
                warningType = WarningType.WARNING;
                
                portal.setShadowRadius((float) ((double) tick / portalOpeningDelay * portalRadius));
                this.spawnAshParticles(100);
            }
            // Summons spears
            else if (tick <= portalDurationWithOpeningDelay) {
                warningType = WarningType.DANGER;
                
                if (modulo(portalSpearPeriod)) {
                    this.summonSpear();
                }
                
                // Execute final slash
                if (tick == portalDurationWithOpeningDelay - 1 && chargeLevel.isOvercharged()) {
                    this.executeFinalSlash();
                }
            }
            // Wait a little bit and do nothing
            else if (tick <= portalClosingDelayWait) {
                this.spawnAshParticles(50);
            }
            // Close portal
            else if (tick <= portalClosingDelay) {
                portal.setShadowRadius((float) (portalRadius - ((double) (tick - portalClosingDelayWait) / (portalClosingDelay - portalClosingDelayWait) * portalRadius)));
            }
            else {
                this.cancel();
            }
            
            // Display warning
            if (modulo(2) && warningType != null) {
                final WarningType finalWarningType = warningType;
                
                this.collectNearbyEntities(boundingBox)
                    .filter(player::canAffect)
                    .forEach(entity -> {
                        entity.showWarning(finalWarningType, 5);
                    });
            }
        }
        
        public void executeFinalSlash() {
            collectNearbyEntities(boundingBox)
                    .filter(player::canAffect)
                    .forEach(entity -> entity.triggerAnomaly(new ImpalementAnomalySource(player), true));
            
            // Fx
            final Location location = LocationHelper.copyOfPosition(this.location).add(0, 1, 0);
            
            
            player.spawnWorldParticle(location, Particle.SONIC_BOOM, 1, 0.0f);
            player.spawnWorldParticle(location, Particle.DUST, 50, 1, 1.5, 1, 0.2f, DUST_PURPLE);
            
            player.playWorldSound(location, Sound.ENTITY_WARDEN_SONIC_BOOM, 0.6f);
            player.playWorldSound(location, Sound.ENTITY_WARDEN_DEATH, 2.0f);
        }
        
        public void summonSpear() {
            final double x = player.random.nextSignedDouble(portalRadius - 1);
            final double z = player.random.nextSignedDouble(portalRadius - 1);
            
            final Location locationFrom = LocationHelper.copyOfPosition(location).add(x, -1, z);
            final Location locationTo = LocationHelper.copyOfPosition(location).add(x, player.random.nextDouble(6, 8), z);
            
            // Deal damage
            collectNearbyEntities(boundingBox)
                    .filter(player::canAffect)
                    .forEach(entity -> {
                        entity.damage(damageSource);
                    });
            
            // Fx
            LocationHelper.offset(location, x, 0, z, () -> {
                final DisplayEntity display = Models.IMPALEMENT_SPEAR.spawn(locationFrom);
                
                HariantTask.later(() -> display.teleport(locationTo), 2);
                
                player.delegate(HariantTask.remove(() -> display, 5), DelegateType.PERSISTENT);
            });
            
            player.playWorldSound(location, Sound.ITEM_FLINTANDSTEEL_USE, 0.75f);
            player.playWorldSound(location, Sound.ENTITY_BLAZE_HURT, 1.5f);
        }
        
        @Override
        public @NotNull Location getLocation() {
            return location;
        }
        
        @Override
        public @NotNull Color outlineColor() {
            return OUTLINE_COLOR;
        }
        
        private void spawnAshParticles(int amount) {
            player.spawnWorldParticle(location, Particle.ASH, amount, portalRadius * 0.8, 0.1, portalRadius * 0.8, 0.02f);
        }
    }
    
    private class DamageSourceImpalement extends DamageSourceImpl {
        public DamageSourceImpalement(@NotNull HariantPlayer player, double damage, double elementalApplication) {
            super(damageSourceImpalement, player, DamageType.ULTIMATE, ElementType.AETHER, DamageComponents.ofCommon(), Set.of(), damage, elementalApplication);
        }
    }
    
    public static class AttributeScalingArithmetic extends AttributeScalingSingle implements Arithmetic<AttributeScalingArithmetic> {
        
        private final double scalingPercent;
        
        AttributeScalingArithmetic(@NotNull AttributeType attributeType, double scalingPercent) {
            super(attributeType, scalingPercent);
            
            this.scalingPercent = scalingPercent;
        }
        
        @Override
        public double add(@NotNull TalentImpalement.AttributeScalingArithmetic that) {
            return this.scalingPercent + that.scalingPercent;
        }
        
        @Override
        public double subtract(@NotNull TalentImpalement.AttributeScalingArithmetic that) {
            return this.scalingPercent - that.scalingPercent;
        }
        
        @Override
        public double multiply(@NotNull TalentImpalement.AttributeScalingArithmetic that) {
            return this.scalingPercent * that.scalingPercent;
        }
        
        @Override
        public double divide(@NotNull TalentImpalement.AttributeScalingArithmetic that) {
            if (this.scalingPercent == 0 || that.scalingPercent == 0) {
                return 0;
            }
            
            return this.scalingPercent / that.scalingPercent;
        }
        
    }
    
    public static class ImpalementAnomalySource extends ElementalAnomalySourceImpl {
        
        ImpalementAnomalySource(@Nullable HariantEntity source) {
            super(ElementalAnomalyType.INTANGIBILITY, source);
        }
        
    }
    
    
}