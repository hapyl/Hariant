package me.hapyl.hariant.profile;

import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.player.dialog.Dialog;
import me.hapyl.eterna.module.player.dialog.DialogEndType;
import me.hapyl.eterna.module.player.dialog.DialogInstance;
import me.hapyl.eterna.module.reflect.glowing.Glowing;
import me.hapyl.eterna.module.reflect.team.PacketTeamColor;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.rank.FormatRules;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.entity.HeadComponent;
import me.hapyl.hariant.entity.Lifecycle;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.game.GameInstance;
import me.hapyl.hariant.game.GameInstanceHandler;
import me.hapyl.hariant.game.battleground.Battleground;
import me.hapyl.hariant.game.battleground.EnumBattleground;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.inventory.drop.DropSummary;
import me.hapyl.hariant.lobby.EnumLobbyItem;
import me.hapyl.hariant.profile.message.MessageChannel;
import me.hapyl.hariant.profile.setting.Setting;
import me.hapyl.hariant.profile.setting.SettingRetriever;
import me.hapyl.hariant.profile.setting.Settings;
import me.hapyl.hariant.profile.ui.PlayerUI;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.team.TeamEntry;
import me.hapyl.hariant.team.TeamEntryProvider;
import me.hapyl.hariant.util.UniquelyIdentified;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.object.ObjectContents;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public final class PlayerProfile
        implements
        Lifecycle, Ticking, UniquelyIdentified, TeamEntryProvider,
        ForwardingAudience.Single, GameInstanceHandler, HeadComponent, SettingRetriever,
        NameFormatter {
    
    private static final int TICK_MODULO_UI = 5;
    private static final char PING_CHAR = '@';
    
    private static final FormatRules DEFAULT_NAME_FORMAT = FormatRules.create(true, false, true, true, false);
    private static final FormatRules DEFAULT_NAME_FORMAT_SOCIAL = FormatRules.create(true, true, true, true, true);
    
    private static final Style PING_STYLE = Style.style(Colors.YELLOW, TextDecoration.UNDERLINED);
    
    private final Player player;
    private final PlayerDatabase database;
    
    private final PlayerUI playerUI;
    private int tick;
    
    private boolean spectator;
    private boolean ready;
    
    public PlayerProfile(@NotNull Player player) {
        this.player = player;
        this.database = new PlayerDatabase(Hariant.getPlugin().getDatabase(), player.getUniqueId());
        this.playerUI = new PlayerUI(this);
    }
    
    public boolean isReady() {
        return ready;
    }
    
    public void setReady(boolean ready) {
        this.setReady0(ready, true);
    }
    
    public void setReady0(boolean ready, boolean triggerUpdate) {
        this.ready = ready;
        
        EnumLobbyItem.READY.give(player);
        
        if (triggerUpdate) {
            Hariant.onPlayerReady(this);
        }
    }
    
    @NotNull
    public PlayerRank getRank() {
        return database.getRank();
    }
    
    @NotNull
    public Component getName() {
        return player.name();
    }
    
    @Override
    public @NotNull Component getNameFormatted(@NotNull FormatRules formatRules) {
        return getRank().formatter().format(this, formatRules);
    }
    
    @Override
    public @NotNull Component getNameFormatted() {
        return this.getNameFormatted(DEFAULT_NAME_FORMAT);
    }
    
    @Override
    public @NotNull Component getNameFormattedSocial() {
        return this.getNameFormatted(DEFAULT_NAME_FORMAT_SOCIAL);
    }
    
    @NotNull
    public Player getPlayer() {
        return player;
    }
    
    @NotNull
    @Override
    public Component asHeadComponent() {
        return Component.object(ObjectContents.playerHead(player)).color(Colors.WHITE);
    }
    
    @NotNull
    public Optional<HariantPlayer> getHariantPlayer() {
        return Hariant.getPlayer(this.getPlayer());
    }
    
    @NotNull
    public PlayerDatabase getDatabase() {
        return database;
    }
    
    @NotNull
    public PlayerUI getPlayerUI() {
        return playerUI;
    }
    
    @NotNull
    public Hero getSelectedHero() {
        return database.heroDirectory.getSelectedHero();
    }
    
    @NotNull
    public HeroInstance getSelectedHeroInstance() {
        return database.heroDirectory.getSelectedHeroInstance();
    }
    
    public void setSelectedHero(@NotNull HeroInstance heroInstance) {
        final Hero hero = heroInstance.getOrigin();
        
        if (database.heroDirectory.getSelectedHero().equals(hero)) {
            HariantLogger.error(player, Component.text("This hero is already selected!"));
            return;
        }
        
        database.heroDirectory.setSelectedHero(heroInstance);
        HariantLogger.success(player, Component.empty().append(Component.text("Selected ")).append(hero).append(Component.text("!")));
        
        // Cancel countdown if it was active
        Hariant.cancelCountdown(
                Component.empty()
                         .append(Component.text("The countdown was cancelled because "))
                         .append(this.getNameFormatted())
                         .append(Component.text(" changed their hero!"))
        );
    }
    
    @Override
    public void onCreate() {
        // Join the smallest team
        EnumTeam.getSmallestTeam().addPlayer(this);
        
        final boolean gameInProgress = Hariant.isGameInProgress();
        
        // Create vanilla teams
        playerUI.getVanillaTeamManager().onCreate();
        
        VanillaTeamManager.bumpOtherProfilesToCreateTeamForMe(this);
        
        // If the game is currently in progress, put the player in spectator
        if (gameInProgress) {
            this.spectate();
        }
        else {
            // Give lobby items and teleport to origin if not in creative
            if (player.getGameMode() != GameMode.CREATIVE) {
                teleportToSpawnAndGiveLobbyItems();
            }
        }
    }
    
    @Override
    public void onDestroy() {
        // Save database
        this.database.save();
        
        VanillaTeamManager.bumpOtherProfilesToDeleteTeamForMe(this);
        
        // Leave team
        this.getTeam().removeEntry(this);
    }
    
    public boolean isSpectator() {
        return spectator;
    }
    
    public void setSpectator(boolean spectator) {
        this.spectator = spectator;
    }
    
    public void spectate() {
        this.player.setGameMode(GameMode.SPECTATOR);
        
        // Update vanilla teams to SPECTATEE, unless self
        this.playerUI.getVanillaTeamManager().setStateForAllProfilesExceptSelf(VanillaTeamManager.State.IN_GAME_SPECTATEE);
        
        // Teleport to the first player
        final List<HariantPlayer> players = Hariant.getPlayers().toList();
        HariantPlayer firstPlayer = null;
        
        for (HariantPlayer hariantPlayer : players) {
            // Teleport to the first player we see
            if (firstPlayer == null) {
                firstPlayer = hariantPlayer;
            }
            
            // Make the player glow based on their team color
            Glowing.setGlowing(player, hariantPlayer.getHandle(), hariantPlayer.getPlayerTeam().getPacketTeamColor());
        }
        
        // Teleport to the first player
        if (firstPlayer != null) {
            player.teleport(firstPlayer.getLocation().add(0, 2, 0));
        }
    }
    
    @Override
    public void tick() {
        tick++;
        
        // Reduce ui updates
        if (tick % TICK_MODULO_UI == 0) {
            playerUI.tick();
        }
    }
    
    @NotNull
    @Override
    public UUID getUuid() {
        return player.getUniqueId();
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.player);
    }
    
    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final PlayerProfile that = (PlayerProfile) object;
        return Objects.equals(this.player.getUniqueId(), that.player.getUniqueId());
    }
    
    @Override
    public String toString() {
        return "PlayerProfile(%s)".formatted(player.getName());
    }
    
    @NotNull
    @Override
    public TeamEntry teamEntry() {
        return TeamEntry.create(this.getUuid(), true);
    }
    
    @NotNull
    @Override
    public Audience audience() {
        return player;
    }
    
    @NotNull
    public EnumTeam getTeam() {
        return Objects.requireNonNull(EnumTeam.getEntryTeam(this), "Player cannot not be in a team!");
    }
    
    @Override
    public void handleInstanceCreated(@NotNull GameInstance gameInstance) {
        this.playerUI.getVanillaTeamManager().setStateForAllProfiles(false);
        
        // Force not ready
        this.ready = false;
        
        // TODO @Feb 24, 2026 (xanyjl) -> Cancel parkour
        
        // Cancel dialog
        getCurrentDialog().ifPresent(dialogInstance -> {
            dialogInstance.cancel(DialogEndType.COMPLETED);
        });
        
        
        // Start glowing unless the player is a spectator
        if (!spectator) {
            getTeam().getPlayerProfiles().filter(Predicate.not(this::equals)).forEach(teammate -> {
                Glowing.setGlowing(player, teammate.getPlayer(), PacketTeamColor.GREEN, Glowing.INFINITE_DURATION);
            });
        }
        
        // Teleport to the map
        final Battleground battleground = gameInstance.getBattleground();
        
        player.teleport(battleground.getRandomSpawnLocation());
    }
    
    @Override
    public void handlerInstanceDestroyed(@NotNull GameInstance gameInstance) {
        this.playerUI.getVanillaTeamManager().setStateForAllProfiles(true);
        
        // Stop glowing for all players
        Glowing.stopGlowing(player);
        
        // Destroy player
        Hariant.destroyEntity(this.getUuid());
        
        this.teleportToSpawnAndGiveLobbyItems();
        this.player.setGameMode(GameMode.SURVIVAL);
        this.player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getValue());
        
        // Show player to all other players
        Hariant.showBukkitEntity(player);
        
        // Generate loot
        final DropSummary dropSummary = gameInstance.getBattleground().getDropTable().generateLoot(this);
        
        dropSummary.showSummary(this);
    }
    
    public void teleportToSpawnAndGiveLobbyItems() {
        // Teleport to the spawn
        player.teleport(EnumBattleground.SPAWN.getSpawnLocations().getFirst().getCenteredLocation());
        
        // Give lobby items
        EnumLobbyItem.clearInventoryAndGiveAllItems(player);
    }
    
    @NotNull
    public Optional<DialogInstance> getCurrentDialog() {
        return Dialog.getCurrentDialog(player);
    }
    
    public void saveDatabaseSync() {
        database.save(false);
    }
    
    public void receiveMessage(@NotNull MessageChannel messageChannel, @Nullable PlayerProfile sender, @NotNull Component message) {
        // Check for player pings
        final String stringMessage = Components.toString(message);
        final String playerName = player.getName();
        
        // If message contains `@playerName`, we replace it with colored one and play a ping sound
        final String pingPlayerName = PING_CHAR + playerName;
        
        if (stringMessage.contains(pingPlayerName)) {
            final boolean pingsAllowed = getSetting(Settings.ALLOW_PINGS) || (sender != null && sender.getRank().isStaff());
            
            if (pingsAllowed) {
                message = message.replaceText(replacer -> replacer.matchLiteral(pingPlayerName).replacement(Component.text(pingPlayerName, PING_STYLE)));
                
                PlayerLib.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f);
            }
        }
        
        // TODO (xanyjl @ Saturday, May 30) -> Check for ignored players, etc
        
        // Finally send the message
        this.sendMessage(message);
    }
    
    @Override
    public @NotNull <I> I getSetting(@NotNull Setting<I> setting) {
        return database.settings.getValue(setting);
    }
    
    @Override
    public <I> void setSetting(@NotNull Setting<I> setting, @NotNull I value) {
        database.settings.setValue(setting, value);
    }
    
}