package me.hapyl.hariant.hero.mage;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroData;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.task.executor.Promise;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HeroDataMage extends HeroData<HeroMage> {
    
    private int souls;
    private SoulStorm soulStorm;
    
    public HeroDataMage(@NotNull HeroMage hero, @NotNull HariantPlayer player) {
        super(hero, player);
        
        this.souls = TalentRegistry.SOUL_HARVEST.startingSouls.intValue();
    }
    
    public int getSouls() {
        return souls;
    }
    
    public @Nullable SoulStorm getSoulStorm() {
        return soulStorm;
    }
    
    public void createSoulStorm(int charges, int maxCharges, @NotNull Promise promise) {
        if (soulStorm != null) {
            soulStorm.dispose();
        }
        
        soulStorm = new SoulStorm(player, charges, maxCharges, promise);
    }
    
    @Override
    public void dispose() {
        this.disposeOfSoulStorm();
    }
    
    @Override
    public void tick() {
        if (soulStorm != null) {
            soulStorm.tick();
            
            if (soulStorm.isEmpty()) {
                this.disposeOfSoulStorm();
            }
        }
    }
    
    public void incrementSouls(int souls) {
        this.souls = Math.min(TalentRegistry.SOUL_HARVEST.maximumSouls.intValue(), this.souls + souls);
    }
    
    public void decrementSouls(int souls) {
        this.souls = Math.max(0, this.souls - souls);
    }
    
    public boolean hasSoulStorm() {
        return soulStorm != null;
    }
    
    private void disposeOfSoulStorm() {
        if (soulStorm != null) {
            soulStorm.dispose();
            soulStorm = null;
        }
    }
    
}