package me.hapyl.hariant.hero.archer;

import me.hapyl.hariant.achievement.AchievementRegistry;
import me.hapyl.hariant.achievement.AchievementUniqueIdCounter;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroData;
import me.hapyl.hariant.profile.PlayerProfile;
import org.jetbrains.annotations.NotNull;

public class HeroDataArcher extends HeroData<HeroArcher> {
    
    public final AchievementUniqueIdCounter lastThreeHits;
    public final AchievementUniqueIdCounter numberOfHawkeyeArrowsShotInARow;
    
    private boolean isInfused;
    
    public HeroDataArcher(@NotNull HeroArcher hero, @NotNull HariantPlayer player) {
        super(hero, player);
        
        final PlayerProfile profile = player.getProfile();
        
        this.lastThreeHits = AchievementRegistry.ARCHER_TRIPLET.createUniqueIdCounter(profile);
        this.numberOfHawkeyeArrowsShotInARow = AchievementRegistry.ARCHER_NO_LUCK_ALL_SKILL.createUniqueIdCounter(profile);
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
