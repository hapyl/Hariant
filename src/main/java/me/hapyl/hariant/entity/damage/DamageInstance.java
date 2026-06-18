package me.hapyl.hariant.entity.damage;

import me.hapyl.hariant.attribute.instance.AttributesInstanceSnapshot;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.entity.damage.report.DamageReport;
import me.hapyl.hariant.event.HariantDamageCalculationsEvent;
import me.hapyl.hariant.util.Identified;
import me.hapyl.hariant.util.decimal.Decimal;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class DamageInstance {
    
    private final DamageSource source;
    private final DamageReport damageReport;
    
    private final List<? extends DamageComponent> components;
    
    @NotNull
    private final HariantEntity entity;
    
    @Nullable
    private final HariantEntity attacker;
    
    private double damage;
    
    private boolean critical;
    private boolean shielded;
    private boolean lethal;
    
    public DamageInstance(@NotNull HariantEntity entity, @NotNull DamageSource source) {
        this.source = source;
        this.components = source.getDamageComponents();
        this.entity = entity;
        this.attacker = source.getSource();
        this.damageReport = new DamageReport(this);
        this.calculateDamage();
    }
    
    @NotNull
    public DamageReport getDamageReport() {
        return damageReport;
    }
    
    @NotNull
    public HariantEntity getEntity() {
        return entity;
    }
    
    @Nullable
    public HariantEntity getAttacker() {
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
    public DamageSource getSource() {
        return source;
    }
    
    @NotNull
    public List<DamageComponent> getComponents() {
        return List.copyOf(components);
    }
    
    @ApiStatus.Internal
    public void markCritical() {
        this.critical = true;
    }
    
    @ApiStatus.Internal
    public void markShielded() {
        this.shielded = true;
    }
    
    @ApiStatus.Internal
    public void markLethal() {
        this.lethal = true;
    }
    
    @ApiStatus.Internal
    private void calculateDamage() {
        this.damage = source.getDamage();
        
        // Snapshot attributes so we can modify them in the event without
        // mutating the actual entity attributes
        final AttributesInstanceSnapshot snapshotEntity = AttributesInstanceSnapshot.snapshot(entity);
        final AttributesInstanceSnapshot snapshotAttacker = AttributesInstanceSnapshot.snapshot(attacker);
        
        // Call calculations event, which can be used to modify attributes
        new HariantDamageCalculationsEvent(source, snapshotEntity, snapshotAttacker).callEvent();
        
        // Apply components
        for (final DamageComponent component : components) {
            final double multiplier = component.multiplier(this, snapshotEntity, snapshotAttacker);
            final double damageBeforeMultiplier = damage;
            
            this.damage *= multiplier;
            this.damageReport.report(component, DamageMutator.multiply(), multiplier, damageBeforeMultiplier, damage);
        }
    }
    
}
