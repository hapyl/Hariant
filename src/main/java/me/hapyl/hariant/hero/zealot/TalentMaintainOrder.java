package me.hapyl.hariant.hero.zealot;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.block.display.DisplayModel;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.math.geometry.Geometry;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.WarningType;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.util.BoundingBoxBlueprint;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Stream;

public final class TalentMaintainOrder extends TalentUltimate {
    
    private final @DisplayField AttributeScaling damageLanding = AttributeScaling.create(AttributeType.ATTACK, 227.7);
    private final @DisplayField AttributeScaling damageFerocity = AttributeScaling.create(AttributeType.ATTACK, 34.8);
    
    private final @DisplayField Decimal landingDuration = Decimal.ofSeconds(0.75f);
    private final @DisplayField Decimal impactDuration = Decimal.ofSeconds(0.2f);
    
    private final @DisplayField Decimal radius = Decimal.ofValue(5);
    private final @DisplayField BoundingBoxBlueprint landingBoundingBox = BoundingBoxBlueprint.define(radius.doubleValue(), 8, radius.doubleValue());
    
    private final @DisplayField Decimal ferocityStrikes = Decimal.ofValue(10);
    
    private final double landingYOffset = 10;
    private final double distanceFromEyes = 4;
    
    private final DisplayModel model = BDEngine.parse(
            "/summon block_display ~-0.5 ~-0.5 ~-0.5 {Passengers:[{id:\"minecraft:item_display\",item:{id:\"minecraft:golden_sword\",Count:1},item_display:\"none\",transformation:[2.6043f,3.5194f,-2.4148f,-0.2500f,3.4151f,-3.4151f,-1.2941f,1.2500f,-2.5602f,-0.9753f,-4.1826f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
    );
    
    private final DamageSourceIdentity damageSourceIdentityLanding = DamageSourceIdentity.create(
            this,
            DeathMessage.create("{player} was killed by a [{killer}'s] massive sword")
    );
    
    private final DamageSourceIdentity damageSourceIdentityFerocity = DamageSourceIdentity.create(
            this,
            DeathMessage.create("[{killer}] has maintained order over {player}")
    );
    
    public TalentMaintainOrder(@NotNull Key key) {
        super(key, Component.text("Maintain Order"), Icon.ofMaterial(Material.GOLDEN_SWORD), UltimateResourceType.ENERGY, 60);
        
        setDurationSeconds(1);
        setCooldownSeconds(30);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Command a "))
                         .append(Component.text("giant sword", Colors.YELLOW))
                         .append(Component.text(" to fall down from the sky."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Upon landing, the sword deals "))
                         .appendNewline()
                         .append(ElementType.AETHER.asComponentAreaOfEffectDamage())
                         .append(Component.text(" and "))
                         .append(Component.text("forcefully", Style.style(TextDecoration.UNDERLINED)))
                         .append(Component.text(" triggers "))
                         .append(ferocityStrikes)
                         .append(Component.text(" instances of "))
                         .append(AttributeType.FEROCITY.getPrefixStyled())
                         .append(Component.text(" ferocity", AttributeType.FEROCITY.getStyle()))
                         .append(Component.text("."))
        );
    }
    
    @Override
    public @NotNull Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, double consumedResource) {
        final Location location = LocationHelper.anchor(player.getLocationInFrontFromEyes(distanceFromEyes));
        
        return Executable.execute(() -> new MaintainOrder(player, location.add(0, 0.1, 0)));
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    public class MaintainOrder extends HariantTickingTask implements EntityCollector {
        
        private final HariantPlayer player;
        private final Location location;
        private final DisplayEntity displayEntity;
        
        private final int duration;
        private final int durationWithImpact;
        
        private final DamageSource damageSourceLanding;
        private final DamageSource damageSourceFerocity;
        
        public MaintainOrder(@NotNull HariantPlayer player, @NotNull Location location) {
            super(Scheduler.ofTimer());
            
            this.player = player;
            this.location = location;
            this.displayEntity = model.spawnInterpolated(LocationHelper.copyOfPosition(location).add(0, landingYOffset, 0));
            this.duration = getDuration();
            this.durationWithImpact = duration + impactDuration.intValue();
            this.damageSourceLanding = new DamageSourceMaintainOrderLanding(player);
            this.damageSourceFerocity = new DamageSourceMaintainOrderFerocity(player);
        }
        
        @Override
        public void run(int tick) {
            // Sword landed, deal AoE landing DMG
            if (tick == duration) {
                stream().forEach(entity -> {
                    entity.damage(damageSourceLanding);
                });
                
                // Fx
                player.playWorldSound(location, Sound.ITEM_SHIELD_BREAK, 0.75f);
            }
            else if (tick > getDuration()) {
                final Stream<HariantEntity> entities = stream();
                
                // Explode the sword
                if (tick >= durationWithImpact) {
                    entities.forEach(entity -> {
                        // Execute ferocity
                        entity.damageFerocity(
                                new DamageInstance(entity, damageSourceFerocity),
                                ferocityStrikes.intValue(),
                                true
                        );
                    });
                    
                    // Fx
                    player.playWorldSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.25f);
                    player.spawnWorldParticle(location.add(0, 1.5, 0), Particle.CRIT, 100, 0.25, 0.5, 0.25, 1.0f);
                    
                    this.cancel();
                    return;
                }
                
                // Send danger
                entities.forEach(entity -> entity.showWarning(WarningType.DANGER, 5));
            }
            else {
                // Teleport sword downwards
                final Location displayLocation = displayEntity.getLocation();
                
                final double t = (double) tick / (getDuration() - 1);
                final double y = landingYOffset * t * t * t;
                
                displayLocation.setY(location.getY() + landingYOffset - y);
                
                displayEntity.teleport(displayLocation);
                
                // Send warning
                if (modulo(4)) {
                    stream().forEach(entity -> entity.showWarning(WarningType.WARNING, 5));
                }
                
                // Fx
                Geometry.drawPolygon(location, 10, radius.doubleValue(), 0.5, _location -> player.spawnWorldParticle(_location, Particle.CRIT, 1, 0));
            }
            
        }
        
        @Override
        public void onCancel() {
            displayEntity.remove();
        }
        
        @Override
        public @NotNull Location getLocation() {
            return location;
        }
        
        private @NotNull Stream<HariantEntity> stream() {
            return collectNearbyEntities(landingBoundingBox.create(location)).filter(player::canAffect);
        }
    }
    
    private class DamageSourceMaintainOrderLanding extends DamageSourceImpl {
        DamageSourceMaintainOrderLanding(@NotNull HariantEntity source) {
            super(damageSourceIdentityLanding, source, DamageType.ULTIMATE, ElementType.AETHER, DamageComponent.ofCommon(), Set.of(), damageLanding.getScaledValue(source), 0);
        }
    }
    
    private class DamageSourceMaintainOrderFerocity extends DamageSourceImpl {
        DamageSourceMaintainOrderFerocity(@NotNull HariantEntity source) {
            super(damageSourceIdentityFerocity, source, DamageType.ULTIMATE, ElementType.AETHER, DamageComponent.ofCommon(), Set.of(), damageFerocity.getScaledValue(source), 0);
        }
    }
    
}