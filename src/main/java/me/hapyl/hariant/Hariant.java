package me.hapyl.hariant;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.hariant.annotate.Singleton;
import me.hapyl.hariant.database.Database;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.entity.EntitySpawner;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.Lifecycle;
import me.hapyl.hariant.entity.StreamRules;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.game.*;
import me.hapyl.hariant.game.battleground.EnumBattleground;
import me.hapyl.hariant.game.type.EnumGameType;
import me.hapyl.hariant.game.type.GameType;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.security.KickReason;
import me.hapyl.hariant.security.SecurityManager;
import me.hapyl.hariant.security.SecurityManagerImpl;
import me.hapyl.hariant.task.HariantTask;
import me.hapyl.hariant.task.InternalTasks;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.util.BooleanExplained;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a singleton manager for the game, which can only be accessed via the package-private singleton; all methods
 * are designed to be static by locally calling the handler.
 *
 * <p><b>The handler instance must not be shared externally!</b></p>
 */
public final class Hariant implements Runnable, Lifecycle {
    
    public static final Component GAME_NAME = Component.text("ʜᴀʀɪᴀɴᴛ", Colors.BRAND_COLOR, TextDecoration.BOLD);
    public static final Component UPDATE_TOPIC = Component.text("\uD83D\uDC79 Orc Invasion!?", Colors.ORC);
    
    public static final World WORLD = Objects.requireNonNull(Bukkit.getWorlds().getFirst(), "Unloaded world!");
    
    public static final int TICK_RATE_NORMAL = 20;
    public static final int TICK_RATE_SLOW = 2;
    
    public static final ZoneOffset TIME_ZONE = ZoneOffset.ofHours(5);
    
    static @Singleton HariantPlugin PLUGIN;
    static @Singleton Hariant HANDLER;
    
    private final Map<UUID, PlayerProfile> profiles;
    private final Map<UUID, HariantEntity> entityMap;
    private final Random theRandom;
    private final SecurityManager securityManager;
    
    private @NotNull EnumBattleground selectedBattleground;
    private @NotNull EnumGameType selectedGameType;
    
    private GameInstance currentGameInstance;
    private GameInstanceCountdown countdown;
    
    Hariant(@NotNull HariantPlugin plugin) {
        this.profiles = Maps.newHashMap();
        this.entityMap = Maps.newHashMap();
        this.theRandom = new Random();
        this.securityManager = new SecurityManagerImpl(this);
        
        // Load battleground and type from the config
        this.selectedBattleground = plugin.config().getSelectedBattleground();
        this.selectedGameType = plugin.config().getSelectedGameType();
        
        // Schedule manager
        Bukkit.getScheduler().runTaskTimer(plugin, this, 0, 1);
    }
    
    @Override
    public void run() {
        // Tick entities
        final Collection<? extends HariantEntity> entities = entityMap.values();
        
        // Remove entities from the map if, well, they should be removed
        entities.removeIf(entity -> {
            if (entity.shouldRemove()) {
                entity.onDestroy();
                return true;
            }
            
            return false;
        });
        
        // Ticks entities internally, which handles internal removal
        entities.forEach(HariantEntity::tick0);
        
        // Tick profiles
        this.profiles.values().forEach(PlayerProfile::tick);
        
        // Tick game instance only if it's IN_PROGRESS
        if (currentGameInstance != null && currentGameInstance.getState() == GameInstanceState.IN_PROGRESS) {
            currentGameInstance.tick();
            
            // We check for tick == 0 instead of <= 0 to not spam the method, even though it validates
            // and does not allow duplicate end of the current game instance
            if (currentGameInstance.getTimeLeft() == 0) {
                final GameType gameType = currentGameInstance.getType();
                
                endCurrentGameInstance(WinResult.create(WinType.TIME_LIMIT, gameType.getWinningTeams(currentGameInstance)));
            }
        }
    }
    
    @Override
    public void onCreate() {
    }
    
    @Override
    public void onDestroy() {
        // Destroy entities
        entityMap.values().forEach(HariantEntity::remove);
        entityMap.clear();
        
        // Just save the database on shutdown, don't call onDestroy()
        profiles.values().forEach(PlayerProfile::saveDatabaseSync);
        profiles.clear();
    }
    
