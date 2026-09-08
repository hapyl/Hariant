package me.hapyl.hariant.daily;

import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.eterna.module.player.PlayerAction;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.inventory.item.ResourceRegistry;
import me.hapyl.hariant.menu.Menu;
import me.hapyl.hariant.menu.MenuPlayerProfile;
import me.hapyl.hariant.menu.MenuReturn;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MenuDaily extends Menu {
    
    private static final int[] SLOTS = { 20, 22, 24 };
    
    public MenuDaily(@NotNull Player player) {
        super(player, () -> Component.text("Daily Bonds"), ChestSize.SIZE_6);
        
        this.openMenu();
    }
    
    @Override
    public @NotNull MenuReturn menuReturn() {
        return MenuReturn.create(Component.text("Player Profile"), MenuPlayerProfile::new);
    }
    
    @Override
    public void updateMenu() {
        final DailyEntry dailyEntry = profile.getDatabase().daily;
        final @NotNull DailyInstance[] dailyInstances = dailyEntry.getDailyInstances();
        
        for (int i = 0; i < dailyInstances.length; i++) {
            final DailyInstance dailyInstance = dailyInstances[i];
            
            this.setItem(SLOTS[i], dailyInstance.createBuilder().asIcon());
            
            // Set rewards item
            final boolean completed = dailyInstance.isCompleted();
            final boolean claimedRewards = dailyInstance.isClaimedRewards();
            
            final ItemBuilder builder = new ItemBuilder(claimedRewards ? Material.MINECART : Material.CHEST_MINECART);
            builder.setName(Component.text("Rewards"));
            builder.addLore();
            
            final DailyType daily = dailyInstance.getDaily();
            final DailyTier tier = daily.getTier();
            
            // Append rewards
            builder.addLore(formatResource(tier.getCoinsReward(), Colors.RESOURCE_CAT_COINS, ResourceRegistry.CAT_COINS.getName()));
            builder.addLore(formatResource(tier.getCoinsReward(), Colors.EXPERIENCE, Component.text("Experience")));
            builder.addLore(formatResource(tier.getRubyReward(), Colors.RESOURCE_RUBY, ResourceRegistry.RUBY.getName()));
            
            builder.addLore();
            
            final PlayerAction playerAction;
            
            if (completed) {
                if (claimedRewards) {
                    builder.addLore(Component.text("Rewards Claimed!", Colors.SUCCESS));
                    playerAction = player -> {
                        HariantLogger.error(player, Component.text("Rewards are already claimed!"));
                        HariantLogger.sound(player, Sound.BLOCK_ANVIL_LAND, 1.0f);
                    };
                }
                else {
                    builder.glow();
                    builder.addLore(ButtonComponents.left("claim"));
                    
                    playerAction = player -> {
                        if (dailyInstance.claimRewards(profile)) {
                            HariantLogger.success(player, Component.text("Successfully claimed rewards!"));
                            HariantLogger.sound(player, Sound.ENTITY_PLAYER_LEVELUP, 2.0f);
                            
                            this.openMenu();
                        }
                        else {
                            HariantLogger.error(player, Component.text("Failed to claimed rewards, try again later!"));
                            HariantLogger.sound(player, Sound.ENTITY_VILLAGER_NO, 1.0f);
                            
                            this.closeMenu();
                        }
                    };
                }
            }
            else {
                builder.addLore(Component.text("Cannot claim yet!", Colors.ERROR));
                playerAction = player -> {
                    HariantLogger.error(player, Component.text("You haven't completed the bond yet!"));
                    HariantLogger.sound(player, Sound.ENTITY_VILLAGER_NO, 1.0f);
                };
            }
            
            this.setItem(SLOTS[i] + 9, builder.asIcon(), PlayerMenuAction.of(playerAction));
        }
        
    }
    
    private static @NotNull Component formatResource(int value, @NotNull TextColor color, @NotNull Component resourceName) {
        return Component.space()
                        .append(Component.text("%,d".formatted(value), color))
                        .appendSpace()
                        .append(resourceName.color(color));
    }
    
}
