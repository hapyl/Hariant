package me.hapyl.hariant.profile.ui;

import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public class PlayerUIFormatterSpectatorImpl extends PlayerUIFormatterLobbyImpl {
    
    PlayerUIFormatterSpectatorImpl() {
    }
    
    @Override
    public void formatScoreboard(@NotNull PlayerProfile profile, @NotNull ComponentList components) {
        components.append(Component.text("SPECTATOR", Colors.GOLD, TextDecoration.BOLD));
        
        components.append(Component.text(" Yeah, there isn't really", Colors.GRAY));
        components.append(Component.text(" much here because spectator", Colors.GRAY));
        components.append(Component.text(" isn't quite implemented yet.", Colors.GRAY));
        
        // TODO @Feb 25, 2026 (xanyjl) -> Show teams
    }
    
}
