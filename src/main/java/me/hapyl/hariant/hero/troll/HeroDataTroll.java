package me.hapyl.hariant.hero.troll;

import me.hapyl.hariant.achievement.AchievementTrollBlastOff;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroData;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public final class HeroDataTroll extends HeroData<HeroTroll> {
    
    private int lastRepulsorUsage;
    
    public HeroDataTroll(@NonNull HeroTroll hero, @NotNull HariantPlayer player) {
        super(hero, player);
    }
    
    public void setLastRepulsorUsage() {
        AchievementTrollBlastOff.progress(player, lastRepulsorUsage);
        
        lastRepulsorUsage = player.localTicks();
    }
    
    @Override
    public void dispose() {
    }
    
}