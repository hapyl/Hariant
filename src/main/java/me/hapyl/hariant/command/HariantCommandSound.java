package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.command.completer.CompleterMethod;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.rank.PlayerRank;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class HariantCommandSound extends HariantPlayerCommand {
    
    private final List<String> soundKeys;
    
    public HariantCommandSound(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
        
        this.soundKeys = Registry.SOUNDS.keyStream()
                                        .map(NamespacedKey::getKey)
                                        .toList();
    }
    
    @Override
    public @NotNull CompleterMethod completerMethod() {
        return CompleterMethod.CONTAINS;
    }
    
    @Override
    public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        final NamespacedKey key = NamespacedKey.minecraft(args.get(0).toString());
        final float pitch = Math.clamp(args.get(1).toFloat(1.0f), 0, 2);
        
        final Sound sound = Registry.SOUNDS.get(key);
        
        if (sound == null) {
            HariantLogger.error(player, Component.text("Invalid key: %s".formatted(key.asMinimalString())));
            return;
        }
        
        player.playSound(player, sound, 3, pitch);
        HariantLogger.success(player, Component.text("Played `%s` @ %.2f pitch.".formatted(key.asMinimalString(), pitch)));
    }
    
    @Override
    public @NotNull List<String> tabComplete(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        if (args.length == 1) {
            return soundKeys;
        }
        
        return List.of();
    }
    
}