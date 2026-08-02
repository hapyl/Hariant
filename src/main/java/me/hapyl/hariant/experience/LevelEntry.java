package me.hapyl.hariant.experience;

import io.papermc.paper.registry.keys.SoundEventKeys;
import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.component.Components;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.PlayerDatabaseEntry;
import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.MongoSerializableConstructor;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.reward.Reward;
import me.hapyl.hariant.util.ComponentShine;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.util.Mth;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class LevelEntry extends PlayerDatabaseEntry implements ComponentLike {
    
    private static final ComponentShine LEVEL_UP_SHINE = ComponentShine.builder("ʟᴇᴠᴇʟ ᴜᴘ")
                                                                       .style(Style.style(TextColor.color(0x5BFF6A), TextDecoration.BOLD))
                                                                       .styleFade(Style.style(TextColor.color(0x85FF6A), TextDecoration.BOLD))
                                                                       .styleShine(Style.style(TextColor.color(0x44A84D), TextDecoration.BOLD))
                                                                       .build();
    private static final int REWARDS_DISPLAY_LIMIT = 3;
    private static final int LEVEL_FEED_LENGTH = 5;
    
    private final @NotNull CachedLevel cachedLevel;
    
    private long experience;
    
    private @MongoSerializableConstructor LevelEntry(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull String parent) {
        super(database, document, parent);
        
        this.cachedLevel = new CachedLevel();
        this.experience = 0;
    }
    
    public long getExperience() {
        return experience;
    }
    
    public @NotNull Result addExperience(@NotNull PlayerProfile profile, long amount, boolean silent) {
        // TODO (xanyjl @ Tuesday, July 28) -> Maybe introduce an event to allow double-exp?
        
        final long experienceBeforeIncrement = experience;
        final long experienceAfterIncrement = experience + amount;
        
        final Level levelBeforeIncrement = Level.forExperience(experienceBeforeIncrement);
        final Level levelAfterIncrement = Level.forExperience(experienceAfterIncrement);
        
        this.experience = experienceAfterIncrement;
        
        // If level increased after the increment, notify player
        if (levelAfterIncrement.isHigher(levelBeforeIncrement) && !silent) {
            this.notifyLevelUp(profile.getPlayer(), levelBeforeIncrement, levelAfterIncrement);
        }
        
        // Trigger update
        this.onExperienceChanged(profile);
        
        return new Result(experienceBeforeIncrement, experienceAfterIncrement, levelBeforeIncrement, levelAfterIncrement);
    }
    
    @EventLike
    public void onExperienceChanged(@NotNull PlayerProfile profile) {
        // Update vanilla experience bar
        final Player player = profile.getPlayer();
        
        player.setLevel(this.getLevel());
        player.setExp(this.getProgressUntilNextLevel());
    }
    
    public float getProgressUntilNextLevel() {
        final int level = this.getLevel();
        
        final long experienceForCurrentLevel = Level.experienceForLevel(level);
        final long experienceForNextLevel = Level.experienceForLevel(level + 1);
        
        final long experienceIntoCurrentLevel = experience - experienceForCurrentLevel;
        final long experienceUntilNextLevel = experienceForNextLevel - experienceForCurrentLevel;
        
        return Mth.clamp((float) experienceIntoCurrentLevel / experienceUntilNextLevel, 0.0f, 1.0f);
    }
    
    public void notifyLevelUp(@NotNull Player player, @NotNull Level from, @NotNull Level to) {
        if (from.isOrHigher(to)) {
            throw new IllegalArgumentException("Cannot level up backwards or to the same level!");
        }
        
        final Component fromToComponent = Component.empty()
                                                   .append(from.asComponent())
                                                   .append(Component.text(" ➠ ", Colors.GREEN))
                                                   .append(to.asComponent());
        
        // Display shine
        LEVEL_UP_SHINE.display(player, fromToComponent, 1, 20);
        
        // Display chat
        player.sendMessage(Component.empty());
        player.sendMessage(Components.center(Component.text("LEVEL UP!", Colors.EXPERIENCE, TextDecoration.BOLD)));
        player.sendMessage(Components.center(fromToComponent));
        player.sendMessage(Component.empty());
        
        player.sendMessage(Components.center(Component.text("ʀᴇᴡᴀʀᴅꜱ", Colors.WHITE, TextDecoration.BOLD)));
        
        // Sum up rewards `from + 1` until `to`
        final List<? extends Reward> rewards = to.getRewards();
        final List<? extends Reward> upToThreeImportantRewards = rewards.stream().sorted(Comparator.comparingInt(Reward::priority)).limit(REWARDS_DISPLAY_LIMIT).toList();
        
        for (Reward reward : upToThreeImportantRewards) {
            player.sendMessage(Components.center(reward.getName()));
        }
        
        // If there are more rewards, display ...n more
        final int numberOfMoreRewards = rewards.size() - upToThreeImportantRewards.size();
        
        if (numberOfMoreRewards > 0) {
            player.sendMessage(Components.center(Component.text("...and %s more!".formatted(numberOfMoreRewards), Colors.DARK_GRAY)));
        }
        
        player.sendMessage(Component.empty());
        player.sendMessage(Components.center(Component.text("Claim rewards in your profile!", Colors.YELLOW)));
        player.sendMessage(Component.empty());
        
        // Play sound
        player.playSound(Sound.sound(SoundEventKeys.ENTITY_PLAYER_LEVELUP, Sound.Source.UI, 3, 0.75f));
        player.playSound(Sound.sound(SoundEventKeys.ENTITY_PLAYER_LEVELUP, Sound.Source.UI, 3, 1.25f));
    }
    
    public int getLevel() {
        // If experience has changed, update the cached level
        if (cachedLevel.experience != experience) {
            cachedLevel.experience = experience;
            cachedLevel.level = Level.forExperience(experience).getLevel();
        }
        
        return cachedLevel.level;
    }
    
    @Override
    public void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        document.put("experience", experience);
    }
    
    @Override
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        this.experience = document.get("experience", 0L);
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return Level.forExperience(experience).asComponent();
    }
    
    public void reset() {
        experience = 0;
    }
    
    public @NotNull Level[] createLevelFeed(int currentLevel) {
        final Level[] feed = new Level[LEVEL_FEED_LENGTH];
        final int indexStart = Math.clamp(currentLevel - 1, HariantConstants.MIN_LEVEL, HariantConstants.MAX_LEVEL - LEVEL_FEED_LENGTH + 1);
        
        for (int i = 0; i < feed.length; i++) {
            feed[i] = Level.forLevel(indexStart + i);
        }
        
        return feed;
    }
    
    public @NotNull List<? extends Reward> getUnclaimedRewards() {
        final PlayerProfile profile = getProfile();
        final int currentLevel = this.getLevel();
        
        return Level.streamLevels()
                    .filter(level -> level.getLevel() <= currentLevel)
                    .flatMap(level -> {
                        return level.getRewards()
                                    .stream()
                                    .filter(Predicate.not(reward -> reward.hasClaimed(profile)));
                    })
                    .toList();
    }
    
    public boolean hasUnclaimedRewards() {
        return !this.getUnclaimedRewards().isEmpty();
    }
    
    public record Result(long experienceBeforeAdd, long experienceAfterAdd, @NotNull Level levelBeforeAdd, @NotNull Level levelAfterAdd) {
        
        public boolean hasExperienceChanged() {
            return experienceBeforeAdd != experienceAfterAdd;
        }
        
        public boolean hasLevelChanged() {
            return levelBeforeAdd.getLevel() != levelAfterAdd.getLevel();
        }
        
    }
    
    private static class CachedLevel {
        
        private long experience;
        private int level;
        
        CachedLevel() {
            this.experience = 0;
            this.level = 1;
        }
        
    }
    
}