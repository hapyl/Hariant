package me.hapyl.hariant.hero.shark;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayModel;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.math.geometry.Geometry;
import me.hapyl.eterna.module.math.geometry.Quality;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementSource;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.*;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.entity.effect.Effect;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.entity.trap.Trap;
import me.hapyl.hariant.entity.trap.TrapEscape;
import me.hapyl.hariant.entity.trap.TrapName;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

public final class TalentBubbleTrap extends Talent implements Effect {
    
    private final @DisplayField Decimal maximumDistance = Decimal.ofValue(16);
    private final @DisplayField Decimal castingTime = Decimal.ofSeconds(0.8f);
    private final @DisplayField Decimal radius = Decimal.ofValue(2.25);
    private final @DisplayField Decimal totalAnomalyApplication = Decimal.ofElementalApplication(ElementType.WATER, 1000);
    
    private final @DisplayField AttributeScaling popDamage = AttributeScaling.create(Map.of(
            AttributeType.ATTACK, 25.0,
            AttributeType.ELEMENTAL_MASTERY, 45.0
    ));
    
    private final double yOffset = 1.2;
    private final double bubbleSize = 1.5;
    
    private final DisplayModel model = BDEngine.parse(
            "/summon block_display ~-0.5 ~ ~-0.5 {Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blue_stained_glass_pane\",Properties:{north:\"true\",south:\"true\",east:\"false\",west:\"false\"}},transformation:[0.5f,0f,0f,0.314695399f,0f,0.5f,0f,0.3571875095f,0f,0f,0.5f,-0.2653046343f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blue_stained_glass_pane\",Properties:{north:\"true\",south:\"true\",east:\"false\",west:\"false\"}},transformation:[0.3535533906f,-0.3535533906f,0f,0.395320399f,0.3535533906f,0.3535533906f,0f,0.6606250095f,0f,0f,0.5f,-0.2653046343f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blue_stained_glass_pane\",Properties:{north:\"true\",south:\"true\",east:\"false\",west:\"false\"}},transformation:[0.3535533906f,0.3535533906f,0f,0.043445399f,-0.3535533906f,0.3535533906f,0f,0.2009375095f,0f,0f,0.5f,-0.2653046343f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blue_stained_glass_pane\",Properties:{north:\"true\",south:\"true\",east:\"false\",west:\"false\"}},transformation:[0.3535533906f,0f,0.3535533906f,0.043445399f,0f,0.5f,0f,0.3571875095f,-0.3535533906f,0f,0.3535533906f,-0.4215546343f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blue_stained_glass_pane\",Properties:{north:\"true\",south:\"true\",east:\"false\",west:\"false\"}},transformation:[0.3535533906f,0f,-0.3535533906f,0.387195399f,0f,0.5f,0f,0.3571875095f,0.3535533906f,0f,0.3535533906f,0.0381328657f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blue_stained_glass_pane\",Properties:{north:\"true\",south:\"true\",east:\"false\",west:\"false\"}},transformation:[0f,0f,-0.5f,0.230945399f,0f,0.5f,0f,0.3571875095f,0.5f,0f,0f,0.3078203657f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blue_stained_glass_pane\",Properties:{north:\"true\",south:\"true\",east:\"false\",west:\"false\"}},transformation:[0f,0f,-0.5f,0.242820399f,0f,0.5f,0f,0.3571875095f,0.5f,0f,0f,-0.8418671343f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blue_stained_glass_pane\",Properties:{north:\"true\",south:\"true\",east:\"false\",west:\"false\"}},transformation:[0.5f,0f,0f,-0.840304601f,0f,0.5f,0f,0.3571875095f,0f,0f,0.5f,-0.2653046343f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blue_stained_glass_pane\",Properties:{north:\"true\",south:\"true\",east:\"false\",west:\"false\"}},transformation:[0.3535533906f,0f,0.3535533906f,-0.779679601f,0f,0.5f,0f,0.3571875095f,-0.3535533906f,0f,0.3535533906f,0.3881328657f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blue_stained_glass_pane\",Properties:{north:\"true\",south:\"true\",east:\"false\",west:\"false\"}},transformation:[0.3535533906f,0f,-0.3535533906f,-0.415304601f,0f,0.5f,0f,0.3571875095f,0.3535533906f,0f,0.3535533906f,-0.7796796343f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blue_stained_glass_pane\",Properties:{north:\"true\",south:\"true\",east:\"false\",west:\"false\"}},transformation:[1.29e-8f,0f,0.5f,-0.256242101f,0.3535533906f,0.3535533906f,-9.1e-9f,0.6606250095f,-0.3535533906f,0.3535533906f,9.1e-9f,-0.4243671343f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blue_stained_glass_pane\",Properties:{north:\"true\",south:\"true\",east:\"false\",west:\"false\"}},transformation:[-0.3535533906f,0.3535533906f,0f,-0.425304601f,0.3535533906f,0.3535533906f,0f,0.6606250095f,0f,0f,-0.5f,0.2346953657f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blue_stained_glass_pane\",Properties:{north:\"true\",south:\"true\",east:\"false\",west:\"false\"}},transformation:[1.67e-8f,0f,-0.5f,0.230945399f,0.3535533906f,0.3535533906f,1.18e-8f,0.6606250095f,0.3535533906f,-0.3535533906f,1.18e-8f,0.3909453657f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blue_stained_glass_pane\",Properties:{north:\"true\",south:\"true\",east:\"false\",west:\"false\"}},transformation:[0f,-0.5f,0f,0.233132899f,0.5f,0f,0f,0.9296875095f,0f,0f,0.5f,-0.2653046343f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blue_stained_glass_pane\",Properties:{north:\"true\",south:\"true\",east:\"false\",west:\"false\"}},transformation:[0f,-0.5f,0f,0.233132899f,0.5f,0f,0f,-0.2187499905f,0f,0f,0.5f,-0.2653046343f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blue_stained_glass_pane\",Properties:{north:\"true\",south:\"true\",east:\"false\",west:\"false\"}},transformation:[1.83e-8f,0f,0.5f,-0.269054601f,-0.3535533906f,0.3535533906f,1.29e-8f,0.2009375095f,-0.3535533906f,-0.3535533906f,1.29e-8f,-0.0668671343f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blue_stained_glass_pane\",Properties:{north:\"true\",south:\"true\",east:\"false\",west:\"false\"}},transformation:[-0.3535533906f,-0.3535533906f,1.18e-8f,-0.062804601f,-0.3535533906f,0.3535533906f,1.18e-8f,0.2009375095f,-1.67e-8f,0f,-0.5f,0.2346953657f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blue_stained_glass_pane\",Properties:{north:\"true\",south:\"true\",east:\"false\",west:\"false\"}},transformation:[1.67e-8f,0f,-0.5f,0.230945399f,-0.3535533906f,0.3535533906f,-1.18e-8f,0.2009375095f,0.3535533906f,0.3535533906f,1.18e-8f,0.0331328657f,0f,0f,0f,1f]}]}"
    );
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.create(
            this,
            DeathMessage.create("{player} was bubbled to death [by {killer}]")
    );
    
