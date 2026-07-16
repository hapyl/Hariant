package me.hapyl.hariant.menu;

import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.achievement.AchievementCategory;
import me.hapyl.hariant.inventory.item.resource.ResourceRuby;
import me.hapyl.hariant.menu.achievement.MenuAchievement;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MenuPlayerProfile extends Menu {
    
    private static final Component COMING_SOON = Component.text("ᴄᴏᴍɪɴɢ ꜱᴏᴏɴ", Colors.ERROR);
    private static final PlayerMenuAction COMING_SOON_ACTION = PlayerMenuAction.of(player -> HariantLogger.error(player, Component.text("This feature is coming soon!")));
    
    public MenuPlayerProfile(@NotNull Player player) {
        super(player, () -> Component.text("Your Profile"), ChestSize.SIZE_6);
        
        this.openMenu();
    }
    
    @Override
    public void updateMenu() {
        setItem(
                20,
                new ItemBuilder(Material.CHEST)
                        .setName(Component.text("Inventory"))
                        .addLore()
                        .addWrappedLore(Component.text("Browse and manage your items and resources."))
                        .addLore()
                        .addLore(COMING_SOON)
                        .asIcon(),
                COMING_SOON_ACTION
        );
        
        setItem(
                31,
                ItemBuilder.playerHead("87d885b32b0dd2d6b7f1b582a34186f8a5373c46589a273423132b448b803462")
                           .setName(Component.text("Levelling"))
                           .addLore()
                           .addWrappedLore(
                                   Component.empty()
                                            .append(Component.text("Earn experience by playing the game to unlock "))
                                            .append(Component.text("unique", Colors.LIGHT_PURPLE))
                                            .append(Component.text(" rewards!"))
                           )
                           .addLore()
                           .addLore(COMING_SOON)
                           .asIcon(),
                COMING_SOON_ACTION
        );
        
        setItem(
                24,
                createAchievementItem(),
                PlayerMenuAction.of(player -> new MenuAchievement(player, AchievementCategory.GAMEPLAY))
        );
        
        setFooter(
                6,
                new ItemBuilder(Material.COMPARATOR)
                        .setName(Component.text("Settings"))
                        .addLore()
                        .addWrappedLore(Component.text("Customize the personal personal experience to your liking."))
                        .addLore()
                        .addLore(ButtonComponents.left("open settings"))
                        .asIcon(),
                PlayerMenuAction.of(MenuSettings::new)
        );
    }
    
    private @NotNull ItemStack createAchievementItem() {
        final ItemBuilder builder = new ItemBuilder(Material.DIAMOND)
                .setName(Component.text("Achievements"))
                .addLore()
                .addWrappedLore(
                        Component.empty()
                                 .append(Component.text("Complete achievements to earn "))
                                 .appendNewline()
                                 .append(ResourceRuby.PREFIX)
                                 .appendSpace()
                                 .append(Component.text("rubies", Colors.RESOURCE_RUBY))
                                 .append(Component.text("!"))
                );
        
        final int unclaimedRewards = profile.getDatabase().achievements.countUnclaimedRewards();
        
        if (unclaimedRewards > 0) {
            builder.addLore();
            builder.addLore(Component.text("You have %s unclaimed rewards!".formatted(unclaimedRewards), Colors.GREEN));
        }
        
        builder.addLore();
        builder.addLore(ButtonComponents.left("open achievements menu"));
        
        return builder.asIcon();
    }
    
}