    // *-* Static Members *-* //
    
    public static void startNewGameInstance() {
        // Create players
        final List<@NotNull HariantPlayer> players = HANDLER.profiles.values()
                                                                     .stream()
                                                                     .map(profile -> recreatePlayer(profile.getPlayer(), profile.getSelectedHeroInstance()))
                                                                     .toList();
        
        // Create game instance
        HANDLER.currentGameInstance = new GameInstanceImpl(HANDLER.selectedGameType, HANDLER.selectedBattleground);
        HANDLER.currentGameInstance.onCreate(players);
        
        // Handle instanced created by profiles
        players.forEach(player -> player.getProfile().handleInstanceCreated(HANDLER.currentGameInstance));
        
        // Call team update
        HANDLER.profiles.values().forEach(profile -> {
            recreatePlayer(profile.getPlayer(), profile.getSelectedHeroInstance());
            
            profile.handleInstanceCreated(HANDLER.currentGameInstance);
        });
        
        final int timeBeforePlayerReveal = HANDLER.currentGameInstance.getBattleground().getTimeBeforePlayerReveal();
        
        // Hide all players
        getPlayers().forEach(player -> {
            player.hide(StreamRules.NOT_TEAMMATES);
            
            player.sendMessage(Component.text("PLAYERS HAVE BEEN HIDDEN!", Colors.GOLD, TextDecoration.BOLD));
            player.sendMessage(
                    Component.empty()
                             .append(Component.text(" They have ", Colors.YELLOW))
                             .append(Component.text(Tick.round(timeBeforePlayerReveal), Colors.RED))
                             .append(Component.text(" to spread before being revealed!", Colors.YELLOW))
            );
        });
        
        // Show all players after a delay
        InternalTasks.later(() -> {
            getPlayers().forEach(player -> {
                player.show(StreamRules.ALL);
                player.strikeLightningEffect();
                
                player.sendMessage(Component.text("PLAYERS HAVE BEEN REVEALED!", Colors.GOLD, TextDecoration.BOLD));
                player.sendMessage(Component.text(" Fight to death!", Colors.RED));
            });
            
            HANDLER.currentGameInstance.setState(GameInstanceState.IN_PROGRESS);
        }, timeBeforePlayerReveal);
    }
    
    public static boolean endCurrentGameInstance(@NotNull WinResult winResult) {
        final GameInstance gameInstance = HANDLER.currentGameInstance;
        
        if (gameInstance == null || gameInstance.getState() != GameInstanceState.IN_PROGRESS) {
            return false;
        }
        
        // Handle instance destroy
        final ServerTickManager serverTickManager = Bukkit.getServerTickManager();
        final List<? extends HariantPlayer> players = getPlayers().toList();
        
        // Destroy non-players entities at 0 tick
        clearEntities();
        
        // Due to removal issues, we manually call `onDestroy` for players at the 0 tick
        // players.forEach(HariantEntity::onDestroy);
        
        // Schedule instance destroy via a bukkit runnable
        new BukkitRunnable() {
            private int tick;
            private int tickRate = TICK_RATE_SLOW;
            
            @Override
            public void run() {
                // Increment tick rate until it hits the normal tick rate
                if (tickRate <= TICK_RATE_NORMAL) {
                    serverTickManager.setTickRate(tickRate++);
                }
                else {
                    // At the first tick, call onDestroy methods
                    if (tick == 0) {
                        gameInstance.setState(GameInstanceState.POST_GAME);
                        gameInstance.onDestroy(players, winResult);
                    }
                    // After the game delay, finalize the game
                    else if (tick == HariantConstants.GAME_END_DELAY) {
                        // Mark the game as finished
                        gameInstance.setState(GameInstanceState.FINISHED);
                        gameInstance.onFinalize(players, winResult);
                        
                        // Call `onCreate` on SPAWN to reset time/weather
                        EnumBattleground.SPAWN.onCreate(List.of());
                        
                        // Cleanup all tasks
                        HariantTask.cancelAllTasks();
                        
                        // Destroy players via their profiles
                        players.forEach(player -> {
                            player.getProfile().handleInstanceDestroyed(gameInstance, winResult);
                        });
                        
                        // Nullate instance at the very end
                        HANDLER.currentGameInstance = null;
                        this.cancel();
                    }
                    
                    tick++;
                }
                
            }
        }.runTaskTimer(PLUGIN, 0, 1);
        
        return true;
    }
    