    public TalentBubbleTrap(@NotNull Key key) {
        super(key, Component.text("Bubble Trap"), Icon.ofMaterial(Material.BLUE_STAINED_GLASS));
        
        setTalentType(TalentType.IMPAIR);
        
        setDurationSeconds(3);
        setCooldownSeconds(18);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Summon a "))
                         .append(Component.text("bubble", Colors.AQUA))
                         .append(Component.text(" at the target "))
                         .append(Component.text("enemy", Colors.RED))
                         .append(Component.text(" position that slowly inflates."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Once inflated, the bubble "))
                         .append(Component.text("traps", Colors.RED))
                         .append(Component.text(" the closest enemy, "))
                         .append(Component.text("impairing", Colors.ARCHETYPE_HEXBANE))
                         .append(Component.text(" their movement and constantly applying "))
                         .appendNewline()
                         .append(ElementType.WATER)
                         .append(Component.text(" anomaly."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("After "))
                         .append(this.getDurationFormatted())
                         .append(Component.text(", the bubble pops, dealing "))
                         .appendNewline()
                         .append(ElementType.WATER.asComponentAreaOfEffectDamage())
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("The bubble pops instantly if no enemies were trapped or the trapped entity has died.", Colors.DARK_GRAY))
        );
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.targetEntityRayCast(maximumDistance.doubleValue(), 1, player::canAffect);
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        final HariantEntity target = context.retrieve(HariantEntity.class);
        final Location location = target.getLocation();
        
        player.delegate(new BubbleTrapAnimation(player, location), DelegateType.INTERRUPTABLE);
        
        return Response.ok();
    }
    
    @Override
    public @NotNull EffectType getEffectType() {
        return EffectType.DEBUFF;
    }
    
    private static void drawBubble(@NotNull ParticleSpawner particleSpawner, @NotNull Location location, double size) {
        Geometry.drawSphere(location, size, Quality.VERY_HIGH, _location -> particleSpawner.spawnWorldParticle(_location, Particle.BUBBLE, 1, 0));
    }
    
    private class BubbleTrapAnimation extends HariantTickingTask implements EntityCollector {
        
        private final HariantPlayer player;
        private final Location location;
        
        private final double baseY;
        
        BubbleTrapAnimation(@NotNull HariantPlayer player, @NotNull Location location) {
            super(Scheduler.ofTimer());
            
            this.player = player;
            this.location = LocationHelper.copyOfPosition(location).add(0, yOffset, 0);
            this.baseY = this.location.getY();
        }
        
        @Override
        public void run(int tick) {
            // Capture the target
            if (tick > castingTime.intValue()) {
                final HariantEntity target = collectNearbyEntities(radius)
                        .filter(player::canAffect)
                        .sorted(Comparator.comparingDouble(entity -> entity.distanceToSquared(location)))
                        .findAny()
                        .orElse(null);
                
                // If captured something, prepare the target
                if (target != null) {
                    // If failed to trap, instantly pop the bubble
                    final BubbleTrap bubbleTrap = new BubbleTrap(target, player, location);
                    
                    if (!target.trap(bubbleTrap)) {
                        bubbleTrap.pop();
                    }
                }
                
                this.cancel();
                return;
            }
            
            // Send warning
            if (modulo(4)) {
                this.collectNearbyEntities(radius).filter(player::canAffect).forEach(entity -> entity.showWarning(WarningType.WARNING, 5));
            }
            
            // Casting Fx
            final double progress = (double) tick / castingTime.doubleValue();
            final double y = Math.sin(Math.PI * 0.5 * progress) * yOffset + baseY;
            
            location.setY(y);
            
            drawBubble(player, location, (bubbleSize - 0.2) * progress + 0.2);
            
            player.playWorldSound(location, Sound.UI_HUD_BUBBLE_POP, (float) (0.5f + progress));
        }
        
        @Override
        public @NotNull Location getLocation() {
            return location;
        }
        
    }
    
    private class BubbleTrap extends Trap implements EntityCollector {
        
        private static final TrapName TRAP_NAME = new TrapName("Bubble", Style.style(Colors.SHARK));
        
        private final Location location;
        private final SitHandler sitHandler;
        
        private final ElementSource elementSource;
        private final DamageSource damageSource;
        
        BubbleTrap(@NotNull HariantEntity entity, @NotNull HariantPlayer source, @NotNull Location location) {
            super(entity, source, TRAP_NAME, TalentBubbleTrap.this.getDuration());
            
            this.location = location;
            this.sitHandler = entity.setSitting(entity.getLocation(), false);
            this.elementSource = ElementSource.create(
                    ElementType.WATER,
                    source,
                    // Element is applied over the duration of the talent, so decrement by the decrement to apply the whole value in total
                    (totalAnomalyApplication.doubleValue() + HariantConstants.ELEMENTAL_UNITS_DECREMENT_PER_TICK * getDuration()) / getDuration()
            );
            this.damageSource = new BubbleDamageSource(source, popDamage.getScaledValue(source));
        }
        
        @Override
        public void onEscape(@NotNull TrapEscape escape) {
            this.pop();
        }
        
        @Override
        public void tick() {
            super.tick();
            
            // Apply anomaly regardless
            entity.applyElement(elementSource);
            
            // Sync bubble
            final Location location = LocationHelper.copyOf(this.location);
            final double y = Math.sin(Math.toRadians(currentTick() * 5)) * 0.25;
            
            location.add(0, y, 0);
            
            // Draw bubble
            drawBubble(entity, location, bubbleSize);
            
            // Offset location by a little to center the entity
            location.subtract(0, 0.5, 0);
            
            // If entity is far away from the bubble, sync to it
            final double distanceToSquared = entity.distanceToSquared(location);
            
            if (distanceToSquared > 4) {
                final Vector vector = entity.getLocation().toVector().subtract(location.toVector()).normalize();
                
                sitHandler.move(location.add(vector));
            }
            // Otherwise sync to the location
            else {
                sitHandler.move(location);
            }
        }
        
        public void pop() {
            entity.unsetSitting();
            
            this.collectNearbyEntities(radius)
                .filter(source::canAffect)
                .forEach(entity -> {
                    entity.damage(damageSource);
                });
            
            // Fx
            source.playWorldSound(location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 3, 2.0f);
            source.playWorldSound(location, Sound.AMBIENT_UNDERWATER_EXIT, 3, 2.0f);
        }
        
        @Override
        public @NotNull Location getLocation() {
            return location;
        }
    }
    
    private class BubbleDamageSource extends DamageSourceImpl {
        
        BubbleDamageSource(@NotNull HariantPlayer player, double damage) {
            super(damageSourceIdentity, player, DamageType.TALENT, ElementType.WATER, DamageComponent.ofCommon(), Set.of(), damage, 0);
        }
        
    }
}