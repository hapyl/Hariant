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
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageSourceImpl;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantProjectileHitEvent;
import me.hapyl.hariant.handler.HariantProjectile;
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
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Stream;

public final class TalentShockDart extends Talent implements Listener {
    
    @DisplayField private final AttributeScaling arrowDamage = AttributeScaling.of(AttributeType.ATTACK, 67);
    @DisplayField private final AttributeScaling explosionMaxDamage = AttributeScaling.of(AttributeType.ATTACK, 345);
    
    @DisplayField private final Decimal explosionRadius = Decimal.ofValue(4.0);
    @DisplayField private final Decimal explosionDelay = Decimal.ofSeconds(1.2f);
    
    @DisplayField private final Decimal elementApplication = Decimal.ofValue(150);
    
    private final ParticleBuilder particleWindup = ParticleBuilder.dustColorTransition(Color.fromRGB(235, 224, 169), Color.fromRGB(224, 211, 141), 1);
    private final ParticleBuilder particleExplosion = ParticleBuilder.dustColorTransition(Color.fromRGB(242, 204, 97), Color.fromRGB(252, 186, 3), 1);
    
    private final DeathMessage deathMessage = DeathMessage.create("{player} was shocked to death [by {killer}]");
    
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
    
    @NotNull
    @Override
    public DeathMessage getDeathMessage() {
        return deathMessage;
    }
    
    @EventHandler
    public void handleHariantProjectileHitEvent(HariantProjectileHitEvent ev) {
        final HariantProjectile projectile = ev.getProjectile();
        final DamageSource damageSource = projectile.getDamageSource();
        
        if (!(damageSource instanceof ShockDartArrowDamageSource shockDartArrowDamageSource)) {
            return;
        }
        
        if (!(damageSource.getSource() instanceof HariantPlayer player)) {
            return;
        }
        
        final double explosionMaxDamage = shockDartArrowDamageSource.explosionMaxDamage;
        final double explosionRadiusSquared = explosionRadius.doubleValueSquared();
        final int explosionDelayInTicks = explosionDelay.intValue();
        
        final Location location = projectile.getLocation();
        
        new HariantTickingTask(Scheduler.ofTimer(1)) {
            @Override
            public void run(int tick) {
                // Create explosion
                final Stream<HariantEntity> entitiesInRange = projectile.collectNearbyEntities(explosionRadius.doubleValue()).filter(player::canAffect);
                
                if (tick > explosionDelayInTicks) {
                    entitiesInRange.forEach(entity -> {
                        final double distanceSquared = entity.distanceToSquared(projectile);
                        
                        if (distanceSquared <= explosionRadiusSquared) {
                            entity.damage(new ShockDartExplosionDamageSource(player, explosionMaxDamage * (1 - distanceSquared / explosionRadiusSquared)));
                        }
                    });
                    
                    // Fx
                    Geometry.drawSphere(location, explosionRadius.doubleValue(), Quality.VERY_HIGH, particleExplosion::display);
                    
                    player.playWorldSound(location, Sound.ENCHANT_THORNS_HIT, 2.0f);
                    
                    this.cancel();
                    return;
                }
                
                // Send danger to entities in range
                entitiesInRange.forEach(entity -> {
                    final boolean willTakeDamage = entity.distanceToSquared(projectile) <= explosionRadiusSquared;
                    
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
        
        player.playWorldSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2.0f);
    }
    
    @NotNull
    @Override
    public TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @NotNull
    @Override
    public Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        final Arrow arrow = player.launchProjectile(Arrow.class, new ShockDartArrowDamageSource(
                player,
                arrowDamage.getScaledValue(player),
                explosionMaxDamage.getScaledValue(player)
        ));
        
        new HariantTask(Scheduler.ofTimer(1)) {
            @Override
            public void run() {
                if (arrow.isDead()) {
                    this.cancel();
                    return;
                }
                
                player.spawnWorldParticle(arrow.getLocation(), Particle.ELECTRIC_SPARK, 1, 0.1, 0.1, 0.1, 0.25f);
            }
        };
        
        // Fx
        player.playWorldSound(Sound.ENTITY_ARROW_SHOOT, 2.0f);
        player.playWorldSound(Sound.ENTITY_BEE_STING, 0.75f);
        
        return Response.ok();
    }
    
    public class ShockDartArrowDamageSource extends DamageSourceArcherTalent {
        private final double explosionMaxDamage;
        
        ShockDartArrowDamageSource(@NotNull HariantEntity attacker, double damage, double explosionMaxDamage) {
            super(TalentShockDart.this, attacker, damage, 0);
            
            this.explosionMaxDamage = explosionMaxDamage;
        }
    }
    
    public class ShockDartExplosionDamageSource extends DamageSourceImpl {
        ShockDartExplosionDamageSource(@NotNull HariantPlayer attacker, double damage) {
            super(TalentShockDart.this, attacker, DamageType.TALENT, ElementType.ELECTRIC, DamageComponent.common(), Set.of(), damage, elementApplication.doubleValue());
        }
    }
    
}