    public static boolean endCurrentGameInstanceIfWinConditionMet() {
        final GameInstance gameInstance = HANDLER.currentGameInstance;
        
        if (gameInstance == null || gameInstance.getState() != GameInstanceState.IN_PROGRESS) {
            return false;
        }
        
        final GameType gameType = gameInstance.getType();
        
        // Check for win condition
        if (!gameType.checkWinCondition(gameInstance)) {
            return false;
        }
        
        // Compute winning teams and end the current game instance
        endCurrentGameInstance(WinResult.create(WinType.WIN_CONDITION_MET, gameType.getWinningTeams(gameInstance)));
        
        return true;
    }
    
    public static @NotNull BooleanExplained canStartNewGameInstance() {
        if (HANDLER.currentGameInstance != null) {
            return BooleanExplained.ofFalse(Component.text("A game is already in progress!", Colors.RED));
        }
        
        if (!HANDLER.selectedBattleground.isSelectable() || HANDLER.selectedBattleground.getSpawnLocations().isEmpty()) {
            return BooleanExplained.ofFalse(Component.text("Selected battleground is invalid!", Colors.RED));
        }
        
        // TODO @Feb 14, 2026 (xanyjl) -> Add tutorial check
        
        final int minimumTeamsRequired = HANDLER.selectedGameType.getMinimumTeamsRequired();
        
        final Set<EnumTeam> populatedTeams = EnumTeam.getPopulatedTeams();
        final int populatedTeamsSize = populatedTeams.size();
        
        if (populatedTeamsSize < minimumTeamsRequired) {
            return BooleanExplained.ofFalse(
                    Component.empty()
                             .append(Component.text("Not enough teams to start! ", Colors.RED))
                             .append(Components.makeComponentFractional(populatedTeamsSize, minimumTeamsRequired))
            );
        }
        
        // Check for duplicate teams if game type doesn't allow it
        if (!HANDLER.selectedGameType.allowDuplicateHeroes()) {
            for (EnumTeam team : populatedTeams) {
                final Map<Hero, List<PlayerProfile>> profilesWithDuplicateHeroes
                        = team.getPlayerProfiles()
                              .collect(Collectors.collectingAndThen(
                                      Collectors.groupingBy(PlayerProfile::getSelectedHero),
                                      collected -> collected.entrySet()
                                                            .stream()
                                                            .filter(entry -> entry.getValue().size() > 1)
                                                            .collect(Collectors.toMap(
                                                                    Map.Entry::getKey,
                                                                    Map.Entry::getValue
                                                            ))
                              ));
                
                // Since we filter() in ths stream, the first entry is guaranteed to be a duplicate
                for (Map.Entry<Hero, List<PlayerProfile>> entry : profilesWithDuplicateHeroes.entrySet()) {
                    final Hero duplicateHero = entry.getKey();
                    final List<PlayerProfile> profiles = entry.getValue();
                    
                    // Notify players with duplicate heroes
                    for (PlayerProfile profile : profiles) {
                        final Player player = profile.getPlayer();
                        final Component othersNames = Components.makeComponentCommaAnd(
                                profiles.stream()
                                        .filter(Predicate.not(profile::equals))
                                        .map(other -> other.getName())
                                        .toList(),
                                Function.identity()
                        );
                        
                        HariantLogger.error(player, Component.text("Currently selected game type does not permit duplicate heroes in the team!"));
                        
                        if (profiles.size() == 2) {
                            HariantLogger.error(
                                    player,
                                    Component.empty()
                                             .append(Component.text("You and "))
                                             .append(othersNames)
                                             .append(Component.text(" both have "))
                                             .append(duplicateHero.getName())
                                             .append(Component.text(" selected!"))
                            );
                        }
                        else {
                            HariantLogger.error(
                                    player,
                                    Component.empty()
                                             .append(Component.text("You, "))
                                             .append(othersNames)
                                             .append(Component.text(" all have "))
                                             .append(duplicateHero.getName())
                                             .append(Component.text(" selected!"))
                            );
                        }
                    }
                    
                    return BooleanExplained.ofFalse(
                            Component.empty()
                                     .append(Component.text("Duplicate heroes in "))
                                     .append(team.getName().style(team.getStyle()))
                                     .append(Component.text("!"))
                    );
                }
            }
        }
        
        return BooleanExplained.ofTrue();
    }
    
