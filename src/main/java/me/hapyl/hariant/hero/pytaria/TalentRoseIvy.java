package me.hapyl.hariant.hero.pytaria;

import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.WarningType;
import me.hapyl.hariant.entity.effect.status.StatusEffectType;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.handler.HariantProjectile;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.util.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.term.EnumTerminology;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.Models;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Snowball;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TalentRoseIvy extends Talent {
    
    public final @DisplayField AttributeScaling damage = AttributeScaling.create(AttributeType.ATTACK, 54);
    public final @DisplayField Decimal elementalApplication = Decimal.ofElementalApplication(ElementType.PHYSICAL, 50);
    
    private final @DisplayField Decimal radius = Decimal.ofValue(3);
    private final @DisplayField Decimal affectPeriod = Decimal.ofSeconds(0.5f);
    private final @DisplayField Decimal effectDuration = Decimal.ofSeconds(0.5f);
    private final @DisplayField Decimal speedDecrease = Decimal.ofPercentage(50);
    
    private final Key modifierKey = Key.ofString("rose_ivy");
    
    private final int modelParts = 7;
    
    public TalentRoseIvy(@NotNull Key key) {
        super(key, Component.text("Rose Ivy"), Icon.ofTexture("41cceb6ee1210e1725ce30a7da3d8e68fc38a7d8b6d30abc030a2601df951d2d"));
        
        this.setCooldownSeconds(16);
        this.setDurationSeconds(4);
        
        this.setTalentType(TalentType.IMPAIR);
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Throw a bag filled with "))
                         .append(Component.text("spiky rose", Colors.RED))
                         .append(Component.text(" seeds in front of you."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Upon hit, the seeds sprout to life, creating a "))
                         .append(this.getName().color(Colors.SUCCESS))
                         .append(Component.text(" in small "))
                         .append(EnumTerminology.AREA_OF_EFFECT)
                         .append(Component.text(" for "))
                         .append(this.getDurationFormatted())
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Enemies who are caught in the ivy are "))
                         .append(Component.text("slowed", Colors.ATTRIBUTE_MOVEMENT_SPEED))
                         .append(Component.text(" and take "))
                         .append(ElementType.PHYSICAL.asComponentDamage())
                         .append(Component.text(" whenever they "))
                         .append(Component.text("move").decorate(TextDecoration.UNDERLINED))
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("This talent cannot kill.", Colors.DARK_GRAY))
        );
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        player.launchProjectile(Snowball.class, null, RoseIvyProjectile::new);
        
        // Fx
        player.playWorldSound(Sound.ENTITY_SNOWBALL_THROW, 0.75f);
        
        return Response.ok();
    }
    
    private class RoseIvyProjectile extends HariantProjectile {
        
        RoseIvyProjectile(@NotNull Snowball projectile, @NotNull HariantEntity shooter) {
            super(projectile, shooter);
            
            projectile.setItem(getIcon().createItem());
        }
        
        @Override
        public void onHit(@Nullable HariantEntity entity, @Nullable Block block) {
            super.onHit(entity, block);
            
            final Location origin = LocationHelper.anchor(this.getLocation());
            final HariantEntity shooter = this.getShooter();
            
            origin.setYaw(shooter.random.nextFloat() * 180);
            
            shooter.delegate(new RoseIvyTask(shooter, origin), DelegateType.PERSISTENT);
            
            // Fx
            shooter.playWorldSound(origin, Sound.ENTITY_CAMEL_SADDLE, 0.0f);
            shooter.playWorldSound(origin, Sound.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH, 0.0f);
        }
        
    }
    
    private class RoseIvyModifier extends AttributeModifier {
        
        RoseIvyModifier(@NotNull HariantEntity applier) {
            super(modifierKey, TalentRoseIvy.this.getName(), applier, effectDuration.intValue());
            
            this.of(AttributeType.MOVEMENT_SPEED, AttributeModifierType.ADDITIVE, -speedDecrease.doubleValue());
        }
        
        @Override
        public void display(@NotNull Location location) {
        }
    }
    
    private class RoseIvyTask extends HariantTickingTask implements EntityCollector {
        
        private static final BlockData PARTICLE_DATA = Material.SWEET_BERRY_BUSH.createBlockData();
        
        private final HariantEntity player;
        private final Location origin;
        private final DisplayEntity vineEntity;
        
        RoseIvyTask(@NotNull HariantEntity entity, @NotNull Location origin) {
            super(Scheduler.ofTimer(1));
            
            this.player = entity;
            this.origin = origin;
            this.vineEntity = Models.ROSE_IVY.spawn(origin, self -> {
                self.setVisibleByDefault(false);
            });
        }
        
        @Override
        public void run(int tick) {
            if (tick >= TalentRoseIvy.this.getDuration()) {
                this.cancel();
                return;
            }
            
            // Show the model
            if (tick <= modelParts) {
                vineEntity.forEach(String.valueOf(tick), display -> Hariant.showBukkitEntity(display.getDisplay()));
            }
            
            // If fully drawn, apply IVY effect
            if (modulo(affectPeriod)) {
                collectNearbyEntities(origin, radius, 1, radius)
                        .filter(player::canAffect)
                        .forEach(entity -> {
                            entity.addEffect(StatusEffectType.ROSE_IVY, effectDuration, player);
                            entity.getAttributes().addModifier(new RoseIvyModifier(player));
                            
                            // Fx
                            entity.spawnWorldParticle(entity.getMidpointLocation(), Particle.BLOCK, 3, 0.25, 0.25, 0.25, 0.1f, PARTICLE_DATA);
                            entity.showWarning(WarningType.WARNING, affectPeriod.intValue() + 1);
                        });
            }
        }
        
        @Override
        public void onCancel() {
            vineEntity.remove();
        }
        
        @NotNull
        @Override
        public Location getLocation() {
            return origin;
        }
    }
    
}