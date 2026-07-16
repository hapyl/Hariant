package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.RewardsRubies;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public enum AchievementTier implements RewardsRubies, Icon {
    
    TIER_1(5, Material.BRICK),
    TIER_2(10, Material.IRON_INGOT),
    TIER_3(20, Material.GOLD_INGOT),
    TIER_4(50, Material.RESIN_BRICK);
    
    private final int rubyReward;
    private final Material material;
    
    AchievementTier(int rubyReward, @NotNull Material material) {
        this.rubyReward = rubyReward;
        this.material = material;
    }
    
    @Override
    public @NotNull ItemBuilder createBuilder() {
        return new ItemBuilder(material);
    }
    
    @Override
    public int getRubyReward() {
        return rubyReward;
    }
    
}
