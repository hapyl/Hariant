package me.hapyl.hariant;

import me.hapyl.eterna.EternaAPI;
import me.hapyl.hariant.command.HariantCommandRegistry;
import me.hapyl.hariant.config.HariantConfig;
import me.hapyl.hariant.config.HariantConfigImpl;
import me.hapyl.hariant.database.Database;
import me.hapyl.hariant.database.DatabaseSyncer;
import me.hapyl.hariant.entity.EntityGarbageCollector;
import me.hapyl.hariant.event.HariantEntityMoveEvent;
import me.hapyl.hariant.game.booster.BoosterHandler;
import me.hapyl.hariant.handler.*;
import me.hapyl.hariant.npc.NpcHandler;
import me.hapyl.hariant.weapon.ability.AbilityHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public final class HariantPlugin extends JavaPlugin {
    
    /**
     * Defines the minimum EternaAPI version plugin requires, using any versions below that will shut down the server.
     */
    public static final String REQUIRED_ETERNA_VERSION = "6.2.16";
    
    private HariantConfig config;
    private Database database;
    private DatabaseSyncer databaseSyncer;
    
    @Override
    public void onDisable() {
        Hariant.HANDLER.onDestroy();
        database.close();
    }
    
    @Override
    public void onEnable() {
        Hariant.PLUGIN = this;
        
        // Instantiate EternaAPI
        EternaAPI.instantiate(this, REQUIRED_ETERNA_VERSION);
        
        // Load config
        config = new HariantConfigImpl(this);
        
        // Register commands
        new HariantCommandRegistry(this);
        
        // Load database
        database = new Database(this);
        
        final BukkitScheduler scheduler = Bukkit.getScheduler();
        final PluginManager pluginManager = Bukkit.getPluginManager();
        
        // Schedule database syncer
        databaseSyncer = new DatabaseSyncer();
        scheduler.scheduleSyncRepeatingTask(this, databaseSyncer, HariantConstants.DATABASE_SYNC_PERIOD, HariantConstants.DATABASE_SYNC_PERIOD);
        pluginManager.registerEvents(databaseSyncer, this);
        
        // Load manager
        Hariant.HANDLER = new Hariant(this);
        
        // Load listeners
        pluginManager.registerEvents(new PlayerHandler(), this);
        pluginManager.registerEvents(new EntityHandler(), this);
        pluginManager.registerEvents(new ProjectileHandler(), this);
        pluginManager.registerEvents(new HariantEntityMoveEvent.Handler(), this);
        pluginManager.registerEvents(new AbilityHandler(), this);
        pluginManager.registerEvents(new NpcHandler(), this);
        pluginManager.registerEvents(new PlayerSitHandler(), this);
        pluginManager.registerEvents(new EntityGarbageCollector(), this);
        pluginManager.registerEvents(new ServerHandler(), this);
        pluginManager.registerEvents(new BoosterHandler(), this);
        
        for (final World world : Bukkit.getWorlds()) {
            setDefaultGamerules(world);
            
            // Unload non-normal worlds
            if (world.getEnvironment() != World.Environment.NORMAL) {
                Bukkit.getServer().unloadWorld(world, false);
            }
        }
    }
    
    @NotNull
    public DatabaseSyncer getDatabaseSyncer() {
        return databaseSyncer;
    }
    
    @NotNull
    public HariantConfig config() {
        return config;
    }
    
    @NotNull
    public Database getDatabase() {
        return database;
    }
    
    @NotNull
    public static RuntimeException severeExceptionShutdownServer(@NotNull RuntimeException ex) {
        final Logger logger = Hariant.PLUGIN.getLogger();
        
        logger.severe("");
        logger.severe("+---------------------------------------------------------+");
        logger.severe("A SEVERE EXCEPTION OCCURRED, THE SERVER WILL NOW SHUT DOWN!");
        logger.severe(" %s:".formatted(ex.getClass().getSimpleName()));
        logger.severe("  %s".formatted(ex.getMessage()));
        logger.severe("+---------------------------------------------------------+");
        logger.severe("");
        
        Bukkit.getScheduler().runTask(Hariant.PLUGIN, () -> Bukkit.getServer().shutdown());
        return ex;
    }
    
    private static void setDefaultGamerules(@NotNull World world) {
        world.setGameRule(GameRules.ADVANCE_TIME, false);
        world.setGameRule(GameRules.ADVANCE_WEATHER, false);
        world.setGameRule(GameRules.ALLOW_ENTERING_NETHER_USING_PORTALS, false);
        world.setGameRule(GameRules.BLOCK_DROPS, false);
        world.setGameRule(GameRules.BLOCK_EXPLOSION_DROP_DECAY, false);
        world.setGameRule(GameRules.COMMAND_BLOCK_OUTPUT, false);
        world.setGameRule(GameRules.COMMAND_BLOCKS_WORK, true);
        world.setGameRule(GameRules.DROWNING_DAMAGE, true);
        world.setGameRule(GameRules.ELYTRA_MOVEMENT_CHECK, false);
        world.setGameRule(GameRules.ENDER_PEARLS_VANISH_ON_DEATH, true);
        world.setGameRule(GameRules.ENTITY_DROPS, false);
        world.setGameRule(GameRules.FALL_DAMAGE, true);
        world.setGameRule(GameRules.FIRE_DAMAGE, true);
        world.setGameRule(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER, 0);
        world.setGameRule(GameRules.FORGIVE_DEAD_PLAYERS, false);
        world.setGameRule(GameRules.FREEZE_DAMAGE, true);
        world.setGameRule(GameRules.GLOBAL_SOUND_EVENTS, false);
        world.setGameRule(GameRules.IMMEDIATE_RESPAWN, true);
        world.setGameRule(GameRules.KEEP_INVENTORY, true);
        world.setGameRule(GameRules.LAVA_SOURCE_CONVERSION, false);
        world.setGameRule(GameRules.LIMITED_CRAFTING, false);
        world.setGameRule(GameRules.LOCATOR_BAR, false);
        world.setGameRule(GameRules.LOG_ADMIN_COMMANDS, true);
        world.setGameRule(GameRules.MAX_BLOCK_MODIFICATIONS, 100_000);
        world.setGameRule(GameRules.MAX_COMMAND_FORKS, 100_000);
        world.setGameRule(GameRules.MAX_COMMAND_SEQUENCE_LENGTH, 100_000);
        world.setGameRule(GameRules.MAX_ENTITY_CRAMMING, 0);
        world.setGameRule(GameRules.MAX_SNOW_ACCUMULATION_HEIGHT, 0);
        world.setGameRule(GameRules.MOB_DROPS, false);
        world.setGameRule(GameRules.MOB_EXPLOSION_DROP_DECAY, false);
        world.setGameRule(GameRules.MOB_GRIEFING, false);
        world.setGameRule(GameRules.NATURAL_HEALTH_REGENERATION, false);
        world.setGameRule(GameRules.PLAYER_MOVEMENT_CHECK, false);
        world.setGameRule(GameRules.PLAYERS_NETHER_PORTAL_CREATIVE_DELAY, 0);
        world.setGameRule(GameRules.PLAYERS_NETHER_PORTAL_DEFAULT_DELAY, 0);
        world.setGameRule(GameRules.PLAYERS_SLEEPING_PERCENTAGE, 100);
        world.setGameRule(GameRules.PROJECTILES_CAN_BREAK_BLOCKS, false);
        world.setGameRule(GameRules.PVP, true);
        world.setGameRule(GameRules.RAIDS, false);
        world.setGameRule(GameRules.RANDOM_TICK_SPEED, 0);
        world.setGameRule(GameRules.REDUCED_DEBUG_INFO, false);
        world.setGameRule(GameRules.RESPAWN_RADIUS, 0);
        world.setGameRule(GameRules.SEND_COMMAND_FEEDBACK, true);
        world.setGameRule(GameRules.SHOW_ADVANCEMENT_MESSAGES, false);
        world.setGameRule(GameRules.SHOW_DEATH_MESSAGES, false);
        world.setGameRule(GameRules.SPAWN_MOBS, false);
        world.setGameRule(GameRules.SPAWN_MONSTERS, false);
        world.setGameRule(GameRules.SPAWN_PATROLS, false);
        world.setGameRule(GameRules.SPAWN_PHANTOMS, false);
        world.setGameRule(GameRules.SPAWN_WANDERING_TRADERS, false);
        world.setGameRule(GameRules.SPAWN_WARDENS, false);
        world.setGameRule(GameRules.SPAWNER_BLOCKS_WORK, false);
        world.setGameRule(GameRules.SPECTATORS_GENERATE_CHUNKS, false);
        world.setGameRule(GameRules.SPREAD_VINES, false);
        world.setGameRule(GameRules.TNT_EXPLODES, false);
        world.setGameRule(GameRules.TNT_EXPLOSION_DROP_DECAY, false);
        world.setGameRule(GameRules.UNIVERSAL_ANGER, false);
        world.setGameRule(GameRules.WATER_SOURCE_CONVERSION, false);
    }
}
