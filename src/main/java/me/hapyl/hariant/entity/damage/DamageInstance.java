package me.hapyl.hariant.entity.damage;

import me.hapyl.hariant.annotate.CopyConstructor;
import me.hapyl.hariant.attribute.instance.AttributesInstanceSnapshot;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.entity.damage.report.DamageReport;
import me.hapyl.hariant.event.HariantDamageCalculationsEvent;
import me.hapyl.hariant.util.Identified;
import me.hapyl.hariant.util.decimal.Decimal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class DamageInstance implements MutatesDamage {
    
    private final @NotNull HariantEntity entity;
    private final @NotNull DamageSource damageSource;
    private final @NotNull DamageReport damageReport;
    
    private double damage;
    
    private boolean critical;
    private boolean shielded;
    private boolean lethal;
    
    private DamageInstance(@NotNull HariantEntity entity, @NotNull DamageSource damageSource, @NotNull Function<DamageInstance, DamageReport> damageReport) {
        this.entity = entity;
        this.damageSource = damageSource;
        this.damageReport = damageReport.apply(this);
    }
    
    @CopyConstructor
    public DamageInstance(@NotNull DamageInstance damageInstance) {
        this(damageInstance.getEntity(), damageInstance.getDamageSource().clone(), copy -> new DamageReport(copy, damageInstance.damageReport));
        
        this.damage = damageInstance.damage;
        this.critical = damageInstance.critical;
        this.shielded = damageInstance.shielded;
        this.lethal = damageInstance.lethal;
    }
    
    public DamageInstance(@NotNull HariantEntity entity, @NotNull DamageSource damageSource) {
        this(entity, damageSource, DamageReport::new);
        
        // Calculate the damage
        this.calculateDamage();
    }
    
    public @NotNull DamageReport getDamageReport() {
        return damageReport;
    }
    
    public @NotNull HariantEntity getEntity() {
        return entity;
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
    
    public @NotNull DamageSource getDamageSource() {
        return damageSource;
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
    
    private void calculateDamage() {
        // Snapshot attributes so we can modify them in the event without mutating the actual entity attributes
        final AttributesInstanceSnapshot snapshotEntity = AttributesInstanceSnapshot.snapshot(entity);
        final AttributesInstanceSnapshot snapshotAttacker = AttributesInstanceSnapshot.snapshot(damageSource.getSource());
        
        // Call calculations event, which can be used to modify attributes or damage source
        final HariantDamageCalculationsEvent event = new HariantDamageCalculationsEvent(damageSource, snapshotEntity, snapshotAttacker);
        event.callEvent();
        
        // Honestly I don't know why we're getting the damage here, you cannot modify the damage in the source, should you?
        this.damage = damageSource.getDamage();
        
        // Apply components
        for (final DamageComponent component : damageSource.getDamageComponents()) {
            final double multiplier = component.multiplier(this, snapshotEntity, snapshotAttacker);
            final double damageBeforeMultiplier = damage;
            
            this.damage *= multiplier;
            this.damageReport.report(component, DamageMutator.multiply(), multiplier, damageBeforeMultiplier, damage);
        }
    }
    
}
