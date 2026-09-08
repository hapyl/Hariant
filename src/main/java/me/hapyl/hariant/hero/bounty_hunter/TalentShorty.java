package me.hapyl.hariant.hero.bounty_hunter;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DamageSourceImpl;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.rechargeable.RechargeType;
import me.hapyl.hariant.talent.rechargeable.RechargeableTalentData;
import me.hapyl.hariant.talent.rechargeable.TalentRechargeable;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public final class TalentShorty extends TalentRechargeable {
    
    private final @DisplayField Decimal pellets = Decimal.ofValue(12);
    
    private final @DisplayField Decimal bulletDistance = Decimal.ofValue(4);
    private final @DisplayField Decimal bulletRadius = Decimal.ofValue(0.5);
    
    private final @DisplayField Decimal horizontalSpread = Decimal.ofAngle(10);
    private final @DisplayField Decimal verticalSpread = Decimal.ofAngle(6);
    
    private final @DisplayField AttributeScaling damagePerPellet = AttributeScaling.create(AttributeType.ATTACK, 10);
    private final @DisplayField Decimal elementalApplicationPerPellet = Decimal.ofElementalApplication(ElementType.PHYSICAL, 40);
    
    private final @DisplayField Decimal knockbackMagnitude = Decimal.ofValue(0.6);
    private final @DisplayField Decimal knockbackY = Decimal.ofValue(0.25);
    
    private final @DisplayField Decimal playerKnockbackMagnitude = Decimal.ofValue(0.3);
    private final @DisplayField Decimal playerKnockbackY = Decimal.ofValue(0.2);
    
    private static final Vector UP = new Vector(0, 1, 0);
    private static final Particle.DustTransition DUST_TRANSITION = new Particle.DustTransition(Color.fromRGB(0, 4, 10), Color.fromRGB(37, 38, 38), 0.75f);
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.create(
            Key.ofString("shorty_damage_instance"),
            Component.text("Shorty"),
            DeathMessage.create("{player} was shot to death [by {killer}]")
    );
    
    public TalentShorty(@NotNull Key key) {
        super(key, Component.text("Shorty"), Icon.ofMaterial(Material.CROSSBOW), 2, RechargeType.DEPLETE_ALL);
        
        setCooldownSeconds(3);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Shoot a double barrel shotgun that deals "))
                         .append(ElementType.PHYSICAL.asComponentAreaOfEffectDamage())
                         .append(Component.text(" and applies "))
                         .appendNewline()
                         .append(ElementType.PHYSICAL)
                         .append(Component.text(" anomaly."))
                         .appendNewline()
                         .appendNewline()
                         .append(this.getMaximumChargesComponent())
        );
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext talentContext, @NotNull RechargeableTalentData rechargeableTalentData) {
        this.shoot(player);
        
        // Fx
        player.playWorldSound(Sound.ENTITY_GENERIC_EXPLODE, 1.75f);
        
        return Response.ok();
    }
    
    private void shoot(@NotNull HariantPlayer player) {
        final Map<HariantEntity, Integer> numberOfPelletHitsPerEnemy = Maps.newHashMap();
        final Location location = player.getEyeLocation().subtract(0, 0.3, 0);
        
        final double originX = location.getX();
        final double originY = location.getY();
        final double originZ = location.getZ();
        
        // Ray trace each pellet individually
        for (int i = 0; i < pellets.intValue(); i++) {
            final Set<HariantEntity> hitEntities = Sets.newHashSet();
            final Vector vector = getPelletVector(player, location);
            
            for (double d = 0; d < bulletDistance.doubleValue(); d += 0.5) {
                final double x = originX + vector.getX() * d;
                final double y = originY + vector.getY() * d;
                final double z = originZ + vector.getZ() * d;
                
                location.set(x, y, z);
                
                // Collect entities
                player.collectNearbyEntities(location, bulletRadius)
                      .filter(player::canAffect)
                      .forEach(entity -> {
                          if (hitEntities.add(entity)) {
                              numberOfPelletHitsPerEnemy.merge(entity, 1, Integer::sum);
                          }
                      });
                
                player.spawnWorldParticle(location, Particle.DUST_COLOR_TRANSITION, 1, 0, 0, 0, 0, DUST_TRANSITION);
            }
        }
        
        final Vector knockbackVector = player.getDirection().normalize().multiply(knockbackMagnitude.doubleValue()).setY(knockbackY.doubleValue());
        
        // Process damage
        numberOfPelletHitsPerEnemy.forEach((entity, count) -> {
            // Calculate damage and anomaly application
            final double damage = damagePerPellet.getScaledValue(player) * count;
            final double elementalApplication = elementalApplicationPerPellet.intValue() * count;
            
            entity.damage(new ShortyDamageSource(player, damage, elementalApplication));
            
            // Deal knockback
            entity.setVelocity(knockbackVector);
        });
        
        // Knock the player a little bit behind
        player.setVelocity(player.getLocation().getDirection().normalize().multiply(-1).multiply(playerKnockbackMagnitude.doubleValue()).setY(playerKnockbackY.doubleValue()));
    }
    
    private @NotNull Vector getPelletVector(@NotNull HariantPlayer player, @NotNull Location location) {
        final Vector vector = location.getDirection().normalize();
        
        final Vector localRight = vector.getCrossProduct(UP).normalize();
        final Vector localUp = vector.getCrossProduct(localRight).normalize();
        
        final double offsetVertical = Math.toRadians(player.random.nextSignedDouble(verticalSpread.doubleValue()));
        final double offsetHorizontal = Math.toRadians(player.random.nextSignedDouble(horizontalSpread.doubleValue()));
        
        return vector.rotateAroundAxis(localRight, offsetVertical).rotateAroundAxis(localUp, offsetHorizontal);
    }
    
    private class ShortyDamageSource extends DamageSourceImpl {
        
        ShortyDamageSource(@NotNull HariantEntity source, double damage, double elementalApplication) {
            super(damageSourceIdentity, source, DamageType.TALENT, ElementType.PHYSICAL, DamageComponents.ofCommon(), Set.of(), damage, elementalApplication);
        }
        
    }
    
    
}