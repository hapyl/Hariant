package me.hapyl.hariant.entity.damage.component;

import me.hapyl.hariant.attribute.instance.snapshot.AttributesSnapshot;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.util.Identified;
import org.jetbrains.annotations.NotNull;

public interface DamageComponent extends Identified {
    
    @Override
    @NotNull String identify();
    
    double multiplier(@NotNull DamageInstance damageInstance, @NotNull AttributesSnapshot entity, @NotNull AttributesSnapshot attacker);
    
}