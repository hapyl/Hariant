package me.hapyl.hariant.command;

import io.papermc.paper.registry.keys.SoundEventKeys;
import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.util.StringList;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.game.battleground.EnumBattleground;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HariantCommandGoTo extends HariantPlayerCommand {
    
    public HariantCommandGoTo(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        final EnumBattleground battleground = args.get(0).toEnum(EnumBattleground.class);
        
        if (battleground == null) {
            HariantLogger.error(player, Component.text("Unknown battleground!"));
            return;
        }
        
        player.teleport(battleground.getSpawnLocations().getFirst().getCenteredLocation());
        player.playSound(Sound.sound(SoundEventKeys.ENTITY_ENDERMAN_TELEPORT, Sound.Source.UI, 3, 1.0f));
        
        HariantLogger.success(player, Component.text("Teleported you to ").append(battleground.getName()).append(Component.text("!")));
    }
    
    @Override
    public @NotNull List<String> tabComplete(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        return StringList.ofEnumConstantLowercaseNames(EnumBattleground.class);
    }
    
}