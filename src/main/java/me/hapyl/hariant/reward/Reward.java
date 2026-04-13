package me.hapyl.hariant.reward;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface Reward extends Keyed, Named {
    
    @NotNull
    @Override
    Key getKey();
    
    @Override
    @NotNull
    Component getName();
    
    void reward(@NotNull PlayerProfile profile);
    
}
