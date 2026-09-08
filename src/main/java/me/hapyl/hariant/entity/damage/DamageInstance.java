package me.hapyl.hariant.entity.damage;

import me.hapyl.hariant.annotate.CopyConstructor;
import me.hapyl.hariant.annotate.Stale;
import me.hapyl.hariant.attribute.instance.AttributesInstanceSnapshot;
import me.hapyl.hariant.element.ElementSource;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.entity.damage.report.DamageReport;
import me.hapyl.hariant.event.HariantDamageCalculationsEvent;
import me.hapyl.hariant.util.Identified;
import me.hapyl.hariant.util.decimal.Decimal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Set;

public class DamageInstance implements MutatesDamage, DamageFlagged, ElementSource {
    
    private final HariantEntity entity;
    private final DamageSource damageSource;
    private final DamageReport damageReport;
    
    private @NotNull ElementType elementType;
    private double elementUnits;
    
    private @NotNull DamageType damageType;
    private @NotNull @Unmodifiable List<? extends DamageComponent> damageComponents;
    private @NotNull @Unmodifiable Set<? extends DamageFlag> damageFlags;
    
    private double damage;
    
    private boolean critical;
    private boolean shielded;
    private boolean lethal;
    
    @CopyConstructor
    public DamageInstance(@NotNull DamageInstance damageInstance) {
        this.entity = damageInstance.entity;
        this.damageSource = damageInstance.damageSource;
        this.damageReport = new DamageReport(this, damageInstance.damageReport);
        this.elementType = damageInstance.elementType;
        this.elementUnits = damageInstance.elementUnits;
        this.damageType = damageInstance.damageType;
        this.damageComponents = List.copyOf(damageInstance.damageComponents);
        this.damageFlags = Set.copyOf(damageInstance.damageFlags);
        this.damage = damageInstance.damage;
        this.critical = damageInstance.critical;
        this.shielded = damageInstance.shielded;
        this.lethal = damageInstance.lethal;
    }
    
    public DamageInstance(@NotNull HariantEntity entity, @NotNull DamageSource damageSource) {
        this.entity = entity;
        this.damageSource = damageSource;
        this.damageReport = new DamageReport(this);
        
        // Copy mutable fields from damage source
        this.elementType = damageSource.getElementType();
        this.elementUnits = damageSource.getElementUnits();
        this.damageType = damageSource.getDamageType();
        this.damageComponents = damageSource.getDamageComponents();
        this.damageFlags = damageSource.getDamageFlags();
        
        this.calculateDamage();
    }
    
    public @NotNull HariantEntity getEntity() {
        return entity;
    }
    
    /**
     * Gets the original {@link DamageSource} of this {@link DamageInstance}, which should only be
     * to compare {@code instanceof} it when needed, for anything else, use the actual {@link DamageInstance}.
     *
     * @return the original damage source.
     */
    public @NotNull @Stale(use = "this") DamageSource getDamageSource() {
        return damageSource;
    }
    
    public @NotNull DamageReport getDamageReport() {
        return damageReport;
    }
    
    @Override
    public @NotNull ElementType getElementType() {
        return elementType;
    }
    
    public void setElementType(@NotNull ElementType elementType) {
        this.elementType = elementType;
    }
    
    @Override
    public @Nullable HariantEntity getSource() {
        return damageSource.getSource();
    }
    
    @Override
    public double getElementUnits() {
        return elementUnits;
    }
    
    public void setElementUnits(double elementUnits) {
        this.elementUnits = elementUnits;
    }
    
    public @NotNull DamageType getDamageType() {
        return damageType;
    }
    
    public void setDamageType(@NotNull DamageType damageType) {
        this.damageType = damageType;
    }
    
    public @NotNull @Unmodifiable List<? extends DamageComponent> getDamageComponents() {
        return damageComponents;
    }
    
    public void setDamageComponents(@NotNull @Unmodifiable List<? extends DamageComponent> damageComponents) {
        this.damageComponents = damageComponents;
    }
    
    @Override
    public @NotNull @Unmodifiable Set<? extends DamageFlag> getDamageFlags() {
        return damageFlags;
    }
    
    public void setDamageFlags(@NotNull @Unmodifiable Set<? extends DamageFlag> damageFlags) {
        this.damageFlags = damageFlags;
    }
    
    public @Nullable HariantEntity getAttacker() {
        return damageSource.getSource();
    }
    
    public boolean isCritical() {
        return critical;
    }
    
    public boolean isShielded() {
        return shielded;
    }
    
    public boolean isLethal() {
        return lethal;
    }
    
    public double getDamage() {
        return damage;
    }
    
    @Override
    public void mutateDamage(@NotNull Identified identity, @NotNull DamageMutator mutator, final double value) {
        final double damageBeforeMutation = damage;
        final double damageAfterMutation = mutator.mutate(damage, value);
        
        this.damage = damageAfterMutation;
        this.damageReport.report(identity, mutator, value, damageBeforeMutation, damageAfterMutation);
    }
    
    @Override
    public void mutateDamage(@NotNull Identified identity, @NotNull DamageMutator mutator, final @NotNull Decimal value) {
        this.mutateDamage(identity, mutator, value.doubleValue());
    }
    
    public void markCritical() {
        this.critical = true;
    }
    
    public void markShielded() {
        this.shielded = true;
    }
    
    public void markLethal() {
        this.lethal = true;
    }
    
    public boolean cooldownExistsEntityOnCooldownElseStartCooldown(@NotNull HariantEntity entity) {
        if (damageSource.hasCooldown()) {
            if (entity.hasCooldown(damageSource)) {
                return true;
            }
            
            // If cooldown exists and entity is not on cooldown, start it
            entity.setCooldown(damageSource, damageSource.getCooldown(), null);
        }
        
        return false;
    }
    
    private void calculateDamage() {
        // Snapshot attributes so we can modify them in the event without mutating the actual entity attributes
        final AttributesInstanceSnapshot snapshotEntity = AttributesInstanceSnapshot.snapshot(entity);
        final AttributesInstanceSnapshot snapshotAttacker = AttributesInstanceSnapshot.snapshot(damageSource.getSource());
        
        // Call calculations event, which can be used to modify attributes or damage source
        final HariantDamageCalculationsEvent event = new HariantDamageCalculationsEvent(this, snapshotEntity, snapshotAttacker);
        event.callEvent();
        
        // Set the base damage equal to the source damage
        this.damage = damageSource.getDamage();
        
        // Apply components
        for (final DamageComponent component : this.damageComponents) {
            final double multiplier = component.multiplier(this, snapshotEntity, snapshotAttacker);
            final double damageBeforeMultiplier = damage;
            
            this.damage *= multiplier;
            this.damageReport.report(component, DamageMutator.multiply(), multiplier, damageBeforeMultiplier, damage);
        }
    }
    
}
