package me.hapyl.hariant.profile;

import io.papermc.paper.registry.keys.SoundEventKeys;
import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.text.Capitalizable;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.profile.setting.Settings;
import me.hapyl.hariant.task.InternalTasks;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum AutoReady implements ComponentLike, Described {
    
    NEVER(Component.text("Never ready automatically.")),
    ALWAYS(Component.text("Always ready automatically.")),
    ALWAYS_EXCEPT_ON_JOIN(Component.text("Always ready automatically, except when you join the server."));
    
    private static final Sound AUTO_READY_SOUND = Sound.sound(SoundEventKeys.BLOCK_NOTE_BLOCK_PLING, Sound.Source.UI, 3, 2.0f);
    
    private final Component component;
    private final Component description;
    
    AutoReady(@NotNull Component description) {
        this.component = Component.text(Capitalizable.capitalize(this));
        this.description = description;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return component;
    }
    
    @Override
    public @NotNull Component getDescription() {
        return description;
    }
    
    public static void scheduleAutoReady(@NotNull PlayerProfile playerProfile, @Nullable AutoReady exception, int delay) {
        // If there is currently a countdown, return
        if (Hariant.isCountdownActive()) {
            return;
        }
        
        // If we can't start the game instance, return
        if (!Hariant.canStartNewGameInstance().booleanValue()) {
            return;
        }
        
        // Otherwise check for AutoReady and toggle ready after the given delay
        final AutoReady currentAutoReady = playerProfile.getDatabase().settings.getValue(Settings.AUTO_READY);
        
        // If auto ready is set to NEVER or to the exception, return
        if (currentAutoReady == NEVER || currentAutoReady == exception) {
            return;
        }
        
        InternalTasks.later(() -> {
            // If player is already ready, return
            if (playerProfile.isReady()) {
                return;
            }
            
            // Otherwise toggle ready
            playerProfile.setReady(true);
            
            // Notify about auto read
            playerProfile.sendActionBar(Component.text("Automatically readied for you!", Colors.GREEN));
            playerProfile.playSound(AUTO_READY_SOUND);
        }, delay);
    }
    
}