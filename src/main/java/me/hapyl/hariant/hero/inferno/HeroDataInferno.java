package me.hapyl.hariant.hero.inferno;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HeroDataInferno extends HeroData<HeroInferno> {
    
    @Nullable public InfernoDemon currentDemon;
    
    public HeroDataInferno(@NotNull HeroInferno hero, @NotNull HariantPlayer player) {
        super(hero, player);
    }
    
    @Override
    public void tick() {
        if (currentDemon != null) {
            // If time ran out, reform from the demon
            if (currentDemon.isOver()) {
                currentDemon.onReform(player, this);
                currentDemon.remove();
                currentDemon = null;
            }
        }
    }
    
    @Override
    public void dispose() {
        if (currentDemon != null) {
            currentDemon.remove();
            currentDemon = null;
        }
    }
    
}