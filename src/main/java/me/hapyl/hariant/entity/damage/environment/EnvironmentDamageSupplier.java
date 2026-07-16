package me.hapyl.hariant.entity.damage.environment;

import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface EnvironmentDamageSupplier {
    
    @NotNull
    EnvironmentDamageSource supply(@NotNull HariantEntity entity);
    
}
