package me.hapyl.hariant.database;

import me.hapyl.eterna.module.util.NanoBenchmark;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.game.GameInstance;
import me.hapyl.hariant.game.GameInstanceHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class DatabaseSyncer implements Runnable, GameInstanceHandler {
    
    private boolean scheduleSync;
    
    public DatabaseSyncer() {
    }
    
    @Override
    public void run() {
        if (scheduleSync) {
            return;
        }
        
        // If no one is online, skip syncing
        if (Hariant.getPlayerProfileCount() == 0) {
            this.broadcast(Component.text("No one is online, skipping database sync.", Colors.GRAY, TextDecoration.ITALIC));
            return;
        }
        
        // If the game is currently in progress, schedule to sync after the game
        if (Hariant.isGameInProgress()) {
            scheduleSync = true;
            Bukkit.broadcast(Component.text("Scheduled to sync the database after this game.", Colors.GRAY, TextDecoration.ITALIC));
            return;
        }
        
        this.sync();
    }
    
    public void sync() {
        scheduleSync = false;
        
        this.broadcast(Component.text("Syncing database, might lag a little...", Colors.GRAY, TextDecoration.ITALIC));
        
        final NanoBenchmark benchmark = NanoBenchmark.ofNow();
        
        Hariant.getPlayerProfiles().forEach(profile -> {
            profile.getDatabase().save();
        });
        
        benchmark.step("sync");
        
        // Cannot throw
        final NanoBenchmark.Result result = benchmark.getFirstResult().orElse(null);
        final long millisTookToSync = result != null ? result.asMillis() : -1L;
        
        this.broadcast(
                Component.empty()
                        .append(Component.text("Database successfully synced! ", Colors.SUCCESS))
                        .append(Component.text("(%sms)".formatted(millisTookToSync), NamedTextColor.DARK_GRAY))
        );
    }
    
    private void broadcast(@NotNull Component text) {
        Bukkit.broadcast(text);
    }
    
    @Override
    public void handleInstanceCreated(@NotNull GameInstance gameInstance) {
    }
    
    @Override
    public void handlerInstanceDestroyed(@NotNull GameInstance gameInstance) {
        if (scheduleSync) {
            this.sync();
        }
    }
}
