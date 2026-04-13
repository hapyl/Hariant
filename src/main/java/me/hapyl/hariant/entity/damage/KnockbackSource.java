package me.hapyl.hariant.entity.damage;

import me.hapyl.eterna.module.location.Coordinates;
import me.hapyl.hariant.HariantConstants;
import org.jetbrains.annotations.NotNull;

public interface KnockbackSource {
    
    double x();
    
    double z();
    
    default double strength() {
        return HariantConstants.MELEE_KNOCKBACK_STRENGTH;
    }
    
    @NotNull
    static KnockbackSource create(@NotNull Coordinates coordinates, final double strength) {
        return create(coordinates.x(), coordinates.z(), strength);
    }
    
    @NotNull
    static KnockbackSource create(final double x, final double z, final double strength) {
        return new KnockbackSourceImpl(x, z, strength);
    }
    
}
