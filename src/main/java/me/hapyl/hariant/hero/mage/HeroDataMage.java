package me.hapyl.hariant.hero.mage;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroData;
import me.hapyl.hariant.talent.TalentRegistry;
import org.jetbrains.annotations.NotNull;

public class HeroDataMage extends HeroData<HeroMage> {
    
    private int souls;
    
    public HeroDataMage(@NotNull HeroMage hero, @NotNull HariantPlayer player) {
        super(hero, player);
        
        this.souls = TalentRegistry.SOUL_HARVEST.startingSouls.intValue();
    }
    
    public int getSouls() {
        return souls;
    }
    
    @Override
    public void dispose() {
    }
    
    public void incrementSouls(int souls) {
        this.souls = Math.min(TalentRegistry.SOUL_HARVEST.maximumSouls.intValue(), this.souls + souls);
    }
    
    public void decrementSouls(int souls) {
        this.souls = Math.max(0, this.souls - souls);
    }
    
}
