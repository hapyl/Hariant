package me.hapyl.hariant.talent.rechargeable;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentIndex;
import org.jetbrains.annotations.NotNull;

public class RechargeableTalentData {
    
    private final HariantPlayer player;
    private final TalentRechargeable talent;
    private final TalentIndex talentIndex;
    
    private int charges;
    
    public RechargeableTalentData(@NotNull HariantPlayer player, @NotNull TalentRechargeable talent, @NotNull TalentIndex talentIndex) {
        this.player = player;
        this.talent = talent;
        this.talentIndex = talentIndex;
        this.charges = talent.getMaximumCharges();
    }
    
    public @NotNull HariantPlayer getPlayer() {
        return player;
    }
    
    public @NotNull TalentRechargeable getTalent() {
        return talent;
    }
    
    public @NotNull TalentIndex getTalentIndex() {
        return talentIndex;
    }
    
    public void incrementCharges(int amount) {
        this.setCharges(Math.min(this.charges + amount, this.talent.getMaximumCharges()));
    }
    
    public void decrementCharges(int amount) {
        this.setCharges(Math.max(this.charges - amount, 0));
    }
    
    public int getCharges() {
        return charges;
    }
    
    private void setCharges(int amount) {
        this.charges = amount;
        this.talent.onUpdate(player, this);
    }
    
    
}
