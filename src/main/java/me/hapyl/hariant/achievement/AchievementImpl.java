package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.annotate.AutoRegisteredListener;
import me.hapyl.hariant.util.ComponentShine;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@AutoRegisteredListener
public class AchievementImpl implements Achievement {
    
    private static final ComponentShine ACHIEVEMENT_MADE = ComponentShine.builder("ᴀᴄʜɪᴇᴠᴇᴍᴇɴᴛ ᴍᴀᴅᴇ")
                                                                         .style(Style.style(TextColor.color(0xFF8222), TextDecoration.BOLD))
                                                                         .styleShine(Style.style(TextColor.color(0xFFB90E), TextDecoration.BOLD))
                                                                         .styleFade(Style.style(TextColor.color(0xFFE418), TextDecoration.BOLD))
                                                                         .build();
    
    private final Key key;
    private final double goal;
    
    private @NotNull Component name;
    private @NotNull Component description;
    
    private @NotNull AchievementCategory category;
    private @NotNull AchievementTier tier;
    
    private boolean hidden;
    
    public AchievementImpl(@NotNull Key key, final double goal) {
        this.key = key;
        this.goal = goal;
        this.name = Component.text("Unnamed Achievement");
        this.description = Component.text("???");
        this.category = AchievementCategory.GAMEPLAY;
        this.tier = AchievementTier.TIER_1;
        this.hidden = false;
        
        AutoRegisteredListener.Registry.register(this);
    }
    
    @Override
    public final @NotNull Key getKey() {
        return key;
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public void setName(@NotNull Component name) {
        this.name = name;
    }
    
    @Override
    public @NotNull Component getDescription() {
        return description;
    }
    
    @Override
    public @NotNull ItemBuilder createBuilder() {
        final ItemBuilder builder = tier.createBuilder();
        builder.setName(name);
        builder.setAmount(tier.getRubyReward());
        
        builder.addLore();
        builder.addWrappedLore(description);
        
        return builder;
    }
    
    @Override
    public void setDescription(@NotNull Component description) {
        this.description = description;
    }
    
    @Override
    public double getGoal() {
        return goal;
    }
    
    @Override
    public @NotNull AchievementCategory getCategory() {
        return category;
    }
    
    public void setCategory(@NotNull AchievementCategory category) {
        this.category = category;
    }
    
    @Override
    public @NotNull AchievementTier getTier() {
        return tier;
    }
    
    public void setTier(@NotNull AchievementTier tier) {
        this.tier = tier;
    }
    
    @Override
    public void onRegister() {
    }
    
    @Override
    public void onUnregister() {
    }
    
    @Override
    public boolean isHidden() {
        return hidden;
    }
    
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
    
    @Override
    public boolean isProgressCapped() {
        return true;
    }
    
    @Override
    public void onProgress(@NotNull Player player, @NotNull AchievementProgress achievementProgress, double progressBefore, double progress) {
    }
    
    @Override
    public void onComplete(@NotNull Player player, @NotNull AchievementProgress achievementProgress) {
        ACHIEVEMENT_MADE.display(player, this.getName().color(Colors.ORANGE));
        
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 3, 1.0f);
    }
    
    @Override
    public void onRewardsClaimed(@NotNull Player player, @NotNull AchievementProgress achievementProgress) {
    }
    
}