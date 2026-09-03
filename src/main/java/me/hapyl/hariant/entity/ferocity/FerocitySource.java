package me.hapyl.hariant.entity.ferocity;

import me.hapyl.eterna.module.annotate.DefensiveCopy;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.entity.damage.DamageSource;
import org.jetbrains.annotations.NotNull;

public interface FerocitySource {
    
    @NotNull HariantEntity getSource();
    
    @NotNull DamageInstance getDamageInstance();
    
    @NotNull DamageSource getDamageSource();
    
    int ferocityStrikes();
    
    static @NotNull FerocitySource create(@NotNull HariantEntity source, @NotNull @DefensiveCopy DamageInstance damageInstance, int ferocityStrikes) {
        return new FerocitySourceImpl(source, damageInstance, ferocityStrikes);
    }
    
}