    @NotNull
    public static Optional<GameInstance> getCurrentGameInstance() {
        return Optional.ofNullable(HANDLER.currentGameInstance);
    }
    
    public static @Nullable GameInstance getCurrentGameInstanceOrNull() {
        return HANDLER.currentGameInstance;
    }
    
    @NotNull
    public static HariantPlugin getPlugin() {
        return PLUGIN;
    }
    
    @NotNull
    public static Random getRandom() {
        return HANDLER.theRandom;
    }
    
    @NotNull
    public static EnumBattleground getSelectedBattleground() {
        return HANDLER.selectedBattleground;
    }
    
    public static void setSelectedBattleground(@NotNull EnumBattleground battleground) {
        if (!battleground.isSelectable()) {
            throw new IllegalArgumentException("This battleground cannot be selected!");
        }
        
        HANDLER.selectedBattleground = battleground;
        PLUGIN.config().setSelectedBattleground(battleground);
    }
    
    @NotNull
    public static EnumGameType getSelectedGameType() {
        return HANDLER.selectedGameType;
    }
    
    public static void setSelectedGameType(@NotNull EnumGameType gameType) {
        HANDLER.selectedGameType = gameType;
        PLUGIN.config().setSelectedGameType(gameType);
    }
    
    @NotNull
    public static Optional<PlayerProfile> getPlayerProfile(@NotNull UUID uuid) {
        return Optional.ofNullable(HANDLER.profiles.get(uuid));
    }
    
    @NotNull
    public static PlayerProfile getPlayerProfile(@NotNull Player player) {
        final UUID uniqueId = player.getUniqueId();
        final PlayerProfile profile = getPlayerProfile(uniqueId).orElse(null);
        
        if (profile != null) {
            return profile;
        }
        
        getSecurityManager().kick(player, KickReason.create(uniqueId, "There was an error loading your profile!", "getPlayerProfile() == null"));
        throw new IllegalStateException("Missing profile for `%s`!".formatted(uniqueId));
    }
    
    public static @NotNull Stream<PlayerProfile> getPlayerProfiles() {
        return HANDLER.profiles.values().stream();
    }
    
    /**
     * Retrieves a {@link PlayerDatabase} for the given <b>online</b> player.
     *
     * @param player - The online player whose database to get.
     * @return the player database instance.
     */
    public static @NotNull PlayerDatabase getPlayerDatabase(@NotNull Player player) {
        return getPlayerProfile(player).getDatabase();
    }
    
    public static @NotNull Optional<HariantEntity> getEntity(@NotNull UUID uuid) {
        return Optional.ofNullable(HANDLER.entityMap.get(uuid));
    }
    
    public static @NotNull Optional<HariantEntity> getEntity(@NotNull Entity entity) {
        return getEntity(entity.getUniqueId());
    }
    
    public static @Nullable HariantEntity getEntityOrNull(@NotNull UUID uuid) {
        return HANDLER.entityMap.get(uuid);
    }
    
    public static @Nullable HariantEntity getEntityOrNull(@NotNull Entity entity) {
        return getEntityOrNull(entity.getUniqueId());
    }
    
    public static @NotNull Optional<HariantPlayer> getPlayer(@NotNull Player player) {
        return getEntityOrNull(player) instanceof HariantPlayer hariantPlayer
               ? Optional.of(hariantPlayer)
               : Optional.empty();
    }
    
    public static @NotNull Stream<HariantPlayer> getPlayers() {
        // Using profiles as a bridge to get players is faster than iterating over the entities map, because
        // we're just calling get() on uuid
        return getPlayerProfiles().map(PlayerProfile::getHariantPlayer).filter(Optional::isPresent).map(Optional::get);
    }
    
