package me.hapyl.hariant.entity.damage;

import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;

public interface DamageSourceCreator {
    
    @NotNull
    DamageSource.Builder createDamageSource(@NotNull HariantEntity attacker);
    
}
