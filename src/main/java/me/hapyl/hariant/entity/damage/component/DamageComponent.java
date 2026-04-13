package me.hapyl.hariant.entity.damage.component;

import me.hapyl.hariant.attribute.instance.AttributesInstanceSnapshot;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.util.Identified;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DamageComponent extends Identified {
    
    @NotNull
    @Override
    String identify();
    
    double multiplier(@NotNull DamageInstance damageInstance, @NotNull AttributesInstanceSnapshot snapshotEntity, @NotNull AttributesInstanceSnapshot snapshotAttacker);
    
    @NotNull
    static DamageComponent elemental() {
        return new DamageComponentElemental();
    }
    
    @NotNull
    static DamageComponent defense() {
        return new DamageComponentDefense();
    }
    
    @NotNull
    static DamageComponent critical() {
        return new DamageComponentCritical();
    }
    
    @NotNull
    static List<DamageComponent> common() {
        return List.of(elemental(), defense(), critical());
    }
    
}
