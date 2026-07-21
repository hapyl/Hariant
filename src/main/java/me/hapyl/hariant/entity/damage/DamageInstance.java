package me.hapyl.hariant.entity.damage;

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

public final class DamageInstance {
    
    private final @NotNull DamageReport damageReport;
    
    private final @NotNull HariantEntity entity;
    private final @Nullable HariantEntity attacker;
    
    private @NotNull DamageSource damageSource;
    private double damage;
    
    private boolean critical;
    private boolean shielded;
    private boolean lethal;
    
    private DamageInstance(@NotNull HariantEntity entity, @NotNull DamageSource damageSource, @NotNull Function<DamageInstance, DamageReport> damageReport) {
        this.entity = entity;
        this.attacker = damageSource.getSource();
        this.damageSource = damageSource;
        this.damageReport = damageReport.apply(this);
    }
    
    public DamageInstance(@NotNull HariantEntity entity, @NotNull DamageSource damageSource) {
        this(entity, damageSource, DamageReport::new);
        this.calculateDamage();
    }
    
    public @NotNull DamageReport getDamageReport() {
        return damageReport;
    }
    
    public @NotNull HariantEntity getEntity() {
        return entity;
    }
    
    public @Nullable HariantEntity getAttacker() {
        return attacker;
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
    
    public void mutateDamage(@NotNull Identified identity, @NotNull DamageMutator mutator, final double value) {
        final double damageBeforeMutation = damage;
        final double damageAfterMutation = mutator.mutate(damage, value);
        
        this.damage = damageAfterMutation;
        this.damageReport.report(identity, mutator, value, damageBeforeMutation, damageAfterMutation);
    }
    
    public void mutateDamage(@NotNull Identified identity, @NotNull DamageMutator mutator, final Decimal value) {
        this.mutateDamage(identity, mutator, value.doubleValue());
    }
    
    @NotNull
    public DamageSource getDamageSource() {
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
        final AttributesInstanceSnapshot snapshotAttacker = AttributesInstanceSnapshot.snapshot(attacker);
        
        // Call calculations event, which can be used to modify attributes or damage source
        final HariantDamageCalculationsEvent event = new HariantDamageCalculationsEvent(damageSource, snapshotEntity, snapshotAttacker);
        event.callEvent();
        
        this.damageSource = event.getDamageSource();
        this.damage = damageSource.getDamage();
        
        // Apply components
        for (final DamageComponent component : damageSource.getDamageComponents()) {
            final double multiplier = component.multiplier(this, snapshotEntity, snapshotAttacker);
            final double damageBeforeMultiplier = damage;
            
            this.damage *= multiplier;
            this.damageReport.report(component, DamageMutator.multiply(), multiplier, damageBeforeMultiplier, damage);
        }
    }
    
    public static @NotNull DamageInstance copyOf(@NotNull DamageInstance damageInstance, @NotNull Mutator mutator) {
        final DamageInstance copy = new DamageInstance(
                damageInstance.entity,
                mutator.mutate(damageInstance.damageSource.toBuilder()).build(),
                newInstance -> DamageReport.copyOf(newInstance, damageInstance.damageReport)
        );
        
        copy.damage = damageInstance.damage;
        copy.critical = damageInstance.critical;
        copy.shielded = damageInstance.shielded;
        copy.lethal = damageInstance.lethal;
        
        return copy;
    }
    
    public interface Mutator {
        
        @NotNull DamageSource.Builder mutate(@NotNull DamageSource.Builder builder);
        
    }
    
}
