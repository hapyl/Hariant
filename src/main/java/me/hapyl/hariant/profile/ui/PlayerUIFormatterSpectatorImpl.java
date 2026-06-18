package me.hapyl.hariant.profile.ui;

import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public class PlayerUIFormatterSpectatorImpl implements PlayerUIFormatter {
    
    PlayerUIFormatterSpectatorImpl() {
    }
    
    @Override
    public void formatScoreboard(@NotNull PlayerProfile profile, @NotNull ComponentList components) {
        components.append(Component.text("SPECTATOR", Colors.GOLD, TextDecoration.BOLD));
        
        // TODO @Feb 25, 2026 (xanyjl) -> Show teams
        
    }
    
}
