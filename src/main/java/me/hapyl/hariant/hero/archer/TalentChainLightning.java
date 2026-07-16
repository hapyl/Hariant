package me.hapyl.hariant.hero.archer;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.math.Vector3;
import me.hapyl.eterna.module.math.geometry.Geometry;
import me.hapyl.eterna.module.particle.ParticleBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantProjectileHitEvent;
import me.hapyl.hariant.handler.HariantProjectile;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class TalentChainLightning extends Talent implements Listener {
    
    private static final Color ARROW_COLOR = Color.fromRGB(Colors.ELEMENT_ELECTRIC.value());
    private static final ParticleBuilder PARTICLE_CHAIN_LIGHTNING = ParticleBuilder.dustColorTransition(Color.fromRGB(240, 213, 79), Color.fromRGB(252, 186, 3), 1);
    
    @DisplayField private final AttributeScaling damage = AttributeScaling.create(AttributeType.ATTACK, 207);
    @DisplayField private final Decimal elementalApplication = Decimal.ofElementalApplication(ElementType.ELECTRIC, 200);
    
    @DisplayField private final Decimal maxChainReaction = Decimal.ofValue(2);
    @DisplayField private final Decimal maxChainReactionDistance = Decimal.ofValue(6);
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.create(
            this,
            DeathMessage.create("{player} was shocked to death [by {killer}]")
    );
    
    public TalentChainLightning(@NotNull Key key) {
        super(key, Component.text("Chain Lightning"), Icon.ofMaterial(Material.TIPPED_ARROW, builder -> builder.setPotionColor(ARROW_COLOR)));
        
        this.setCooldownSeconds(7f);
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Shoot an electrified arrow in front of you that deals "))
                         .append(ElementType.ELECTRIC.asComponentDamage())
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Upon hitting an enemy, the arrow creates a chain reaction that deals "))
                         .appendNewline()
                         .append(ElementType.ELECTRIC.asComponentDamage())
                         .append(Component.text(" up to "))
                         .append(maxChainReaction)
                         .append(Component.text(" additional enemies."))
        );
        
    }
    
    @EventHandler
    public void handleHariantProjectileHitEvent(HariantProjectileHitEvent ev) {
        final HariantEntity entity = ev.getEntity();
        final HariantProjectile projectile = ev.getProjectile();
        
        if (entity == null || !(projectile.getDamageSource() instanceof ChainLightningArrowDamageSource damageSource)) {
            return;
        }
        
        if (!(damageSource.getSource() instanceof HariantPlayer player)) {
            return;
        }
        
        // Find the targets
        final List<HariantEntity> chainLightningTarget = findChainLightningTarget(player, entity);
        final int targetsSize = chainLightningTarget.size();
        
        // Affect the entities
        for (int i = 0; i < targetsSize; i++) {
            final HariantEntity targetCurrent = chainLightningTarget.get(i);
            final HariantEntity targetNext = i + 1 < targetsSize ? chainLightningTarget.get(i + 1) : null;
            
            // Damage non-first entity, because the arrow does the damage for that one
            if (i != 0) {
                targetCurrent.damage(ev.getProjectile().getDamageSource());
            }
            
            // Fx
            if (targetNext != null) {
                this.drawLightning(player.getWorld(), Vector3.ofLocation(targetCurrent.getMidpointLocation()), Vector3.ofLocation(targetNext.getMidpointLocation()), 0.25, 3);
            }
            
            targetCurrent.playWorldSound(Sound.ENTITY_LIGHTNING_BOLT_THUNDER, player.getRandom().nextFloat(1.25f, 2f));
        }
    }
    
    @NotNull
    public List<HariantEntity> findChainLightningTarget(@NotNull HariantPlayer player, @NotNull HariantEntity entity) {
        final List<HariantEntity> result = Lists.newArrayList();
        result.add(entity);
        
        final int maxChainReaction = this.maxChainReaction.intValue();
        final double maxChainReactionDistance = this.maxChainReactionDistance.doubleValue();
        
        HariantEntity current = entity;
        
        while (result.size() <= maxChainReaction) {
            HariantEntity closestEntity = null;
            double closestDistance = 0;
            
            for (HariantEntity potentialEntity : current.collectNearbyEntities(maxChainReactionDistance).toList()) {
                final double distanceToSquared = current.distanceToSquared(potentialEntity);
                
                // If already chained or not valid, skip
                if (result.contains(potentialEntity) || !player.canAffect(potentialEntity)) {
                    continue;
                }
                
                if (closestEntity == null || distanceToSquared < closestDistance) {
                    closestEntity = potentialEntity;
                    closestDistance = distanceToSquared;
                }
            }
            
            // No more entities to link
            if (closestEntity == null) {
                break;
            }
            
            current = closestEntity;
            result.add(closestEntity);
        }
        
        return result;
    }
    
    @NotNull
    @Override
    public TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @NotNull
    @Override
    public Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        player.launchProjectile(Arrow.class, new ChainLightningArrowDamageSource(player, damage.getScaledValue(player)), self -> self.setColor(ARROW_COLOR));
        
        // Fx
        player.playWorldSound(Sound.ENTITY_ARROW_SHOOT, 0.75f);
        player.playWorldSound(Sound.ENTITY_WARDEN_ATTACK_IMPACT, 1.25f);
        
        return Response.ok();
    }
    
    private void drawLightning(@NotNull World world, @NotNull Vector3 from, @NotNull Vector3 to, double offset, int depth) {
        if (depth == 0) {
            Geometry.drawLine(from.toLocation(world), to.toLocation(world), 0.25, PARTICLE_CHAIN_LIGHTNING::display);
            return;
        }
        
        final Vector3 midpoint = from.midpoint(to);
        final Vector3 direction = to.subtract(from);
        
        final Vector3 perpendicular = perpendicular(direction);
        final Vector3 newMidpoint = midpoint.add(perpendicular.multiply(Hariant.getRandom().nextDouble(-offset, offset)));
        
        this.drawLightning(world, from, newMidpoint, offset * 0.5, depth - 1);
        this.drawLightning(world, newMidpoint, to, offset * 0.5, depth - 1);
    }
    
    @NotNull
    private Vector3 perpendicular(@NotNull Vector3 vector) {
        final Vector3 perpendicular = vector.crossProduct(Vector3.up());
        
        return perpendicular.lengthSquared() != 0 ? perpendicular : vector.crossProduct(new Vector3(1, 0, 0));
    }
    
    public class ChainLightningArrowDamageSource extends DamageSourceArcherTalent {
        ChainLightningArrowDamageSource(@NotNull HariantEntity attacker, double damage) {
            super(damageSourceIdentity, attacker, damage, elementalApplication.doubleValue());
        }
    }
    
}
