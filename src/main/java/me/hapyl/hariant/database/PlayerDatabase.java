package me.hapyl.hariant.database;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import me.hapyl.eterna.module.util.Enums;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.HariantPlugin;
import me.hapyl.hariant.database.problem.*;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.database.serialize.MongoSerializable;
import me.hapyl.hariant.dialog.DialogDatabaseEntry;
import me.hapyl.hariant.hero.HeroDirectory;
import me.hapyl.hariant.inventory.HariantInventory;
import me.hapyl.hariant.level.LevelEntry;
import me.hapyl.hariant.profile.setting.SettingEntry;
import me.hapyl.hariant.security.KickReason;
import me.hapyl.hariant.task.InternalTasks;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public sealed class PlayerDatabase permits PlayerDatabaseView {
    
    public final HariantInventory inventory;
    public final HeroDirectory hero;
    public final SettingEntry settings;
    public final LevelEntry level;
    public final DialogDatabaseEntry dialog;
    
    private final Database database;
    private final UUID uuid;
    private final Bson filter;
    
    private final Document root;
    private final List<PlayerDatabaseEntry> entries;
    
    public PlayerDatabase(@NotNull Database database, @NotNull UUID uuid) {
        this.database = database;
        this.uuid = uuid;
        this.filter = new Document("_id", uuid.toString());
        
        // Load `root` document
        try {
            final MongoCollection<Document> players = this.database.getCollection(DatabaseCollection.PLAYERS);
            Document root = players.find(filter).first();
            
            // If there are no Document, means player joined for the first time, so create the Document
            if (root == null) {
                players.insertOne(root = createDefaultDocument(uuid));
            }
            
            this.root = root;
        }
        catch (Exception ex) {
            throw HariantPlugin.severeExceptionShutdownServer(new RuntimeException(ex));
        }
        
        // Deserialize entries
        this.entries = Lists.newArrayList();
        
        final ProblemReporter problemReporter = new ProblemReporterImpl();
        
        /*
            Note that the load order is VERY important, since HeroDirectory loads artifacts by UUID,
            which requires artifacts to be loaded before heroes!
         */
        
        this.inventory = deserialize("inventory", HariantInventory.class, problemReporter);
        this.hero = deserialize("hero", HeroDirectory.class, problemReporter);
        this.settings = deserialize("setting", SettingEntry.class, problemReporter);
        this.level = deserialize("level", LevelEntry.class, problemReporter);
        this.dialog = deserialize("dialog", DialogDatabaseEntry.class, problemReporter);
        
        // Handle problems
        problemReporter.handle(problem -> {
            final ProblemType problemType = problem.getProblemType();
            
            // `FINE` problems send a warning message
            if (problemType == ProblemType.WARNING) {
                HariantLogger.logger().warning(problem.toString());
            }
            
            // `SEVERE` problems throw error, and if player is online, kicks them
            else if (problemType == ProblemType.SEVERE) {
                // If the player is online, kick them
                if (getPlayer() instanceof Player onlinePlayer) {
                    Hariant.getSecurityManager().kick(
                            onlinePlayer,
                            KickReason.create(
                                    onlinePlayer.getUniqueId(),
                                    "There was an error loading your database, try logging again. If the issue persists, contact support.",
                                    problem.toString()
                            )
                    );
                }
                
                throw new RuntimeException(problem.toString());
            }
        });
    }
    
    @NotNull
    public Document getRoot() {
        return root;
    }
    
    @NotNull
    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }
    
    @NotNull
    public PlayerRank getRank() {
        return Enums.byName(PlayerRank.class, root.get("player_rank", "default"), PlayerRank.DEFAULT);
    }
    
    public void setRank(@NotNull PlayerRank rank) {
        root.put("player_rank", rank.name().toLowerCase());
    }
    
    @NotNull
    public UUID getUuid() {
        return uuid;
    }
    
    public void save() {
        this.save(true);
    }
    
    public void save(boolean async) {
        root.append("last_known_name", Bukkit.getOfflinePlayer(uuid).getName());
        root.append("last_online", System.currentTimeMillis());
        
        final ProblemReporter problemReporter = new ProblemReporterImpl();
        
        // Serialize entries
        entries.forEach(entry -> {
            // Write to the existing document
            final Document document = entry.getDocument();
            entry.write(this, document, problemReporter);
            
            root.put(entry.getParent(), document);
        });
        
        // Report any problem on save
        problemReporter.handle(new ProblemHandler() {
            private static final Logger logger = Hariant.getPlugin().getLogger();
            
            @Override
            public void handle(@NotNull Problem problem) {
                logger.warning(problem.toString());
            }
        });
        
        // Save
        final Runnable runnable = () -> database.getCollection(DatabaseCollection.PLAYERS).replaceOne(filter, root);
        
        if (async) {
            InternalTasks.asynchronously(runnable);
        }
        else {
            runnable.run();
        }
    }
    
    @NotNull
    public String getName() {
        return root.get("last_known_name", "");
    }
    
    private <E extends PlayerDatabaseEntry> E deserialize(@NotNull String parent, @NotNull Class<E> clazz, @NotNull ProblemReporter problemReporter) {
        final E deserialize = MongoSerializable.deserialize(parent, clazz, this, root, problemReporter);
        
        // Add to the list for serialization
        entries.add(deserialize);
        return deserialize;
    }
    
    @NotNull
    private static Document createDefaultDocument(@NotNull UUID uuid) {
        final Document document = new Document("_id", uuid.toString());
        
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        
        document.put("last_known_name", offlinePlayer.getName());
        document.put("player_rank", PlayerRank.DEFAULT.name().toLowerCase());
        
        return document;
    }
}
