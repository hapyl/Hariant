package me.hapyl.hariant.profile.ui;

import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.eterna.module.player.ScoreboardBuilder;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.game.GameInstance;
import me.hapyl.hariant.lobby.LobbyItemPlayerProfile;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.profile.VanillaTeamManager;
import me.hapyl.hariant.profile.notification.NotificationHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public final class PlayerUI implements Ticking {
    
    private static final PlayerUIFormatter FORMATTER_LOBBY = new PlayerUIFormatterLobbyImpl();
    private static final PlayerUIFormatter FORMATTER_SPECTATOR = new PlayerUIFormatterSpectatorImpl();
    
    private static final int UPDATE_TIME_FAST = 5;
    private static final int UPDATE_TIME_SLOW = 20;
    
    private final PlayerProfile profile;
    
    private final Scoreboard scoreboard;
    private final VanillaTeamManager vanillaTeamManager;
    private final ScoreboardBuilder scoreboardBuilder;
    
    private final PlayerTablist tablist;
    private int tick;
    private boolean updateNotifications;
    
    public PlayerUI(@NotNull PlayerProfile profile) {
        this.profile = profile;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.vanillaTeamManager = new VanillaTeamManager(scoreboard, profile);
        this.scoreboardBuilder = new ScoreboardBuilder(scoreboard, profile.getPlayer(), Hariant.GAME_NAME);
        
        this.tablist = new PlayerTablist(profile);
        this.tablist.show();
    }
    
    @Override
    public void tick() {
        // Tick important ui elements
        if (tick % UPDATE_TIME_FAST == 0) {
            profile.getHariantPlayer().ifPresent(HariantPlayer::tickActionbar);
            
            // Tick vanilla team manager
            vanillaTeamManager.tick();
        }
        
        // Tick less important ui elements
        if (tick % UPDATE_TIME_SLOW == 0) {
            final PlayerUIFormatter formatter = this.formatter();
            
            this.updateScoreboard(formatter);
            this.updateTablist(formatter);
            this.updateNotifications(formatter);
        }
        
        tick++;
    }
    
    private void updateNotifications(@NotNull PlayerUIFormatter formatter) {
        // Only call updates if entity does not exist for the player
        if (Hariant.entityExists(profile.getUuid())) {
            return;
        }
        
        
        
        final int numberOfNotifications = NotificationHandler.getNotifications(profile).sizeFiltered();
        
        // If player has notifications, blink the player head
        if (numberOfNotifications > 0) {
            updateNotifications = true;
            LobbyItemPlayerProfile.give(profile, Hariant.currentTickMod20() ? numberOfNotifications : 0);
        }
        // Otherwise, update the item once
        else {
            if (updateNotifications) {
                updateNotifications = false;
                LobbyItemPlayerProfile.give(profile, 0);
            }
        }
        
        
    }
    
    @NotNull
    public VanillaTeamManager getVanillaTeamManager() {
        return vanillaTeamManager;
    }
    
    public @NotNull PlayerUIFormatter formatter() {
        if (profile.isSpectator()) {
            return FORMATTER_SPECTATOR;
        }
        else if (Hariant.getCurrentGameInstanceOrNull() instanceof GameInstance gameInstance) {
            return gameInstance;
        }
        
        return FORMATTER_LOBBY;
    }
    
    private void updateScoreboard(@NotNull PlayerUIFormatter formatter) {
        final ComponentList components = ComponentList.empty();
        components.append(Component.text(this.getTodayFormatted(), Colors.DARK_GRAY));
        components.append(Component.empty());
        
        // Pass to the formatter
        formatter.formatScoreboard(profile, components);
        
        scoreboardBuilder.setLines(components);
    }
    
    private void updateTablist(@NotNull PlayerUIFormatter formatter) {
        tablist.update(formatter);
    }
    
    @NotNull
    private String getTodayFormatted() {
        final LocalDate localDate = LocalDate.now();
        
        return "%s/%s/%s".formatted(localDate.getDayOfMonth(), localDate.getMonthValue(), localDate.getYear());
    }
    
}
