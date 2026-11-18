package me.hapyl.hariant.hero.shaman;

import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Disposable;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.HariantTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.Origin;
import me.hapyl.hariant.util.decimal.Decimal;
import me.hapyl.hariant.util.field.DisplayField;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TalentShamanMark extends Talent {
    
    public static final Component COMPONENT_SHAMAN_MARK = Component.text("\uD83D\uDC38", Colors.THE_JUNGLE);
    
    private static final ItemStack PARTICLE_DATA = new ItemStack(Material.SLIME_BALL);
    private static final Particle.DustTransition DUST_TRANSITION = new Particle.DustTransition(Color.fromRGB(48, 242, 86), Color.fromRGB(16, 122, 55), 1);
    
    private final @DisplayField Decimal maximumProjectileDistance = Decimal.ofValue(25);
    private final @DisplayField Decimal maximumNoLineOfSightTicks = Decimal.ofSeconds(2);
    
    private final @DisplayField Decimal projectileSpeed = Decimal.ofBlocksPerSecond(16);
    private final @DisplayField Decimal projectileLookupRadius = Decimal.ofValue(1.2);
    
    private final @DisplayField Decimal attackIncrease = Decimal.ofAttribute(AttributeType.ATTACK, 50);
    private final @DisplayField Decimal movementSpeedIncrease = Decimal.ofAttribute(AttributeType.MOVEMENT_SPEED, 15);
    
    private final Key modifierKey = Key.ofString("shaman_mark");
    
    public TalentShamanMark(@NotNull Key key) {
        super(key, Component.text("Shaman's Mark"), Icon.ofMaterial(Material.LILY_PAD));
        
        setTalentType(TalentType.SUPPORT);
        setCooldown(Tick.ofSeconds(12));
        
        setDescription(
                Component.empty()
                         .append(Component.text("Launch a projectile forwards that sticks to the first "))
                         .append(Component.text("ally", Colors.GREEN))
                         .append(Component.text(" hit, applying "))
                         .append(this.getName().color(Colors.GREEN))
                         .append(Component.text(" unto them."))
                         .appendNewline()
                         .appendNewline()
                         .append(this.getName().color(Colors.GOLD))
                         .appendNewline()
                         .append(Component.text("Increases "))
                         .append(AttributeType.ATTACK)
                         .append(Component.text(" and "))
                         .append(AttributeType.MOVEMENT_SPEED)
                         .append(Component.text(" for an "))
                         .append(Component.text("undefinite", Colors.WHITE))
                         .append(Component.text(" duration, as long as line of sight with Shaman is maintained."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Only one mark can exist on a single ally at any given time, and cooldown of this talent starts when the mark is removed.", Colors.DARK_GRAY))
        );
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        player.delegate(new ShamanMarkProjectile(player), DelegateType.PERSISTENT);
        
        return Response.await();
    }
    
    public enum RemovalReason implements ComponentLike {
        
        NO_LOS(Component.text("line of sight was broken")),
        ENTITY_DIED(Component.text("teammate has died"));
        
        private final Component component;
        
        RemovalReason(@NotNull Component component) {
            this.component = component;
        }
        
        @Override
        public @NotNull Component asComponent() {
            return component;
        }
        
    }
    
    public class ShamanMark implements Ticking, Disposable, ComponentLike {
        
        private final HariantPlayer player;
        private final HariantEntity entity;
        
        private final Component componentPlayerEntity;
        private final Component componentEntityPlayer;
        
        private int noLineOfSightTicks;
        private @Nullable RemovalReason removalReason;
        
        ShamanMark(@NotNull HariantPlayer player, @NotNull HariantEntity entity) {
            this.player = player;
            this.entity = entity;
            
            this.componentPlayerEntity = COMPONENT_SHAMAN_MARK.appendSpace().append(entity.asHeadComponentInBrackets());
            this.componentEntityPlayer = COMPONENT_SHAMAN_MARK.appendSpace().append(player.asHeadComponentInBrackets());
            
            // Add modifier
            entity.getAttributes().addModifier(new ShamanMarkAttributeModifier(player));
            
            // Sfx
            player.playSound(Sound.ENTITY_FROG_HURT, 0.5f);
            player.playSound(Sound.ENTITY_FROG_DEATH, 0.0f);
        }
        
        public @Nullable RemovalReason getRemovalReason() {
            return removalReason;
        }
        
        @Override
        public void tick() {
            noLineOfSightTicks++;
            
            // If player has line of sight, reset line of sight ticks
            if (player.hasLineOfSight(entity)) {
                noLineOfSightTicks = 0;
            }
            
            // Notify Shaman if line of sight was broken
            if (noLineOfSightTicks == 1) {
                player.playSound(Sound.ENTITY_FROG_HURT, 1.25f);
            }
            
            // If entity has died, or no line of sights ticks are reached, break the mark
            if (entity.isDead()) {
                removalReason = RemovalReason.ENTITY_DIED;
            }
            else if (noLineOfSightTicks > maximumNoLineOfSightTicks.intValue()) {
                removalReason = RemovalReason.NO_LOS;
            }
            else {
                // Notify entity that they have the buff
                if (entity instanceof HariantPlayer entityPlayer) {
                    entityPlayer.actionbar(TalentShamanMark.class, componentEntityPlayer);
                }
                
                this.drawLine();
            }
        }
        
        @Override
        public void dispose() {
            // Remove the modifier from the entity
            entity.getAttributes().removeModifier(modifierKey);
            
            // Start the cooldown for Shaman
            player.startCooldown(TalentShamanMark.this);
        }
        
        @Override
        public @NotNull Component asComponent() {
            return componentPlayerEntity;
        }
        
        private void drawLine() {
            final Location location = player.getMidpointLocation();
            final Location destination = entity.getMidpointLocation();
            
            final double distance = LocationHelper.distance(location, destination);
            
            for (double d = 0; d < distance; d += 0.1) {
                final double progress = d / distance;
                final double x = location.getX() + (destination.getX() - location.getX()) * progress;
                final double y = location.getY() + (destination.getY() - location.getY()) * progress + Math.sin(Math.toRadians(player.localTicks()) * 4) * 0.05;
                final double z = location.getZ() + (destination.getZ() - location.getZ()) * progress;
                
                location.set(x, y, z);
                
                player.spawnWorldParticle(location, Particle.DUST_COLOR_TRANSITION, 1, 0, 0, 0, 0, DUST_TRANSITION);
            }
        }
        
    }
    
    public class ShamanMarkProjectile extends HariantTask implements EntityCollector {
        
        private final HariantPlayer player;
        private final Location location;
        private final Origin origin;
        private final Vector vector;
        
        private double distanceFlown;
        
        ShamanMarkProjectile(@NotNull HariantPlayer player) {
            super(Scheduler.ofTimer());
            
            this.player = player;
            this.location = player.getEyeLocation();
            this.origin = Origin.create(location);
            this.vector = location.getDirection().normalize();
        }
        
        @Override
        public void run() {
            if (distanceFlown > maximumProjectileDistance.doubleValue()) {
                // Nothing was hit, start the cooldown normally
                this.cancelStartCooldown();
                return;
            }
            
            // Increment first so we don't check on origin
            distanceFlown += projectileSpeed.doubleValue();
            
            final double x = vector.getX() * distanceFlown;
            final double y = vector.getY() * distanceFlown;
            final double z = vector.getZ() * distanceFlown;
            
            origin.merge(location, x, y, z);
            
            // If we hit wall, break
            if (location.getBlock().isSolid()) {
                this.cancelStartCooldown();
                return;
            }
            
            // Check for collision
            final HariantEntity collision = this.collectNearbyEntities(projectileLookupRadius)
                                                // `isTeammate` does not count player themselves as a teammate
                                                .filter(player::isTeammate)
                                                .findAny()
                                                .orElse(null);
            
            if (collision != null) {
                player.getHeroData(HeroRegistry.SHAMAN, HeroDataShaman::new).setShamanMark(new ShamanMark(player, collision));
                
                this.cancel();
                return;
            }
            
            // Sfx
            player.spawnWorldParticle(location, Particle.ITEM, 3, 0.1, 0.1, 0.1, 0.1f, PARTICLE_DATA);
            player.playWorldSound(location, Sound.ENTITY_FROG_TONGUE, 1.25f);
        }
        
        @Override
        public @NotNull Location getLocation() {
            return location;
        }
        
        private void cancelStartCooldown() {
            this.cancel();
            this.player.startCooldown(TalentShamanMark.this);
        }
        
    }
    
    public class ShamanMarkAttributeModifier extends AttributeModifier {
        
        ShamanMarkAttributeModifier(@NotNull HariantEntity applier) {
            super(modifierKey, TalentShamanMark.this, applier, Tick.INDEFINITE);
            
            of(AttributeType.ATTACK, AttributeModifierType.FLAT, attackIncrease);
            of(AttributeType.MOVEMENT_SPEED, AttributeModifierType.FLAT, movementSpeedIncrease);
        }
        
    }
    
}