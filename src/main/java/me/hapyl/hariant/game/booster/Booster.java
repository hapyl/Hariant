package me.hapyl.hariant.game.booster;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.util.ImmutableLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface Booster {
    
    @NotNull ImmutableLocation getLocation();
    
    void boost(@NotNull HariantPlayer player);
    
    static <B extends Booster> @NotNull B create(@NotNull B booster) {
        BoosterHandler.BOOSTERS.put(booster.getLocation(), booster);
        return booster;
    }
    
    static <B extends Booster> @NotNull B create(@NotNull Supplier<B> supplier) {
        return create(supplier.get());
    }
    
}
