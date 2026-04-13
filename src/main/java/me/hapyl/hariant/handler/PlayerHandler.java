package me.hapyl.hariant.handler;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.hapyl.eterna.module.inventory.menu.PlayerMenu;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.database.rank.RankFormatter;
import me.hapyl.hariant.entity.frozen.FrozenHandler;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.profile.message.EnumMessageChannel;
import me.hapyl.hariant.profile.message.MessageChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Input;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public final class PlayerHandler implements Listener {
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerJoinEvent(PlayerJoinEvent ev) {
        final Player player = ev.getPlayer();
        final PlayerProfile profile = Hariant.createProfile(player);
        
        final PlayerRank playerRank = profile.getRank();
        final RankFormatter rankFormatter = playerRank.formatter();
        final boolean displayJoinMessages = rankFormatter.displayJoinMessages();
        
        ev.joinMessage(
                displayJoinMessages
                ? Component.empty()
                           .append(Component.text("[", NamedTextColor.DARK_GRAY))
                           .append(Component.text("+", NamedTextColor.GREEN))
                           .append(Component.text("]", NamedTextColor.DARK_GRAY))
                           .appendSpace()
                           .append(rankFormatter.format(profile))
                : null
        );
        
        // Display staff join messages
        if (playerRank.isStaff()) {
            // TODO @Feb 25, 2026 (xanyjl) ->
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void handlePlayerQuitEvent(PlayerQuitEvent ev) {
        final Player player = ev.getPlayer();
        final PlayerProfile profile = Hariant.getPlayerProfile(player);
        
        final RankFormatter rankFormatter = profile.getRank().formatter();
        final boolean displayJoinMessages = rankFormatter.displayJoinMessages();
        
        ev.quitMessage(
                displayJoinMessages
                ? Component.empty()
                           .append(Component.text("[", NamedTextColor.DARK_GRAY))
                           .append(Component.text("-", NamedTextColor.RED))
                           .append(Component.text("]", NamedTextColor.DARK_GRAY))
                           .appendSpace()
                           .append(rankFormatter.format(profile))
                : null
        );
        
        Hariant.destroyProfile(player);
    }
    
    @EventHandler
    public void handlePlayerItemHeldEvent(PlayerItemHeldEvent ev) {
        Hariant.getPlayer(ev.getPlayer()).ifPresent(player -> {
            final HeroInstance heroInstance = player.getHeroInstance();
            ev.setCancelled(true);
            
            heroInstance.getOrigin().handleItemHeldEvent(player, heroInstance, ev);
        });
    }
    
    @EventHandler
    public void handlePlayerSwapHandItemsEvent(PlayerSwapHandItemsEvent ev) {
        final Player bukkitPlayer = ev.getPlayer();
        
        // Always cancel swaps unless in creative
        if (bukkitPlayer.getGameMode() != GameMode.CREATIVE) {
            ev.setCancelled(true);
        }
        
        Hariant.getPlayer(bukkitPlayer).ifPresent(player -> {
            final HeroInstance heroInstance = player.getHeroInstance();
            
            heroInstance.getOrigin().handleSwapHandItemsEvent(player, heroInstance, ev);
        });
    }
    
    @EventHandler
    public void handleFoodLevelChangeEvent(FoodLevelChangeEvent ev) {
        ev.setCancelled(true);
    }
    
    @EventHandler
    public void handleBlockBreakEvent(BlockBreakEvent ev) {
        if (ev.getPlayer().getGameMode() != GameMode.CREATIVE) {
            ev.setCancelled(true);
        }
    }
    
    @EventHandler
    public void handleBlockPlaceEvent(BlockPlaceEvent ev) {
        if (ev.getPlayer().getGameMode() != GameMode.CREATIVE) {
            ev.setCancelled(true);
        }
    }
    
    @EventHandler
    public void handleInventoryClickEvent(InventoryClickEvent ev) {
        final Player player = (Player) ev.getWhoClicked();
        final boolean isCreative = player.getGameMode() == GameMode.CREATIVE;
        
        // If in a game, always cancel inventory clicks, otherwise only cancel if not in a menu or not in a creative
        if (Hariant.isGameInProgress() || (PlayerMenu.getPlayerMenu(player).isEmpty() && !isCreative)) {
            ev.setCancelled(true);
        }
        
        // Show debug to creative players
        if (ev.getClick() == ClickType.DOUBLE_CLICK && isCreative) {
            final int slot = ev.getRawSlot();
            final int mod8 = slot % 9;
            
            player.sendRichMessage("<rainbow>You clicked %s slot (%s %% 9)</rainbow>".formatted(slot, mod8));
            ev.setCancelled(true);
        }
    }
    
    @EventHandler
    public void handlePlayerInteractEvent(PlayerInteractEvent ev) {
        final Player player = ev.getPlayer();
        final Block clickedBlock = ev.getClickedBlock();
        final boolean isCreative = player.getGameMode() == GameMode.CREATIVE;
        
        if (clickedBlock == null || isCreative) {
            return;
        }
        
        // We only have to cancel block interaction, since we must keep item usage for bows
        ev.setUseInteractedBlock(Event.Result.DENY);
    }
    
    @EventHandler
    public void handlePlayerDropItemEvent(PlayerDropItemEvent ev) {
        final Player player = ev.getPlayer();
        
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        
        ev.setCancelled(true);
    }
    
    @EventHandler
    public void handleAsyncChatEvent(AsyncChatEvent ev) {
        final Player player = ev.getPlayer();
        
        // We always cancel the event because we'll need to implement
        // per-player message checks
        ev.setCancelled(true);
        
        final PlayerProfile profile = Hariant.getPlayerProfile(player);
        Component originalMessage = ev.originalMessage();
        
        // No fucking idea who can original message not be a TextComponent ¯\_(ツ)_/¯
        if (!(originalMessage instanceof TextComponent textComponent)) {
            return;
        }
        
        final MessageChannel messageChannel = EnumMessageChannel.getChannel(profile, textComponent);
        final Pattern lookupPattern = messageChannel.lookupPattern();
        
        final PlayerRank playerRank = profile.getRank();
        final RankFormatter rankFormatter = playerRank.formatter();
        
        final Component prefix = rankFormatter.format(profile);
        
        // TODO @Feb 16, 2026 (xanyjl) -> Implement message checks & per-player format
        
        // Remove the lookupPattern from the message
        if (lookupPattern != null) {
            originalMessage = originalMessage.replaceText(builder -> builder.match(lookupPattern).replacement(""));
        }
        
        // Create message
        final TextComponent message = Component.empty()
                                               .append(messageChannel.channelPrefix(profile))
                                               .append(prefix)
                                               .append(Component.text(":", NamedTextColor.GRAY))
                                               .appendSpace()
                                               .append(originalMessage.style(rankFormatter.getMessageStyle()));
        
        messageChannel.recipients(profile).forEach(recipient -> recipient.sendMessage(message));
    }
    
    @EventHandler
    public void handlePlayerInputEvent(PlayerInputEvent ev) {
        final Player player = ev.getPlayer();
        final Input input = ev.getInput();
        
        // If input isn't directional, ignore it
        if (!isInputDirectional(input)) {
            return;
        }
        
        Hariant.getPlayer(player).ifPresent(harp -> {
            final FrozenHandler frozenHandler = harp.getFrozenHandler();
            
            if (frozenHandler != null) {
                frozenHandler.input(input);
            }
        });
    }
    
    private static boolean isInputDirectional(@NotNull Input input) {
        return input.isForward() || input.isBackward() || input.isLeft() || input.isRight();
    }
    
}
