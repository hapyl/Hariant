package me.hapyl.hariant.hero.bounty_hunter;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.util.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.target.TalentTargetEntityRayCast;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.task.executor.ExecutorService;
import me.hapyl.hariant.ui.ComponentDisplay;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class TalentSeverance extends TalentUltimate {
    
    private final @DisplayField Decimal maximumDistance = Decimal.ofValue(30);
    private final @DisplayField AttributeScaling damage = AttributeScaling.create(AttributeType.ATTACK, 338);
    
    private final @DisplayField Decimal movementSpeedIncrease = Decimal.ofAttribute(AttributeType.MOVEMENT_SPEED, 20);
    private final @DisplayField Decimal movementSpeedIncreaseDuration = Decimal.ofSeconds(8);
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.createOfNamed(
            this,
            Key.ofString("severance_damage_source"),
            DeathMessage.create("{player} was stabbed in the back [by {killer}]")
    );
    
    public TalentSeverance(@NotNull Key key) {
        super(key, Component.text("Severance"), Icon.ofMaterial(Material.NETHERITE_SWORD), UltimateResourceType.ENERGY, 120);
        
        setCooldownSeconds(20);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Instantly teleport behind the target "))
                         .append(Component.text("enemy", Colors.RED))
                         .append(Component.text(", stabbing them from behind, dealing "))
                         .append(ElementType.PHYSICAL.asComponentDamage())
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("If the damage is lethal, gain a "))
                         .appendNewline()
                         .append(AttributeType.MOVEMENT_SPEED)
                         .append(Component.text(" buff for "))
                         .append(movementSpeedIncreaseDuration)
                         .append(Component.text("."))
        );
        
    }
    
    @Override
    public @NotNull Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, double consumedResource) {
        final HariantEntity target = context.retrieve(HariantEntity.class);
        final DamageSource damageSource = new SeveranceDamageSource(player);
        
        return new ExecutorService()
                .then(Executable.execute(() -> {
                    final Location location = target.getLocation();
                    location.add(location.getDirection().normalize().setY(0).multiply(-1));
                    
                    final Location locationBeforeTeleport = player.getLocation();
                    
                    player.teleport(location);
                    
                    // Fx
                    player.playWorldSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.0f);
                    player.playWorldSound(location, Sound.ENTITY_IRON_GOLEM_REPAIR, 1.25f);
                    
                    player.spawnWorldParticle(locationBeforeTeleport, Particle.LARGE_SMOKE, 20, 0.1, 0.5, 0.1, 0.25f);
                    player.spawnWorldParticle(location, Particle.LARGE_SMOKE, 20, 0.1, 0.5, 0.1, 0.25f);
                }))
                .then(Executable.later(() -> {
                    if (target.damage(damageSource).isDead()) {
                        player.getAttributes().addModifier(new SeveranceAttributeModifier(player));
                    }
                    
                    player.swingHand();
                }, 1));
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.targetEntity(
                maximumDistance.doubleValue(),
                1.0,
                TalentTargetEntityRayCast.BlockCollision.ALLOW_PASSABLE,
                TalentTargetEntityRayCast.EntityPriority.PLAYER_PRIORITY,
                entity -> player.canAffect(entity) && player.hasLineOfSight(entity)
        );
    }
    
    private class SeveranceDamageSource extends DamageSourceImpl {
        
        SeveranceDamageSource(@NotNull HariantEntity source) {
            super(damageSourceIdentity, source, DamageType.ULTIMATE, ElementType.PHYSICAL, DamageComponents.ofCommon(), Set.of(), damage.getScaledValue(source), 0);
        }
        
    }
    
    private class SeveranceAttributeModifier extends AttributeModifier {
        
        SeveranceAttributeModifier(@NotNull HariantEntity applier) {
            super(Key.ofString("severance_modifier"), TalentSeverance.this.getName(), applier, movementSpeedIncreaseDuration.intValue());
            
            of(AttributeType.MOVEMENT_SPEED, AttributeModifierType.FLAT, movementSpeedIncreaseDuration.doubleValue());
        }
        
        @Override
        public void display(@NotNull Location location) {
            ComponentDisplay.ofAscend(this.getName(), location, 20, 1.0f);
        }
        
    }
    
}