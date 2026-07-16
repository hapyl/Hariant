package me.hapyl.hariant.achievement;

import me.hapyl.hariant.util.RewardsRubies;

public enum AchievementTier implements RewardsRubies {
    
    TIER_1(5),
    TIER_2(10),
    TIER_3(20),
    TIER_4(50);
    
    private final int rubyReward;
    
    AchievementTier(int rubyReward) {
        this.rubyReward = rubyReward;
    }
    
    @Override
    public int getRubyReward() {
        return rubyReward;
    }
}
