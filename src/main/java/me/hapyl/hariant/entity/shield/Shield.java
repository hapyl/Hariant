package me.hapyl.hariant.entity.shield;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageFlag;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.environment.EnvironmentDamageSource;
import me.hapyl.hariant.event.HariantShieldRemoveEvent;
import me.hapyl.hariant.ui.ComponentDisplay;
import me.hapyl.hariant.util.Identified;
import me.hapyl.hariant.util.TickDuration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class Shield implements Ticking, TickDuration, ComponentLike, Identified {
    
    public static final Component SHIELD_CHARACTER = HariantConstants.CHARACTER_SHIELDED_DAMAGE.color(Colors.YELLOW);
    
    private static final double MAX_ABSORPTION = 20;
    
    protected final @NotNull HariantEntity entity;
    protected final @NotNull HariantEntity applier;
    
    private final ShieldStrength strength;
    
    private final double maximumCapacity;
    private final int duration;
    
    private double capacity;
    private int tick;
    
    public Shield(@NotNull HariantEntity entity, @NotNull HariantEntity applier, @NotNull ShieldStrength strength, double maximumCapacity, int duration) {
        this.entity = entity;
        this.applier = applier;
        this.maximumCapacity = maximumCapacity;
        this.strength = strength;
        this.capacity = maximumCapacity;
        this.duration = duration;
        this.tick = duration;
    }
    
    public void regenerate(double capacity) {
        this.capacity = Math.min(this.capacity + Math.abs(capacity), maximumCapacity);
        this.onRegenerate();
        this.updateYellowHearts();
    }
    
    public @NotNull HariantEntity getEntity() {
        return entity;
    }
    
    public @NotNull HariantEntity getApplier() {
        return applier;
    }
    
    public @NotNull ShieldStrength getStrength() {
        return strength;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public boolean canShield(@NotNull DamageSource damageSource) {
        // If damage source is flagged as PIERCING_DAMAGE damage, the shield cannot shield
        if (damageSource.isFlagged(DamageFlag.PIERCING_DAMAGE)) {
            return false;
        }
        
        // By defaults, shield prevent any damage except FALL damage
        if (damageSource instanceof EnvironmentDamageSource environmentDamageSource) {
            return !environmentDamageSource.isFall();
        }
        
        return true;
    }
    
    @Override
    public void tick() {
        if (duration != HariantConstants.INDEFINITE_DURATION) {
            tick--;
        }
    }
    
    public @NotNull ShieldResult shield(double damage, @NotNull DamageSource damageSource) {
        final double mitigated = damage / strength.strength(damageSource.getElementType());
        final double mitigatedMin = Math.min(mitigated, capacity);
        
        this.capacity -= mitigated;
        this.onHit(mitigatedMin);
        this.updateYellowHearts();
        
        return new ShieldResult(capacity, mitigated, mitigatedMin);
    }
    
    @EventLike
    public void onCreate() {
    }
    
    @EventLike
    public void onRemove(@NotNull Cause cause) {
    }
    
    @EventLike
    public void onHit(double amount) {
    }
    
    @EventLike
    public void onRegenerate() {
    }
    
    public boolean isBroken() {
        return capacity <= 0;
    }
    
    public double getCapacity() {
        return capacity;
    }
    
    public void setCapacity(double capacity) {
        this.capacity = capacity;
        this.updateYellowHearts();
    }
    
    public double getMaximumCapacity() {
        return maximumCapacity;
    }
    
    @Override
    public int currentTick() {
        return tick;
    }
    
    @Override
    public int duration() {
        return duration;
    }
    
    @Override
    public @NotNull Component asComponent() {
        final TextComponent.Builder builder = Component.text();
        
        builder.append(Component.text("%,.0f/%,.0f".formatted(capacity, maximumCapacity), Colors.YELLOW));
        builder.appendSpace();
        
        // If shield isn't infinite, append time left on the shield
        if (!isIndefinite()) {
            builder.append(
                    Component.empty()
                             .append(Component.text("(", Colors.GOLD))
                             .append(currentTickFormatted().color(Colors.GOLD))
                             .append(Component.text(")", Colors.GOLD))
            );
            builder.appendSpace();
        }
        
        builder.append(SHIELD_CHARACTER);
        
        // Append applier unless it is self
        if (!entity.equals(applier)) {
            builder.appendSpace()
                   .append(Component.text("[", Colors.GRAY))
                   .append(applier.asHeadComponent())
                   .append(Component.text("]", Colors.GRAY));
        }
        
        return builder.build();
    }
    
    public final void onCreate0() {
        entity.getHandle().setAbsorptionAmount(MAX_ABSORPTION);
        
        this.onCreate();
    }
    
    public final void onRemove0(@NotNull Cause cause) {
        this.onRemove(cause);
        this.entity.getHandle().setAbsorptionAmount(0);
        
        // Call event
        new HariantShieldRemoveEvent(this, cause).callEvent();
    }
    
    @Override
    public @NotNull String identify() {
        return this.getClass().getSimpleName();
    }
    
    public void display(double shielded, @NotNull Location location) {
        ComponentDisplay.ofAscend(
                Component.empty()
                         .append(SHIELD_CHARACTER)
                         .append(Component.text(" %.0f".formatted(shielded), Colors.YELLOW)),
                location,
                20,
                1.5f
        );
    }
    
    private void updateYellowHearts() {
        final double absorptionAmount = Math.clamp(MAX_ABSORPTION * capacity / maximumCapacity, 0, MAX_ABSORPTION);
        
        entity.getHandle().setAbsorptionAmount(absorptionAmount);
    }
    
    public enum Cause {
        BROKE,
        EXPIRED,
        REPLACED,
        ENTITY_DIED
    }
    
}