    public static @NotNull <H extends HariantEntity> H createEntity(@NotNull EntitySpawner<H> spawner) {
        final H entity = spawner.spawn();
        HANDLER.entityMap.put(entity.getUuid(), entity);
        entity.onCreate();
        
        return entity;
    }
    
    public static void destroyEntity(@NotNull UUID uuid) {
        final HariantEntity entity = HANDLER.entityMap.remove(uuid);
        
        if (entity != null) {
            entity.onDestroy();
        }
    }
    
    @NotNull
    public static HariantPlayer createPlayer(@NotNull Player player, @NotNull HeroInstance heroInstance) {
        final UUID uniqueId = player.getUniqueId();
        
        if (HANDLER.entityMap.containsKey(uniqueId)) {
            throw new IllegalStateException("HariantPlayer already exists for `%s`!".formatted(player.getName()));
        }
        
        return createEntity(() -> new HariantPlayer(
                Objects.requireNonNull(HANDLER.profiles.get(player.getUniqueId()), "Cannot create HariantPlayer without a profile!"),
                player,
                heroInstance
        ));
    }
    
    @NotNull
    public static HariantPlayer recreatePlayer(@NotNull Player player, @NotNull HeroInstance heroInstance) {
        destroyEntity(player.getUniqueId());
        return createPlayer(player, heroInstance);
    }
    
    @NotNull
    public static PlayerProfile createProfile(@NotNull Player player) {
        final UUID uuid = player.getUniqueId();
        
        if (HANDLER.profiles.containsKey(uuid)) {
            throw new IllegalStateException("Profile already exists for %s!".formatted(player.getName()));
        }
        
        final PlayerProfile profile = new PlayerProfile(player);
        HANDLER.profiles.put(uuid, profile);
        profile.onCreate();
        
        return profile;
    }
    
    public static void destroyProfile(@NotNull Player player) {
        final PlayerProfile profile = HANDLER.profiles.remove(player.getUniqueId());
        
        if (profile != null) {
            profile.onDestroy();
        }
    }
    
    public static boolean isGameInProgress() {
        return HANDLER.currentGameInstance != null;
    }
    
    public static boolean isGameInProgressButNotActive() {
        return HANDLER.currentGameInstance != null && HANDLER.currentGameInstance.getState() != GameInstanceState.IN_PROGRESS;
    }
    
    @NotNull
    public static SecurityManager getSecurityManager() {
        return HANDLER.securityManager;
    }
    
    public static void hideBukkitEntity(@NotNull Entity entity) {
        Bukkit.getOnlinePlayers().forEach(player -> player.hideEntity(PLUGIN, entity));
    }
    
    public static void showBukkitEntity(@NotNull Entity entity) {
        Bukkit.getOnlinePlayers().forEach(player -> player.showEntity(PLUGIN, entity));
    }
    
    public static void hideBukkitEntity(@NotNull Player player, @NotNull Entity entity) {
        player.hideEntity(PLUGIN, entity);
    }
    
    public static void showBukkitEntity(@NotNull Player player, @NotNull Entity entity) {
        player.showEntity(PLUGIN, entity);
    }
    
    public static int getPlayerProfileCount() {
        return HANDLER.profiles.size();
    }
    
