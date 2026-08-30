package me.hapyl.hariant.daily;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.hariant.util.RewardsRubies;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public enum DailyTier implements Named, RewardsRubies {
    
    TIER_1(
            Component.text("Tier I"),
            250,
            500,
            5
    ),
    
    TIER_2(
            Component.text("Tier II"),
            500,
            1000,
            5
    ),
    
    TIER_3(
            Component.text("Tier III"),
            1000,
            2000,
            5
    );
    
    private final Component name;
    
    private final int coinsReward;
    private final int experienceReward;
    private final int rubyReward;
    
    DailyTier(@NotNull Component name, int coinsReward, int experienceReward, int rubyReward) {
        this.name = name;
        this.coinsReward = coinsReward;
        this.experienceReward = experienceReward;
        this.rubyReward = rubyReward;
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    public int getCoinsReward() {
        return coinsReward;
    }
    
    public int getExperienceReward() {
        return experienceReward;
    }
    
    @Override
    public int getRubyReward() {
        return rubyReward;
    }
    
}