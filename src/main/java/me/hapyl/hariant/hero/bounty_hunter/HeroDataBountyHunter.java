package me.hapyl.hariant.hero.bounty_hunter;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HeroDataBountyHunter extends HeroData<HeroBountyHunter> {
    
    public @Nullable TalentGrapple.Grapple grapple;
    
    public HeroDataBountyHunter(@NotNull HeroBountyHunter hero, @NotNull HariantPlayer player) {
        super(hero, player);
    }
    
    @Override
    public void dispose() {
        if (grapple != null) {
            grapple.cancel();
        }
    }
    
}