    public static void globalBlockChange(@NotNull Location location, @NotNull BlockData blockData) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendBlockChange(location, blockData));
    }
    
    public static void globalBlockChange(@NotNull Location location) {
        globalBlockChange(location, location.getBlock().getBlockData());
    }
    
    public static boolean entityExists(@NotNull UUID uniqueId) {
        return HANDLER.entityMap.containsKey(uniqueId);
    }
    
    public static void onPlayerReady(@NotNull PlayerProfile playerProfile) {
        // If there is a countdown already, cancel it
        if (HANDLER.countdown != null) {
            cancelCountdown(
                    Component.empty()
                             .append(playerProfile.getNameFormatted())
                             .appendSpace()
                             .append(Component.text("cancelled the countdown!", Colors.ERROR))
            );
            return;
        }
        
        // Send readiness message BEFORE checking for whether we can start the game
        final boolean ready = playerProfile.isReady();
        
        HariantLogger.PREFIX_INFO.broadcastMessage(
                Component.empty()
                         .append(playerProfile.getNameFormatted())
                         .appendSpace()
                         .append(
                                 ready
                                 ? Component.text("is now ready.", Colors.SUCCESS)
                                 : Component.text("is no longer ready.", Colors.ERROR)
                         )
        );
        
        PlayerLib.playSound(Sound.BLOCK_NOTE_BLOCK_HAT, ready ? 0.75f : 0.5f);
        
        // Check whether all players are ready
        final Collection<? extends PlayerProfile> nonSpectatorProfiles = HANDLER.profiles.values()
                                                                                         .stream()
                                                                                         .filter(Predicate.not(PlayerProfile::isSpectator))
                                                                                         .toList();
        
        final int expectedReadyPlayers = nonSpectatorProfiles.size();
        final int currentlyReadyPlayers = (int) nonSpectatorProfiles
                .stream()
                .filter(PlayerProfile::isReady)
                .count();
        
        // If everyone who isn't a spectator is ready, attempt to start the game
        if (currentlyReadyPlayers == expectedReadyPlayers) {
            // Check whether the game can be started
            final BooleanExplained booleanExplained = canStartNewGameInstance();
            
            if (!booleanExplained.booleanValue()) {
                // Unready the player who was the last to press ready
                playerProfile.setReady0(false, false);
                
                HariantLogger.PREFIX_ERROR.broadcastMessage(
                        Component.empty()
                                 .append(Component.text("Failed to start the game!"))
                                 .appendSpace()
                                 .append(booleanExplained.explain())
                );
                return;
            }
            
            // Otherwise start the countdown
            startCountdown();
        }
    }
    
    public static void startCountdown() {
        if (HANDLER.countdown != null) {
            HANDLER.countdown.cancel();
        }
        
        HANDLER.countdown = new GameInstanceCountdown();
    }
    
    public static void cancelCountdown(@Nullable Component reason) {
        if (HANDLER.countdown == null) {
            return;
        }
        
        HANDLER.countdown.cancel(reason);
        HANDLER.countdown = null;
    }
    
    public static boolean isCountdownActive() {
        return HANDLER.countdown != null;
    }
    
    public static @NotNull String getVersion() {
        return PLUGIN.getPluginMeta().getVersion().replace("-SNAPSHOT", "");
    }
    
    public static int currentTick() {
        return Bukkit.getCurrentTick();
    }
    
    public static boolean currentTickMod20() {
        return currentTick() % 40 < 20;
    }
    
    public static @NotNull Database getDatabase() {
        return PLUGIN.getDatabase();
    }
    
    public static int getCurrentDaySinceEpoch() {
        return (int) (System.currentTimeMillis() / HariantConstants.MILLIS_IN_DAY);
    }
    
    public static long getTimeUntilReset() {
        final long currentTimeMillis = System.currentTimeMillis();
        
        // Calculate the day since epoch
        final long startOfCurrentDay = Math.floorDiv(currentTimeMillis, HariantConstants.MILLIS_IN_DAY) * HariantConstants.MILLIS_IN_DAY;
        final long startOfNextDay = startOfCurrentDay + HariantConstants.MILLIS_IN_DAY;
        
        return startOfNextDay - currentTimeMillis;
    }
    
    public static @NotNull Component getTimeUntilResetFormatted() {
        final long totalSeconds = getTimeUntilReset() / 1000;
        final long hoursPart = totalSeconds / 3600;
        final long minutesPart = (totalSeconds % 3600) / 60;
        final long secondsPart = totalSeconds % 60;
        
        return Component.text("%dh %02dm %02ds".formatted(hoursPart, minutesPart, secondsPart));
    }
    
    public static @NotNull World getWorld() {
        return WORLD;
    }
    
    public static @NotNull NamespacedKey createNamespacedKey(String key) {
        return new NamespacedKey(PLUGIN, key);
    }
    
    private static void clearEntities() {
        HANDLER.entityMap.values().removeIf(entity -> {
            if (entity.isPersistent()) {
                return false;
            }
            
            // Remove the entity forcefully
            entity.removeForcefully();
            return true;
        });
    }
    
}