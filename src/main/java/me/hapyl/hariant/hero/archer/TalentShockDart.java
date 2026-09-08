package me.hapyl.hariant.hero.archer;

import me.hapyl.eterna.module.math.geometry.Geometry;
import me.hapyl.eterna.module.math.geometry.Quality;
import me.hapyl.eterna.module.particle.ParticleBuilder;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.WarningType;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DamageSourceImpl;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.handler.HariantDamageProjectile;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.HariantTask;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.term.EnumTerminology;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Stream;

public final class TalentShockDart extends Talent {
    
    private final @DisplayField AttributeScaling arrowDamage = AttributeScaling.create(AttributeType.ATTACK, 60.3);
    private final @DisplayField AttributeScaling explosionMaxDamage = AttributeScaling.create(AttributeType.ATTACK, 293.25);
    
    private final @DisplayField Decimal explosionRadius = Decimal.ofValue(4.0);
    private final @DisplayField Decimal explosionDelay = Decimal.ofSeconds(1.0f);
    
    private final @DisplayField Decimal elementApplication = Decimal.ofValue(250);
    
    private final ParticleBuilder particleWindup = ParticleBuilder.dustColorTransition(Color.fromRGB(235, 224, 169), Color.fromRGB(224, 211, 141), 1);
    private final ParticleBuilder particleExplosion = ParticleBuilder.dustColorTransition(Color.fromRGB(242, 204, 97), Color.fromRGB(252, 186, 3), 1);
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.createOfNamed(
            this,
            Key.ofString("shock_dart_damage_source"),
            DeathMessage.create("{player} was shocked to death [by {killer}]")
    );
    
    public TalentShockDart(@NotNull Key key) {
        super(key, Component.text("Shock Dart"), Icon.ofMaterial(Material.SPECTRAL_ARROW));
        
        this.setCooldownSeconds(6);
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Shoot an arrow infused with "))
                         .append(Component.text("shocking", Colors.ELEMENT_ELECTRIC, TextDecoration.ITALIC))
                         .append(Component.text(" power in front of you."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Upon hit, the arrow charges and explodes, dealing "))
                         .append(ElementType.ELECTRIC.asComponentDamage())
                         .append(Component.text(" in small "))
                         .append(EnumTerminology.AREA_OF_EFFECT)
                         .append(Component.text("."))
        );
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        player.launchProjectile(Arrow.class, null, ShockDartProjectile::new);
        
        // Fx
        player.playWorldSound(Sound.ENTITY_ARROW_SHOOT, 2.0f);
        player.playWorldSound(Sound.ENTITY_BEE_STING, 0.75f);
        
        return Response.ok();
    }
    
    private class ShockDartProjectile extends HariantDamageProjectile {
        
        private final double explosionMaxDamage;
        
        ShockDartProjectile(@NotNull Projectile projectile, @NotNull HariantEntity shooter) {
            super(projectile, shooter, new ShockDartArrowDamageSource(shooter, arrowDamage.getScaledValue(shooter)));
            
            this.explosionMaxDamage = TalentShockDart.this.explosionMaxDamage.getScaledValue(shooter);
        }
        
        @Override
        public void onLaunch() {
            super.onLaunch();
            
            // Arrow fx
            new HariantTask(Scheduler.ofTimer(1)) {
                @Override
                public void run() {
                    if (projectile.isDead()) {
                        this.cancel();
                        return;
                    }
                    
                    getShooter().spawnWorldParticle(getLocation(), Particle.ELECTRIC_SPARK, 1, 0.1, 0.1, 0.1, 0.25f);
                }
            };
        }
        
        @Override
        public void onHit(@Nullable HariantEntity entity, @Nullable Block block) {
            super.onHit(entity, block);
            
            final HariantEntity shooter = getShooter();
            
            final double explosionRadiusSquared = explosionRadius.doubleValueSquared();
            final int explosionDelayInTicks = explosionDelay.intValue();
            
            final Location location = this.getLocation();
            
            new HariantTickingTask(Scheduler.ofTimer(1)) {
                @Override
                public void run(int tick) {
                    // Create explosion
                    final Stream<HariantEntity> entitiesInRange = collectNearbyEntities(explosionRadius.doubleValue()).filter(shooter::canAffect);
                    
                    if (tick > explosionDelayInTicks) {
                        entitiesInRange.forEach(entity -> {
                            final double distanceSquared = entity.distanceToSquared(ShockDartProjectile.this);
                            
                            if (distanceSquared <= explosionRadiusSquared) {
                                entity.damage(new ShockDartExplosionDamageSource(shooter, explosionMaxDamage * (1 - distanceSquared / explosionRadiusSquared)));
                            }
                        });
                        
                        // Fx
                        Geometry.drawSphere(location, explosionRadius.doubleValue(), Quality.VERY_HIGH, particleExplosion::display);
                        
                        shooter.playWorldSound(location, Sound.ENCHANT_THORNS_HIT, 2.0f);
                        
                        this.cancel();
                        return;
                    }
                    
                    // Send danger to entities in range
                    entitiesInRange.forEach(entity -> {
                        final boolean willTakeDamage = entity.distanceToSquared(ShockDartProjectile.this) <= explosionRadiusSquared;
                        
                        entity.showWarning(willTakeDamage ? WarningType.DANGER : WarningType.WARNING, 2);
                    });
                }
                
                @Override
                public void onCancel() {
                    PlayerLib.stopSound(Sound.ENTITY_LIGHTNING_BOLT_THUNDER);
                }
            };
            
            // Fx
            Geometry.drawSphere(location, explosionRadius.doubleValue(), Quality.VERY_HIGH, particleWindup::display);
            
            getShooter().playWorldSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2.0f);
        }
        
    }
    
    public class ShockDartArrowDamageSource extends DamageSourceArcherTalent {
        
        ShockDartArrowDamageSource(@NotNull HariantEntity attacker, double damage) {
            super(damageSourceIdentity, attacker, damage, 0);
        }
        
    }
    
    public class ShockDartExplosionDamageSource extends DamageSourceImpl {
        
        ShockDartExplosionDamageSource(@NotNull HariantEntity attacker, double damage) {
            super(damageSourceIdentity, attacker, DamageType.TALENT, ElementType.ELECTRIC, DamageComponents.ofCommon(), Set.of(), damage, elementApplication.doubleValue());
        }
        
    }
    
}
