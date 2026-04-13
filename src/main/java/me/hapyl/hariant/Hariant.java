package me.hapyl.hariant;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.player.dialog.DialogEndType;
import me.hapyl.eterna.module.reflect.glowing.Glowing;
import me.hapyl.eterna.module.reflect.team.PacketTeamColor;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.annotate.Singleton;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.PlayerDatabaseView;
import me.hapyl.hariant.entity.EntitySpawner;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.Lifecycle;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.game.*;
import me.hapyl.hariant.game.battleground.Battleground;
import me.hapyl.hariant.game.battleground.EnumBattleground;
import me.hapyl.hariant.game.type.EnumGameType;
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
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    
    public static final Component GAME_NAME = Component.text("Hariant", Colors.BRAND_COLOR);
    private static final int GAME_END_DELAY = Tick.fromSeconds(5);
    
    @Singleton static HariantPlugin plugin;
    @Singleton static Hariant handler;
    
    private final Map<UUID, PlayerProfile> profiles;
    private final Map<UUID, HariantEntity> entityMap;
    private final Random theRandom;
    private final SecurityManager securityManager;
    
    @NotNull private EnumBattleground selectedBattleground;
    @NotNull private EnumGameType selectedGameType;
    
    private GameInstance currentGameInstance;
    
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
        final Collection<HariantEntity> entities = entityMap.values();
        
        // Don't call `removeIfShould` to avoid double calling `remove()`
        entities.removeIf(HariantEntity::shouldRemove);
        entities.forEach(HariantEntity::tick0);
        
        // Tick profiles
        this.profiles.values().forEach(PlayerProfile::tick);
        
        // Tick game instance
        if (currentGameInstance != null) {
            currentGameInstance.tick();
            
            // We check for tick == 0 instead of <= 0 to not spam the method, even though it validates
            // and does not allow duplicate end of the current game instance
            if (currentGameInstance.getTimeLeft() == 0) {
                endCurrentGameInstance(WinResult.create(WinType.TIME_LIMIT, List.of()));
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
        
        // Save player data on shutdown
        profiles.values().forEach(PlayerProfile::onDestroy);
        profiles.clear();
    }
    
    // *-* Static Members *-* //
    
    public static void startNewGameInstance() {
        handler.currentGameInstance = new GameInstanceImpl(handler.selectedGameType, handler.selectedBattleground);
        handler.currentGameInstance.onCreate();
        
        // Call team update
        handler.profiles.values().forEach(profile -> {
            final Player bukkitPlayer = profile.getPlayer();
            final HariantPlayer player = recreatePlayer(bukkitPlayer, profile.getSelectedHeroInstance());
            
            profile.handleInstanceCreated(handler.currentGameInstance);
            
            // Cancel dialog
            profile.getCurrentDialog().ifPresent(dialogInstance -> {
                dialogInstance.cancel(DialogEndType.COMPLETED);
            });
            
            // TODO @Feb 24, 2026 (xanyjl) -> Cancel parkour
            
            // Teleport to the map
            final Battleground battleground = handler.currentGameInstance.getBattleground();
            
            player.teleport(battleground.getRandomSpawnLocation());
            
            // Set glowing for teammates
            profile.getTeam().getPlayerProfiles().filter(Predicate.not(profile::equals)).forEach(teammate -> {
                Glowing.setGlowing(profile.getPlayer(), teammate.getPlayer(), PacketTeamColor.GREEN, Glowing.INFINITE_DURATION);
            });
        });
        
        final int timeBeforePlayerReveal = handler.currentGameInstance.getBattleground().getTimeBeforePlayerReveal();
        
        // Hide all players
        getPlayers().forEach(player -> {
            player.hide();
            
            player.sendMessage(Component.text("PLAYERS HAVE BEEN HIDDEN!", NamedTextColor.GOLD, TextDecoration.BOLD));
            player.sendMessage(
                    Component.empty()
                             .append(Component.text(" They have ", NamedTextColor.YELLOW))
                             .append(Component.text(Tick.round(timeBeforePlayerReveal), NamedTextColor.RED))
                             .append(Component.text(" to spread before being revealed!", NamedTextColor.YELLOW))
            );
        });
        
        // Show all players after a delay
        InternalTasks.later(() -> {
            getPlayers().forEach(player -> {
                player.show();
                player.strikeLightning();
                
                player.sendMessage(Component.text("PLAYERS HAVE BEEN REVEALED!", NamedTextColor.GOLD, TextDecoration.BOLD));
                player.sendMessage(Component.text(" Fight to death!", NamedTextColor.RED));
            });
            
            handler.currentGameInstance.setState(GameInstanceState.ACTIVE);
        }, timeBeforePlayerReveal);
    }
    
    public static void endCurrentGameInstance(@NotNull WinResult result) {
        if (handler.currentGameInstance == null || handler.currentGameInstance.getState() != GameInstanceState.ACTIVE) {
            return;
        }
        
        handler.currentGameInstance.setState(GameInstanceState.POST_GAME);
        handler.currentGameInstance.onDestroy();
        
        // Keep the reference, we'll need it later
        final List<HariantPlayer> players = getPlayers().toList();
        
        players.forEach(player -> {
            if (result.isWinner(player.teamEntry())) {
                player.setGameMode(GameMode.SURVIVAL);
            }
            else {
                player.setGameMode(GameMode.SPECTATOR);
            }
            
            // Display result
            player.sendMessage(Component.text("GAME OVER", NamedTextColor.GOLD, TextDecoration.BOLD));
            player.sendMessage(Component.text("Win type %s".formatted(result.getWinType())));
            player.sendMessage(Component.text("Winners: %s".formatted(result.getWinningTeams())));
        });
        
        // Schedule a cleanup via the bukkit runnable
        new BukkitRunnable() {
            private int tick;
            
            @Override
            public void run() {
                if (tick++ > GAME_END_DELAY) {
                    // Destroy players
                    getPlayerProfiles().forEach(profile -> profile.handlerInstanceDestroyed(handler.currentGameInstance));
                    
                    // Sync the database
                    plugin.getDatabaseSyncer().handlerInstanceDestroyed(handler.currentGameInstance);
                    
                    handler.currentGameInstance.setState(GameInstanceState.FINISHED);
                    handler.currentGameInstance = null;
                    
                    // Cleanup tasks
                    HariantTask.cancelAllTasks();
                    
                    // Destroy non-players entities
                    clearEntities();
                    
                    this.cancel();
                    return;
                }
                
                // Fx
                players.forEach(player -> {
                    player.sendSubtitle(
                            Component.text("game ends in %s".formatted(GAME_END_DELAY - tick), TextColor.color(0xA1DE9D)),
                            0, 10, 0
                    );
                });
            }
        }.runTaskTimer(plugin, 0, 1);
    }
    
    public static boolean endCurrentGameInstanceIfWinConditionMet() {
        if (handler.currentGameInstance == null || handler.currentGameInstance.getState() != GameInstanceState.ACTIVE) {
            return false;
        }
        
        final WinResult winResult = handler.currentGameInstance.getType().checkWinCondition(handler.currentGameInstance);
        
        if (winResult != null) {
            endCurrentGameInstance(winResult);
            return true;
        }
        
        return false;
    }
    
    @NotNull
    public static BooleanExplained canStartNewGameInstance() {
        if (handler.currentGameInstance != null) {
            return BooleanExplained.ofFalse(Component.text("A game is already in progress!"));
        }
        
        if (!handler.selectedBattleground.isSelectable() || handler.selectedBattleground.getSpawnLocations().isEmpty()) {
            return BooleanExplained.ofFalse(Component.text("Selected battleground is invalid!"));
        }
        
        // TODO @Feb 14, 2026 (xanyjl) -> Add tutorial check
        
        final int minimumTeamsRequired = handler.selectedGameType.getMinimumTeamsRequired();
        
        final Set<EnumTeam> populatedTeams = EnumTeam.getPopulatedTeams();
        final int populatedTeamsSize = populatedTeams.size();
        
        if (populatedTeamsSize < minimumTeamsRequired) {
            return BooleanExplained.ofFalse(
                    Component.empty()
                             .append(Component.text("Not enough teams to start! "))
                             .append(Components.makeComponentFractional(populatedTeamsSize, minimumTeamsRequired))
            );
        }
        
        // Check for duplicate teams if game type doesn't allow it
        if (!handler.selectedGameType.allowDuplicateHeroes()) {
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
                                             .append(Component.text("You and"))
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
        return Optional.ofNullable(handler.currentGameInstance);
    }
    
    @NotNull
    public static HariantPlugin getPlugin() {
        return plugin;
    }
    
    @NotNull
    public static Random getRandom() {
        return handler.theRandom;
    }
    
    @NotNull
    public static EnumBattleground getSelectedBattleground() {
        return handler.selectedBattleground;
    }
    
    public static void setSelectedBattleground(@NotNull EnumBattleground battleground) {
        if (!battleground.isSelectable()) {
            throw new IllegalArgumentException("This battleground cannot be selected!");
        }
        
        handler.selectedBattleground = battleground;
        plugin.config().setSelectedBattleground(battleground);
    }
    
    @NotNull
    public static EnumGameType getSelectedGameType() {
        return handler.selectedGameType;
    }
    
    public static void setSelectedGameType(@NotNull EnumGameType gameType) {
        handler.selectedGameType = gameType;
        plugin.config().setSelectedGameType(gameType);
    }
    
    @NotNull
    public static Optional<PlayerProfile> getPlayerProfile(@NotNull UUID uuid) {
        return Optional.ofNullable(handler.profiles.get(uuid));
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
    
    @NotNull
    public static Stream<PlayerProfile> getPlayerProfiles() {
        return handler.profiles.values().stream();
    }
    
    /**
     * Retrieves a {@link PlayerDatabase} for the given <b>online</b> player.
     *
     * <p>
     * This method must only be used to retrieve a runtime database for a connected player. If you require
     * a database for offline player, use {@link Hariant#getPlayerDatabase(UUID)} to load a new instance.
     * </p>
     *
     * @param player - The online player whose database to get.
     * @return the player database instance.
     */
    @NotNull
    public static PlayerDatabase getPlayerDatabase(@NotNull Player player) {
        return getPlayerProfile(player).getDatabase();
    }
    
    /**
     * Creates a new instance of {@link PlayerDatabase} for the given {@link UUID}.
     *
     * <p>
     * The returned instance is a view of a player database, mutating it does not
     * affect the actual player database, even if the player is online.
     * </p>
     *
     * <p>
     * Saving the view is prohibited and will result in an {@link UnsupportedOperationException}.
     * </p>
     *
     * @param uuid - The player uuid for whom to create the database.
     * @return a new instance of the database.
     */
    @NotNull
    public static PlayerDatabase getPlayerDatabase(@NotNull UUID uuid) {
        return new PlayerDatabaseView(plugin.getDatabase(), uuid);
    }
    
    @NotNull
    public static <E extends HariantEntity> Optional<E> getEntity(@NotNull UUID uuid, @NotNull Class<E> clazz) {
        final HariantEntity hariantEntity = handler.entityMap.get(uuid);
        
        return clazz.isInstance(hariantEntity) ? Optional.of(clazz.cast(hariantEntity)) : Optional.empty();
    }
    
    @NotNull
    public static <E extends HariantEntity> Optional<E> getEntity(@NotNull Entity entity, @NotNull Class<E> clazz) {
        return getEntity(entity.getUniqueId(), clazz);
    }
    
    @NotNull
    public static Optional<HariantEntity> getEntity(@NotNull Entity entity) {
        return getEntity(entity, HariantEntity.class);
    }
    
    @NotNull
    public static Optional<HariantEntity> getEntity(@NotNull UUID uuid) {
        return getEntity(uuid, HariantEntity.class);
    }
    
    @Nullable
    public static HariantEntity getEntityOrNull(@NotNull Entity entity) {
        return handler.entityMap.get(entity.getUniqueId());
    }
    
    @NotNull
    public static Optional<HariantPlayer> getPlayer(@NotNull Player player) {
        return getEntity(player, HariantPlayer.class);
    }
    
    @NotNull
    public static Stream<HariantPlayer> getPlayers() {
        // Using profiles as a bridge to get players is faster than iterating over the entities map, because
        // we're just calling get() on uuid
        return getPlayerProfiles().map(PlayerProfile::getHariantPlayer).filter(Optional::isPresent).map(Optional::get);
    }
    
    @NotNull
    public static <H extends HariantEntity> H createEntity(@NotNull EntitySpawner<H> spawner) {
        final H entity = spawner.spawn();
        handler.entityMap.put(entity.getUuid(), entity);
        entity.onCreate();
        
        return entity;
    }
    
    public static void destroyEntity(@NotNull UUID uuid) {
        final HariantEntity entity = handler.entityMap.remove(uuid);
        
        if (entity != null) {
            entity.onDestroy();
        }
    }
    
    @NotNull
    public static HariantPlayer createPlayer(@NotNull Player player, @NotNull HeroInstance heroInstance) {
        final UUID uniqueId = player.getUniqueId();
        
        if (handler.entityMap.containsKey(uniqueId)) {
            throw new IllegalStateException("HariantPlayer already exists for `%s`!".formatted(player.getName()));
        }
        
        return createEntity(() -> new HariantPlayer(
                Objects.requireNonNull(handler.profiles.get(player.getUniqueId()), "Cannot create HariantPlayer without a profile!"),
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
        
        if (handler.profiles.containsKey(uuid)) {
            throw new IllegalStateException("Profile already exists for %s!".formatted(player.getName()));
        }
        
        final PlayerProfile profile = new PlayerProfile(player);
        handler.profiles.put(uuid, profile);
        profile.onCreate();
        
        return profile;
    }
    
    public static void destroyProfile(@NotNull Player player) {
        final PlayerProfile profile = handler.profiles.remove(player.getUniqueId());
        
        if (profile != null) {
            profile.onDestroy();
        }
    }
    
    public static boolean isGameInProgress() {
        return handler.currentGameInstance != null;
    }
    
    public static boolean isGameInProgressButNotActive() {
        return handler.currentGameInstance != null && handler.currentGameInstance.getState() != GameInstanceState.ACTIVE;
    }
    
    @NotNull
    public static SecurityManager getSecurityManager() {
        return handler.securityManager;
    }
    
    public static void hideBukkitEntity(@NotNull Entity entity) {
        Bukkit.getOnlinePlayers().forEach(player -> player.hideEntity(plugin, entity));
    }
    
    public static void showBukkitEntity(@NotNull Entity entity) {
        Bukkit.getOnlinePlayers().forEach(player -> player.showEntity(plugin, entity));
    }
    
    public static int getPlayerProfileCount() {
        return handler.profiles.size();
    }
    
    private static void clearEntities() {
        handler.entityMap.values().removeIf(entity -> {
            if (entity.isPersistent()) {
                return false;
            }
            
            entity.remove();
            return true;
        });
    }
    
}