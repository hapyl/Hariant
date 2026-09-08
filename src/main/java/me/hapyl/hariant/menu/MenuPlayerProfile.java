package me.hapyl.hariant.menu;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.PlayerMenu;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.achievement.AchievementCategory;
import me.hapyl.hariant.achievement.AchievementEntry;
import me.hapyl.hariant.achievement.AchievementRegistry;
import me.hapyl.hariant.daily.DailyEntry;
import me.hapyl.hariant.daily.MenuDaily;
import me.hapyl.hariant.experience.Level;
import me.hapyl.hariant.experience.MenuLevelling;
import me.hapyl.hariant.inventory.item.resource.ResourceCatCoins;
import me.hapyl.hariant.inventory.item.resource.ResourceRuby;
import me.hapyl.hariant.menu.achievement.MenuAchievement;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.profile.notification.NotificationHandler;
import me.hapyl.hariant.profile.notification.NotificationResult;
import me.hapyl.hariant.util.SlotBound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MenuPlayerProfile extends Menu {
    
    private static final Component COMING_SOON = Component.text("ᴄᴏᴍɪɴɢ ꜱᴏᴏɴ", Colors.ERROR);
    
    private final Map<Type, Boolean> lastItemValue;
    
    public MenuPlayerProfile(@NotNull Player player) {
        super(player, () -> Component.text("Your Profile"), ChestSize.SIZE_6);
        
        this.lastItemValue = Maps.newEnumMap(Type.class);
        
        this.openMenu();
    }
    
    @Override
    public void updateMenu() {
        this.lastItemValue.clear();
        this.updateMenu0();
        
        // Footer
        setFooter(
                6,
                new ItemBuilder(Material.COMPARATOR)
                        .setName(Component.text("Settings"))
                        .addLore()
                        .addWrappedLore(Component.text("Customize the personal experience to your liking."))
                        .addLore()
                        .addLore(ButtonComponents.left("open settings"))
                        .asIcon(),
                PlayerMenuAction.of(MenuSettings::new)
        );
    }
    
    private void setItem(@NotNull MenuPlayerProfile.Type type, boolean blink) {
        // Do not update the item if the value is already set, causes annoying blinking for custom heads
        if (this.lastItemValue.get(type) instanceof Boolean value && value == blink) {
            return;
        }
        
        this.lastItemValue.put(type, blink);
        this.setItem(type.getSlot(), NotificationHandler.setTexture(type.createBuilder(profile), blink ? 1 : 0).asIcon(), type);
    }
    
    private void updateMenu0() {
        setItem(Type.INVENTORY, false);
        setItem(Type.LEVEL, false);
        setItem(Type.DAILY, false);
        setItem(Type.ACHIEVEMENT, false);
    }
    
    public static void update(@NotNull PlayerProfile profile, @Nullable NotificationResult notificationResult) {
        if (!(PlayerMenu.getPlayerMenu(profile.getPlayer()).orElse(null) instanceof MenuPlayerProfile menuPlayerProfile)) {
            return;
        }
        
        // Update the menu with defaults values to the items are normal
        if (notificationResult == null) {
            menuPlayerProfile.updateMenu0();
            return;
        }
        
        // Otherwise blink based on if notifications have the type
        menuPlayerProfile.setItem(Type.INVENTORY, false);
        menuPlayerProfile.setItem(Type.LEVEL, notificationResult.hasOf(Level.class));
        menuPlayerProfile.setItem(Type.DAILY, notificationResult.hasOf(DailyEntry.class));
        menuPlayerProfile.setItem(Type.ACHIEVEMENT, notificationResult.hasOf(AchievementEntry.class));
    }
    
    public enum Type implements SlotBound, PlayerMenuAction {
        
        INVENTORY(19) {
            @Override
            public @NotNull ItemBuilder createBuilder(@NotNull PlayerProfile profile) {
                return new ItemBuilder(Material.CHEST)
                        .setName(Component.text("Inventory"))
                        .addLore()
                        .addWrappedLore(Component.text("Browse and manage your items and resources."))
                        .addLore()
                        .addLore(COMING_SOON);
            }
            
            @Override
            public void use(@NotNull PlayerMenu menu, @NotNull Player player, @NotNull ClickType clickType, int slot, int hotbarNumber) {
                HariantLogger.error(player, Component.text("This feature is coming soon!"));
                HariantLogger.sound(player, Sound.ENTITY_VILLAGER_NO, 1.0f);
            }
            
        },
        
        LEVEL(30) {
            @Override
            public @NotNull ItemBuilder createBuilder(@NotNull PlayerProfile profile) {
                final ItemBuilder builder = ItemBuilder.playerHead("87d885b32b0dd2d6b7f1b582a34186f8a5373c46589a273423132b448b803462")
                                                       .setName(Component.text("Levelling"))
                                                       .addLore()
                                                       .addWrappedLore(
                                                               Component.empty()
                                                                        .append(Component.text("Earn experience by playing the game to unlock "))
                                                                        .append(Component.text("unique", Colors.LIGHT_PURPLE))
                                                                        .append(Component.text(" rewards and perks!"))
                                                       );
                
                unclaimedRewardsComponent(builder, profile.getDatabase().level.getUnclaimedRewards().size());
                
                builder.addLore();
                builder.addLore(ButtonComponents.left("open levelling menu"));
                
                return builder;
            }
            
            @Override
            public void use(@NotNull PlayerMenu menu, @NotNull Player player, @NotNull ClickType clickType, int slot, int hotbarNumber) {
                new MenuLevelling(player);
            }
            
        },
        
        DAILY(32) {
            @Override
            public @NotNull ItemBuilder createBuilder(@NotNull PlayerProfile profile) {
                final DailyEntry dailyEntry = profile.getDatabase().daily;
                final int unclaimedRewards = dailyEntry.countUnclaimedRewards();
                
                final ItemBuilder builder = ItemBuilder.playerHead("86f1c9ecbcd49842dcbe9a3d85abba6479ea46bdd424dbd9d0ef54c28bf502d7")
                                                       .setName(Component.text("Daily Bonds"))
                                                       .addLore()
                                                       .addWrappedLore(
                                                               Component.empty()
                                                                        .append(Component.text("Complete daily challenges to earn "))
                                                                        .appendNewline()
                                                                        .append(ResourceCatCoins.PREFIX)
                                                                        .append(Component.text(" catcoins", Colors.RESOURCE_CAT_COINS))
                                                                        .append(Component.text(", "))
                                                                        .append(Level.COMPONENT_PREFIX)
                                                                        .append(Component.text(" experience", Colors.EXPERIENCE))
                                                                        .append(Component.text(", and "))
                                                                        .appendNewline()
                                                                        .append(ResourceRuby.PREFIX)
                                                                        .append(Component.text(" rubies", Colors.RESOURCE_RUBY))
                                                                        .append(Component.text("!"))
                                                       );
                
                // Append completion
                builder.addLore();
                builder.addLore(Component.text("Today's Dailies ").append(Components.makeComponentFractional(dailyEntry.countCompleteDailies(), DailyEntry.NUMBER_OF_DAILIES)));
                
                // Append reset time
                builder.addLore();
                builder.addLore(Component.text("Dailies reset in ", Colors.RED).append(Hariant.getTimeUntilResetFormatted().color(Colors.RED)));
                
                unclaimedRewardsComponent(builder, unclaimedRewards);
                
                // Append button
                builder.addLore();
                builder.addLore(ButtonComponents.left("open dailies menu"));
                
                return builder;
            }
            
            @Override
            public void use(@NotNull PlayerMenu menu, @NotNull Player player, @NotNull ClickType clickType, int slot, int hotbarNumber) {
                new MenuDaily(player);
            }
        },
        
        ACHIEVEMENT(25) {
            @Override
            public @NotNull ItemBuilder createBuilder(@NotNull PlayerProfile profile) {
                final AchievementEntry achievementEntry = profile.getDatabase().achievements;
                final int unclaimedRewards = achievementEntry.countUnclaimedRewards();
                
                final ItemBuilder builder = new ItemBuilder(Material.DIAMOND)
                        .setName(Component.text("Achievements"))
                        .addLore()
                        .addWrappedLore(
                                Component.empty()
                                         .append(Component.text("Complete various achievements to earn "))
                                         .appendNewline()
                                         .append(ResourceRuby.PREFIX)
                                         .appendSpace()
                                         .append(Component.text("rubies", Colors.RESOURCE_RUBY))
                                         .append(Component.text("!"))
                        )
                        .addLore()
                        .addLore(
                                Component.empty()
                                         .append(Component.text("Achievements: "))
                                         .append(Components.makeComponentFractional(achievementEntry.countCompletedAchievements(), AchievementRegistry.totalNumberOfAchievements()))
                        );
                
                unclaimedRewardsComponent(builder, unclaimedRewards);
                
                builder.addLore();
                builder.addLore(ButtonComponents.left("open achievements menu"));
                
                return builder;
            }
            
            @Override
            public void use(@NotNull PlayerMenu menu, @NotNull Player player, @NotNull ClickType clickType, int slot, int hotbarNumber) {
                new MenuAchievement(player, AchievementCategory.GENESIS);
            }
            
        };
        
        private final int slot;
        
        Type(int slot) {
            this.slot = slot;
        }
        
        @Override
        public int getSlot() {
            return slot;
        }
        
        public @NotNull ItemBuilder createBuilder(@NotNull PlayerProfile profile) {
            return new ItemBuilder(Material.BARRIER);
        }
        
        @Override
        public void use(@NotNull PlayerMenu menu, @NotNull Player player, @NotNull ClickType clickType, int slot, int hotbarNumber) {
        }
        
        private static void unclaimedRewardsComponent(@NotNull ItemBuilder builder, int numberOfUnclaimedRewards) {
            if (numberOfUnclaimedRewards == 0) {
                return;
            }
            
            builder.glow();
            builder.addLore();
            builder.addLore(Component.text("You have %s unclaimed rewards!".formatted(numberOfUnclaimedRewards), Colors.GREEN, TextDecoration.UNDERLINED));
        }
        
    }
    
}