package me.hapyl.hariant.hero.archer;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroData;
import org.jetbrains.annotations.NotNull;

public class HeroDataArcher extends HeroData<HeroArcher> {
    
    private boolean isInfused;
    
    public HeroDataArcher(@NotNull HeroArcher hero, @NotNull HariantPlayer player) {
        super(hero, player);
    }
    
    public void setInfused(boolean value) {
        this.isInfused = value;
    }
    
    public boolean isInfused() {
        return isInfused;
    }
    
    @Override
    public void dispose() {
    }
    
}
