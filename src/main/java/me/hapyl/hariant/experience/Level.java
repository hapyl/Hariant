package me.hapyl.hariant.experience;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import me.hapyl.eterna.module.component.Styled;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.text.prefix.Prefix;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.command.HariantCommandOpenMenu;
import me.hapyl.hariant.inventory.item.ResourceRegistry;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.profile.notification.Notification;
import me.hapyl.hariant.profile.notification.NotificationListener;
import me.hapyl.hariant.profile.notification.NotificationType;
import me.hapyl.hariant.reward.Reward;
import me.hapyl.hariant.util.ComparableOrdinal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.List;
import java.util.NavigableMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class Level implements Styled, ComponentLike, ComparableOrdinal<Level> {
    
    public static final NotificationListener NOTIFICATION_LISTENER = new NotificationListener() {
        private final Notification notification = new Notification() {
            @Override
            public @NotNull Component getName() {
                return Component.text("Unclaimed Level Rewards");
            }
            
            @Override
            public @NotNull NotificationType getNotificationType() {
                return NotificationType.NORMAL;
            }
            
            @Override
            public @NotNull ClickEvent<?> clickEvent() {
                return HariantCommandOpenMenu.Menus.LEVELLING.createClickEvent();
            }
        };
        
        @Override
        public @Nullable Notification listen(@NotNull PlayerProfile profile) {
            return profile.getDatabase().level.hasUnclaimedRewards() ? notification : null;
        }
    };
    
    private static final Component BRACKET_LEFT = Component.text("[", Colors.DARK_GRAY);
    private static final Component BRACKET_RIGHT = Component.text("]", Colors.DARK_GRAY);
    
    private static final Prefix PREFIX = Prefix.create(Component.text("◆", Colors.EXPERIENCE), Component.space());
    
    private static final double CAT_COIN_REWARD_PER_LEVEL = 100;
    private static final double RUBY_REWARD_PER_FIVE_LEVELS = 5;
    
    private static final NavigableMap<Integer, ? extends Style> LEVEL_STYLES
            = ImmutableSortedMap.<Integer, Style>naturalOrder()
                                .put(1, Style.style(TextColor.color(0xAAAAAA)))
                                .put(5, Style.style(TextColor.color(0xFFFFFF)))
                                .put(10, Style.style(TextColor.color(0x55FF55)))
                                .put(15, Style.style(TextColor.color(0x00AA00)))
                                .put(20, Style.style(TextColor.color(0x00FFFF)))
                                .put(25, Style.style(TextColor.color(0x3B7FFF)))
                                .put(30, Style.style(TextColor.color(0x8B3FE0)))
                                .put(35, Style.style(TextColor.color(0xD670FF)))
                                .put(40, Style.style(TextColor.color(0xFFAA00)))
                                .put(45, Style.style(TextColor.color(0xFF3333)))
                                .put(50, Style.style(TextColor.color(0xE63232), TextDecoration.BOLD))
                                .build();
    
    private static final List<Level> LEVELS;
    
    private static final int PRESTIGE_COIN_MULTIPLIER = 2;
    
    static {
        // Setup levels
        LEVELS = Lists.newArrayList();
        
        for (int level = 0; level < HariantConstants.MAX_LEVEL; level++) {
            final long experience = (long) (HariantConstants.BASE_EXPERIENCE * level * Math.pow(level, HariantConstants.EXPERIENCE_EXPONENT));
            final long experienceRounded = Math.round(experience / 100.0) * 100;
            
            LEVELS.add(new Level(level + 1, experienceRounded));
        }
        
        // TODO (xanyjl @ Tuesday, July 28) -> Either make level 50 incredible hard to get or add level 51 with (totalExp * N)?
        
        // Setup levels
        setupLevel(
                1,
                level -> {
                
                }
        );
        
        setupLevel(
                3,
                level -> {
                    level.rewards.add(
                            Reward.ofResource(Key.ofString("level_3_hero_voucher"), ResourceRegistry.HERO_RECRUIT_VOUCHER, 2)
                    );
                }
        );
    }
    
    private final int level;
    private final long experience;
    private final boolean prestige;
    
    private @NotNull final Style style;
    private @NotNull final List<Reward> rewards;
    
    private Level(int level, long experience) {
        this.level = level;
        this.experience = experience;
        this.prestige = level > 1 && LEVEL_STYLES.containsKey(level);
        this.style = defaultStyle(level);
        this.rewards = defaultRewards(level, prestige);
    }
    
    public int getLevel() {
        return level;
    }
    
    public long getExperience() {
        return experience;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return Component.empty()
                        .append(BRACKET_LEFT)
                        .append(Component.text(level, style))
                        .append(BRACKET_RIGHT);
    }
    
    @Override
    public @NotNull Style getStyle() {
        return style;
    }
    
    public @NotNull List<Reward> getRewards() {
        return rewards;
    }
    
    @Override
    public int ordinal() {
        return level;
    }
    
    public boolean isPrestige() {
        return prestige;
    }
    
    public static @NotNull Level forExperience(long experience) {
        for (int i = LEVELS.size() - 1; i >= 0; i--) {
            final Level level = LEVELS.get(i);
            
            if (level.experience <= experience) {
                return level;
            }
        }
        
        return LEVELS.getFirst();
    }
    
    public static @NotNull Level forLevel(@Range(from = HariantConstants.MIN_LEVEL, to = HariantConstants.MAX_LEVEL) int level) {
        return LEVELS.get(level - 1);
    }
    
    public static @NotNull Stream<Level> streamLevels() {
        return LEVELS.stream();
    }
    
    public static long experienceForLevel(int level) {
        if (level < HariantConstants.MIN_LEVEL) {
            return 0;
        }
        else if (level > HariantConstants.MAX_LEVEL) {
            // Allow experience overflow
            return Long.MAX_VALUE;
        }
        
        return LEVELS.get(level - 1).getExperience();
    }
    
    public static @NotNull Prefix getPrefix() {
        return PREFIX;
    }
    
    private static @NotNull Style defaultStyle(int level) {
        return LEVEL_STYLES.floorEntry(level).getValue();
    }
    
    private static @NotNull List<Reward> defaultRewards(int level, boolean prestige) {
        final List<Reward> defaultRewards = Lists.newArrayList();
        
        final int rewardCatCoins = (int) (CAT_COIN_REWARD_PER_LEVEL * level) * (prestige ? PRESTIGE_COIN_MULTIPLIER : 1);
        final int rewardRuby = level % 5 == 0 ? (int) (RUBY_REWARD_PER_FIVE_LEVELS * level / 5) : 0;
        
        defaultRewards.add(Reward.ofResource(Key.ofString("level_%s_coins".formatted(level)), ResourceRegistry.CAT_COINS, rewardCatCoins));
        
        if (rewardRuby > 0) {
            defaultRewards.add(Reward.ofResource(Key.ofString("level_%s_rubies".formatted(level)), ResourceRegistry.RUBY, rewardRuby));
        }
        
        return defaultRewards;
    }
    
    private static void setupLevel(int level, @NotNull Consumer<Level> consumer) {
        consumer.accept(forLevel(level));
    }
    
}
