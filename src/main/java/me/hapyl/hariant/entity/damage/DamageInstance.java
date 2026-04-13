package me.hapyl.hariant.entity.damage;

import me.hapyl.hariant.attribute.instance.AttributesInstanceSnapshot;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.event.HariantDamageCalculationsEvent;
import me.hapyl.hariant.util.Identified;
import net.kyori.adventure.text.Component;
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
    
    public double getDamage() {
        return damage;
    }
    
    public void multiplyDamage(@NotNull Identified identity, double multiplier) {
        this.damageReport.report(identity, multiplier, damage);
        this.damage *= multiplier;
    }
    
    @NotNull
    public DamageSource getSource() {
        return source;
    }
    
    @NotNull
    public List<DamageComponent> getComponents() {
        return List.copyOf(components);
    }
    
    public boolean isAlreadyCritical() {
        return critical;
    }
    
    @ApiStatus.Internal
    public void markCritical() {
        this.critical = true;
    }
    
    @ApiStatus.Internal
    void calculateDamage() {
        this.damage = source.getDamage();
        
        // Snapshot attributes so we can modify them in the event without
        // mutating the actual entity attributes
        final AttributesInstanceSnapshot snapshotEntity = AttributesInstanceSnapshot.snapshot(entity);
        final AttributesInstanceSnapshot snapshotAttacker = AttributesInstanceSnapshot.snapshot(attacker);
        
        // Call calculations event, which can be used to modify attributes
        new HariantDamageCalculationsEvent(source, snapshotEntity, snapshotAttacker).callEvent();
        
        // Apply components
        for (DamageComponent component : components) {
            final double multiplier = component.multiplier(this, snapshotEntity, snapshotAttacker);
            final double damageBeforeMultiplier = damage;
            
            this.damage *= multiplier;
            this.damageReport.report(component, multiplier, damageBeforeMultiplier);
        }
    }
    
}
