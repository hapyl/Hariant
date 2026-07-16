package me.hapyl.hariant.database;

import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.event.HariantGameInstanceStateEvent;
import me.hapyl.hariant.game.GameInstance;
import me.hapyl.hariant.game.GameInstanceState;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class DatabaseSyncer implements Runnable, Listener {
    
    private boolean scheduleSync;

    @EventHandler
    public void handleHariantGameInstanceStateEvent(HariantGameInstanceStateEvent ev) {
        if (ev.getState() == GameInstanceState.FINISHED && scheduleSync) {
            this.sync();
        }
    }
    
    @Override
    public void run() {
        if (this.scheduleSync) {
            return;
        }
        
        // If no one is online, skip syncing
        if (Hariant.getPlayerProfileCount() == 0) {
            this.broadcast(Component.text("No one is online, skipping database sync.", Colors.LIGHT_GRAY, TextDecoration.ITALIC));
            return;
        }
        
        // If the game is currently in progress, schedule to sync after the game
        if (Hariant.isGameInProgress()) {
            this.scheduleSync = true;
            this.broadcast(Component.text("Scheduled to sync the database after this game.", Colors.LIGHT_GRAY, TextDecoration.ITALIC));
            return;
        }
        
        this.sync();
    }
    
    public void sync() {
        this.scheduleSync = false;
        
        this.broadcast(Component.text("Syncing database, might lag a little...", Colors.LIGHT_GRAY, TextDecoration.ITALIC));
        
        final long startSyncAt = System.currentTimeMillis();
        
        Hariant.getPlayerProfiles().forEach(profile -> {
            profile.getDatabase().save();
        });
        
        // Cannot throw
        final long millisTookToSync = System.currentTimeMillis() - startSyncAt;
        
        this.broadcast(
                Component.empty()
                         .append(Component.text("Database successfully synced! ", Colors.SUCCESS))
                         .append(Component.text("(%sms)".formatted(millisTookToSync), Colors.DARK_GRAY))
        );
    }
    
    private void broadcast(@NotNull Component text) {
        HariantLogger.PREFIX_INFO.broadcastMessage(text);
    }
    
}