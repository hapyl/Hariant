package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.hero.Hero;
import org.jetbrains.annotations.NotNull;

public class AchievementHeroImpl extends AchievementImpl {
    
    private final Hero hero;
    
    AchievementHeroImpl(@NotNull Key key, @NotNull Hero hero, double goal) {
        super(key, goal);
        
        this.hero = hero;
        this.setCategory(AchievementCategory.HERO_RELATED);
    }
    
    public @NotNull Hero getHero() {
        return hero;
    }
    
}