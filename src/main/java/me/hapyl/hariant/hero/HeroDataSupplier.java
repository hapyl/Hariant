package me.hapyl.hariant.hero;

import me.hapyl.hariant.entity.player.HariantPlayer;
import org.jetbrains.annotations.NotNull;

public interface HeroDataSupplier<H extends Hero, D extends HeroData<H>> {
    
    @NotNull
    D supply(@NotNull H hero, @NotNull HariantPlayer player);
    
}
