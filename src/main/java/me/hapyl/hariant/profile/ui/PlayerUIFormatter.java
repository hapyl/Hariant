package me.hapyl.hariant.profile.ui;

import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.eterna.module.player.tablist.EntryList;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface PlayerUIFormatter {
    
    void formatScoreboard(@NotNull PlayerProfile profile, @NotNull ComponentList components);
    
    void formatTablistSystem(@NotNull PlayerProfile profile, @NotNull EntryList entryList);
    
    @NotNull Component createTablistFooter(@NotNull PlayerProfile profile, @NotNull PlayerUIFormatter formatter);
}
