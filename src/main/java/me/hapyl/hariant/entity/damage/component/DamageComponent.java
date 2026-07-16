package me.hapyl.hariant.entity.damage.component;

import me.hapyl.hariant.attribute.instance.AttributesInstanceSnapshot;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.util.Identified;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface DamageComponent extends Identified {
    
    @Override
    @NotNull String identify();
    
    double multiplier(@NotNull DamageInstance damageInstance, @NotNull AttributesInstanceSnapshot entity, @NotNull AttributesInstanceSnapshot attacker);
    
    static @NotNull DamageComponent elemental() {
        return Holder.ELEMENTAL;
    }
    
    static @NotNull DamageComponent defense() {
        return Holder.DEFENSE;
    }
    
    static @NotNull DamageComponent critical() {
        return Holder.CRITICAL;
    }
    
    static @NotNull List<? extends DamageComponent> ofCommon() {
        return Holder.COMMON;
    }
    
    static @NotNull List<? extends DamageComponent> ofTrueDamage() {
        return Holder.TRUE_DAMAGE;
    }
    
    static @NotNull List<? extends DamageComponent> ofEnvironmentDamage() {
        return Holder.ENVIRONMENT_DAMAGE;
    }
    
    final class Holder {
        private static final @NotNull DamageComponent ELEMENTAL = new DamageComponentElemental();
        private static final @NotNull DamageComponent DEFENSE = new DamageComponentDefense();
        private static final @NotNull DamageComponent CRITICAL = new DamageComponentCritical();
        
        /* Commons components scale of ELEMENTAL, DEF and can deal CRIT DMG. */
        private static final @NotNull @Unmodifiable List<? extends DamageComponent> COMMON = List.of(ELEMENTAL, DEFENSE, CRITICAL);
        
        /* True damage ignores DEF. */
        private static final @NotNull @Unmodifiable List<? extends DamageComponent> TRUE_DAMAGE = List.of(ELEMENTAL, CRITICAL);
        
        /* Environment damage cannot CRIT. */
        private static final @NotNull @Unmodifiable List<? extends DamageComponent> ENVIRONMENT_DAMAGE = List.of(ELEMENTAL, DEFENSE);
        
        private Holder() {
        }
    }
    
    
}