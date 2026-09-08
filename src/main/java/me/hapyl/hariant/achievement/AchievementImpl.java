package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.annotate.AutoRegisteredListener;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.util.ComponentShine;
import me.hapyl.hariant.util.FireworkHelper;
import me.hapyl.hariant.util.ShowTextBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AutoRegisteredListener
public class AchievementImpl implements Achievement {
    
    private static final String STRING_ACHIEVEMENT_MADE = "ᴀᴄʜɪᴇᴠᴇᴍᴇɴᴛ ᴍᴀᴅᴇ";
    
    private static final Style STYLE = Style.style(TextColor.color(0xFF8222), TextDecoration.BOLD);
    private static final Style STYLE_SHINE = Style.style(TextColor.color(0xFFB90E), TextDecoration.BOLD);
    private static final Style STYLE_FADE = Style.style(TextColor.color(0xFFE418), TextDecoration.BOLD);
    
    private static final Component COMPONENT_ACHIEVEMENT_MADE = Component.text(STRING_ACHIEVEMENT_MADE, STYLE);
    private static final ComponentShine ACHIEVEMENT_MADE = ComponentShine.builder(STRING_ACHIEVEMENT_MADE)
                                                                         .style(STYLE)
                                                                         .styleShine(STYLE_SHINE)
                                                                         .styleFade(STYLE_FADE)
                                                                         .build();
    
    private final Key key;
    private final double goal;
    
    private final Component name;
    private final Component description;
    
    private @NotNull AchievementCategory category;
    private @NotNull AchievementTier tier;
    
    private @Nullable Achievement parent;
    
    private boolean hidden;
    
    public AchievementImpl(@NotNull Key key, final double goal, @NotNull Component name, @NotNull Component description) {
        this.key = key;
        this.goal = goal;
        this.name = name;
        this.description = description;
        this.category = AchievementCategory.GENESIS;
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
    
    @Override
    public @Nullable Achievement getParent() {
        return parent;
    }
    
    public void setParent(@Nullable Achievement parent) {
        this.parent = parent;
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
        
        // Also show in chat
        final Component nameInGold = this.getName().color(Colors.GOLD);
        
        player.sendMessage(Component.empty());
        player.sendMessage(COMPONENT_ACHIEVEMENT_MADE);
        player.sendMessage(
                Component.space()
                         .hoverEvent(
                                 ShowTextBuilder.builder()
                                                .append(nameInGold)
                                                .appendNewline()
                                                .appendWrapped(this.getDescription())
                         )
                         .append(nameInGold)
        );
        player.sendMessage(Component.empty());
        
        // Sfx
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 3, 1.0f);
        
        // Spawn firework
        FireworkHelper.detonate(
                player.getLocation().add(0, player.getHeight() * 0.5, 0),
                edit -> edit.addEffects(
                        FireworkEffect.builder()
                                      .withColor(Color.ORANGE, Color.YELLOW)
                                      .withFlicker()
                                      .build()
                )
        );
    }
    
    @Override
    public void onRewardsClaimed(@NotNull Player player, @NotNull AchievementProgress achievementProgress) {
    }
    
    @Override
    public final @NotNull AchievementUniqueIdCounter createUniqueIdCounter(@NotNull PlayerProfile profile) {
        return new AchievementUniqueIdCounter(this, profile, uniqueIdCounterValue());
    }
    
    @Override
    public int uniqueIdCounterValue() {
        // Default to throwing illegal state exception to prevent creating counters for unsupported achievements; supported achievements
        // must override this method and return the expected counter value
        throw new IllegalStateException("Achievement %s does not support unique id counter!".formatted(this));
    }
    
    public void setTier(@NotNull AchievementTier tier) {
        this.tier = tier;
    }
    
    @Override
    public String toString() {
        return key.toString();
    }
    
}