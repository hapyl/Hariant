package me.hapyl.hariant.hero.troll;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroData;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public final class HeroDataTroll extends HeroData<HeroTroll> {
    
    public TalentStickySituation.StickySituation stickySituation;
    
    public HeroDataTroll(@NonNull HeroTroll hero, @NotNull HariantPlayer player) {
        super(hero, player);
    }
    
    @Override
    public void dispose() {
        destroyStickSituation();
    }
    
    @Override
    public void tick() {
        if (stickySituation != null) {
            stickySituation.tick();
            
            if (stickySituation.isEmpty()) {
                destroyStickSituation();
            }
        }
    }
    
    public void createStickSituation(@NotNull TalentStickySituation.StickySituation stickySituation) {
        this.destroyStickSituation();
        this.stickySituation = stickySituation;
    }
    
    public void destroyStickSituation() {
        if (stickySituation != null) {
            stickySituation.dispose();
            stickySituation = null;
        }
    }
    
}