package me.hapyl.hariant.profile.ui;

import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.hariant.profile.PlayerProfile;
import org.jetbrains.annotations.NotNull;

public interface PlayerUIFormatter {
    
    @NotNull
    PlayerUIFormatter LOBBY = new PlayerUIFormatterLobbyImpl();
    
    @NotNull
    PlayerUIFormatter SPECTATOR = new PlayerUIFormatterSpectatorImpl();
    
    void formatScoreboard(@NotNull PlayerProfile profile, @NotNull ComponentList components);
    
}
