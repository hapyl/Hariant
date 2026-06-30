package me.hapyl.hariant.handler;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.hapyl.eterna.module.annotate.SelfReturn;
import me.hapyl.eterna.module.inventory.menu.PlayerMenu;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.database.rank.RankFormatter;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.SitHandler;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.frozen.FrozenHandler;
import me.hapyl.hariant.entity.heal.HealingSource;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantHealEvent;
import me.hapyl.hariant.event.HariantMonitorDamageEvent;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.profile.message.EnumMessageChannel;
import me.hapyl.hariant.profile.setting.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
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
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public final class PlayerHandler implements Listener {
    
    private static final Component PREFIX_INCOMING_DAMAGE = createPrefix(Component.text("⚔", Colors.RED));
    private static final Component PREFIX_OUTGOING_DAMAGE = createPrefix(Component.text("⚔", Colors.GREEN));
    
    private static final Component PREFIX_INCOMING_HEALING = createPrefix(Component.text("❤", Colors.RED));
    private static final Component PREFIX_OUTGOING_HEALING = createPrefix(Component.text("❤", Colors.GREEN));
    
    private static final Style STYLE_IMPORTANT = Style.style(Colors.WHITE);
    
    private static final double MINIMUM_DAMAGE = 1.0;
    private static final double MINIMUM_HEALING = 1.0;
    
    public PlayerHandler() {
    }
    
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
                           .append(Component.text("[", Colors.DARK_GRAY))
                           .append(Component.text("+", Colors.GREEN))
                           .append(Component.text("]", Colors.DARK_GRAY))
                           .appendSpace()
                           .append(profile.getNameFormatted())
                : null
        );
        
        // Display staff join messages
        if (playerRank.isStaff()) {
            EnumMessageChannel.STAFF.message(
                    Component.empty()
                             .append(profile.getName().color(Colors.STAFF))
                             .append(Component.text(" joined.", Colors.STAFF))
            );
        }
        
        // Cancelling has go after the message because it looks goofy otherwise
        Hariant.cancelCountdown(
                Component.empty()
                         .append(Component.text("The countdown was cancelled because "))
                         .append(profile.getNameFormatted())
                         .append(Component.text(" has joined."))
        );
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void handlePlayerQuitEvent(PlayerQuitEvent ev) {
        final Player player = ev.getPlayer();
        final PlayerProfile profile = Hariant.getPlayerProfile(player);
        final PlayerRank playerRank = profile.getRank();
        final RankFormatter rankFormatter = playerRank.formatter();
        
        final boolean displayJoinMessages = rankFormatter.displayJoinMessages();
        
        ev.quitMessage(
                displayJoinMessages
                ? Component.empty()
                           .append(Component.text("[", Colors.DARK_GRAY))
                           .append(Component.text("-", Colors.RED))
                           .append(Component.text("]", Colors.DARK_GRAY))
                           .appendSpace()
                           .append(profile.getNameFormatted())
                : null
        );
        
        Hariant.destroyProfile(player);
        
        if (playerRank.isStaff()) {
            EnumMessageChannel.STAFF.message(
                    Component.empty()
                             .append(profile.getName().color(Colors.STAFF))
                             .append(Component.text(" left.", Colors.STAFF))
            );
        }
    }
    
    @EventHandler
    public void handlePlayerItemHeldEvent(PlayerItemHeldEvent ev) {
        Hariant.getPlayer(ev.getPlayer()).ifPresent(player -> {
            final HeroInstance heroInstance = player.getHeroInstance();
            final Hero hero = heroInstance.getOrigin();
            
            // TODO (xanyjl @ Tuesday, June 2) -> Condition for input talents (if you even add them)
            ev.setCancelled(true);
            
            // Always snap to weapon
            player.getInventory().setHeldItemSlot(hero.getWeaponSlot(player));
            
            hero.handleItemHeldEvent(player, heroInstance, ev);
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
        
        // We always cancel the event because we'll need to use custom message logic
        ev.setCancelled(true);
        
        final PlayerProfile profile = Hariant.getPlayerProfile(player);
        Component originalMessage = ev.originalMessage();
        
        // No fucking idea how can original message not be a TextComponent ¯\_(ツ)_/¯
        if (!(originalMessage instanceof TextComponent textComponent)) {
            return;
        }
        
        final EnumMessageChannel messageChannel = EnumMessageChannel.fromMessage(profile, textComponent);
        final Pattern lookupPattern = messageChannel.lookupPattern();
        
        final PlayerRank playerRank = profile.getRank();
        final RankFormatter rankFormatter = playerRank.formatter();
        
        if (lookupPattern != null) {
            originalMessage = originalMessage.replaceText(replacer -> replacer.match(lookupPattern).replacement(""));
        }
        
        // Create message
        messageChannel.message(profile, originalMessage.style(rankFormatter.getMessageStyle()));
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
    
    @EventHandler
    public void handleEntityDismountEvent(EntityDismountEvent ev) {
        if (!(ev.getEntity() instanceof Player player)) {
            return;
        }
        
        final HariantPlayer hariantPlayer = Hariant.getPlayer(player).orElse(null);
        
        if (hariantPlayer == null) {
            return;
        }
        
        final SitHandler sitHandler = hariantPlayer.getSitHandler();
        
        if (sitHandler != null && !sitHandler.allowDismount()) {
            ev.setCancelled(true);
        }
    }
    
    @EventHandler
    public void handleHariantMonitorDamageEvent(HariantMonitorDamageEvent ev) {
        final DamageInstance damageInstance = ev.getDamageInstance();
        final DamageSource damageSource = damageInstance.getSource();
        
        final HariantEntity entity = ev.getEntity();
        final HariantEntity source = damageSource.getSource();
        
        final double damage = damageInstance.getDamage();
        
        if (damage <= MINIMUM_DAMAGE) {
            return;
        }
        
        if (entity instanceof HariantPlayer player && player.getSetting(Settings.COMBAT_FEEDBACK)) {
            sendCombatFeedback(player, damage, damageInstance, damageSource, source, entity, false);
        }
        else if (source instanceof HariantPlayer player && player.getSetting(Settings.COMBAT_FEEDBACK)) {
            sendCombatFeedback(player, damage, damageInstance, damageSource, source, entity, true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void handleHariantHealEvent(HariantHealEvent ev) {
        final HariantEntity entity = ev.getEntity();
        
        final HealingSource healingSource = ev.getHealingSource();
        final HariantEntity healer = healingSource.getHealer();
        
        final double actualHealing = ev.getActualHealing();
        
        if (actualHealing <= MINIMUM_HEALING) {
            return;
        }
        
        // Outgoing healing
        if (healer instanceof HariantPlayer player && player.getSetting(Settings.COMBAT_FEEDBACK)) {
            this.sendHealingFeedback(player, healingSource, entity, healer, actualHealing, true);
        }
        else if (entity instanceof HariantPlayer player && player.getSetting(Settings.COMBAT_FEEDBACK)) {
            this.sendHealingFeedback(player, healingSource, entity, healer, actualHealing, false);
        }
    }
    
    private void sendCombatFeedback(@NotNull HariantPlayer player, double damage, @NotNull DamageInstance damageInstance, @NotNull DamageSource damageSource, @Nullable HariantEntity source, @NotNull HariantEntity entity, boolean outgoing) {
        final FeedbackBuilder builder = new FeedbackBuilder(outgoing ? PREFIX_OUTGOING_DAMAGE : PREFIX_INCOMING_DAMAGE);
        builder.append(Component.text("%,.0f".formatted(damage), STYLE_IMPORTANT));
        
        if (outgoing) {
            builder.append(Component.text("using", Colors.GRAY));
            builder.append(damageSource.getIdentity().getName().style(STYLE_IMPORTANT));
            builder.append(Component.text("to", Colors.GRAY));
            builder.append(entity.getName().style(STYLE_IMPORTANT));
        }
        else {
            builder.append(Component.text("from", Colors.GRAY));
            builder.append(damageSource.getIdentity().getName().style(STYLE_IMPORTANT));
            
            if (source != null) {
                builder.append(Component.text("by", Colors.GRAY));
                builder.append(source.getName().style(STYLE_IMPORTANT));
            }
        }
        
        if (damageInstance.isCritical()) {
            builder.append(HariantConstants.CHARACTER_CRITICAL_DAMAGE.style(STYLE_IMPORTANT));
        }
        
        if (damageInstance.isShielded()) {
            builder.append(HariantConstants.CHARACTER_SHIELDED_DAMAGE.color(Colors.YELLOW));
        }
        
        if (damageInstance.isLethal()) {
            builder.append(HariantConstants.CHARACTER_LETHAL_DAMAGE.color(Colors.RED));
        }
        
        // Also set the hover
        builder.hoverEvent(damageInstance.getDamageReport().createHoverEvent());
        
        player.sendMessage(builder.asComponent());
    }
    
    private void sendHealingFeedback(@NotNull HariantPlayer player, @NotNull HealingSource healingSource, @NotNull HariantEntity entity, @Nullable HariantEntity healer, double actualHealing, boolean outgoing) {
        final FeedbackBuilder builder = new FeedbackBuilder(outgoing ? PREFIX_OUTGOING_HEALING : PREFIX_INCOMING_HEALING);
        
        builder.append(Component.text("+%,.0f".formatted(actualHealing), Colors.GREEN));
        
        if (outgoing) {
            builder.append(Component.text("using", Colors.GRAY));
            builder.append(healingSource.getName().style(STYLE_IMPORTANT));
            builder.append(Component.text("to", Colors.GRAY));
            builder.append(entity.getName().style(STYLE_IMPORTANT));
        }
        else {
            builder.append(Component.text("from", Colors.GRAY));
            builder.append(healingSource.getName().style(STYLE_IMPORTANT));
            
            if (healer != null) {
                builder.append(Component.text("by", Colors.GRAY));
                builder.append(healer.getName().style(STYLE_IMPORTANT));
            }
        }
        
        player.sendMessage(builder.asComponent());
    }
    
    public static @NotNull Component createPrefix(@NotNull Component component) {
        return Component.empty()
                        .append(Component.text("[", Colors.DARK_GRAY))
                        .append(component)
                        .append(Component.text("]", Colors.DARK_GRAY));
    }
    
    private static boolean isInputDirectional(@NotNull Input input) {
        return input.isForward() || input.isBackward() || input.isLeft() || input.isRight();
    }
    
    private static class FeedbackBuilder implements ComponentLike {
        
        private final TextComponent.Builder builder;
        
        private FeedbackBuilder(@NotNull Component prefix) {
            this.builder = Component.text();
            this.append(prefix);
        }
        
        @SelfReturn
        public FeedbackBuilder append(@NotNull Component component) {
            builder.append(component);
            builder.appendSpace();
            return this;
        }
        
        @SelfReturn
        public FeedbackBuilder hoverEvent(@NotNull HoverEvent<?> hoverEvent) {
            builder.hoverEvent(hoverEvent);
            return this;
        }
        
        @Override
        public @NotNull Component asComponent() {
            return builder.build();
        }
        
    }
    
}
