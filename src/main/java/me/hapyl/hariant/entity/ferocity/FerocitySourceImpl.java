package me.hapyl.hariant.entity.ferocity;

import me.hapyl.eterna.module.annotate.DefensiveCopy;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageType;
import org.jetbrains.annotations.NotNull;

public class FerocitySourceImpl implements FerocitySource {
    
    private final HariantEntity source;
    private final DamageInstance damageInstance;
    private final DamageSource damageSource;
    private final int ferocityStrikes;
    
    FerocitySourceImpl(@NotNull HariantEntity source, @NotNull @DefensiveCopy DamageInstance damageInstance, int ferocityStrikes) {
        this.source = source;
        this.damageInstance = prepareDamageInstance(damageInstance);
        this.damageSource = damageInstance.getDamageSource();
        this.ferocityStrikes = ferocityStrikes;
    }
    
    @Override
    public @NotNull HariantEntity getSource() {
        return source;
    }
    
    @Override
    public @NotNull DamageInstance getDamageInstance() {
        // Always return a copy of the damage instance since it can be mutated inside damage method
        return DamageInstance.copyOf(damageInstance);
    }
    
    @Override
    public @NotNull DamageSource getDamageSource() {
        return damageSource;
    }
    
    @Override
    public int ferocityStrikes() {
        return ferocityStrikes;
    }
    
    private static @NotNull DamageInstance prepareDamageInstance(@NotNull DamageInstance damageInstance) {
        final DamageInstance prepared = DamageInstance.copyOf(damageInstance);
        
        // Set the damage type to FEROCITY and zero the elemental units application
        prepared.setDamageType(DamageType.FEROCITY);
        prepared.setElementUnits(0);
        
        return prepared;
    }
    
}