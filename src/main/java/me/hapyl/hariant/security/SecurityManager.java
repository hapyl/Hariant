package me.hapyl.hariant.security;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface SecurityManager {
    
    void kick(@NotNull Player player, @NotNull KickReason kickReason);
    
}
