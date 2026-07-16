package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.entity.vanilla.VanillaEntity;
import me.hapyl.hariant.entity.vanilla.VanillaEntityType;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class HariantCommandSpawnEntity extends HariantPlayerCommand {
    
    public HariantCommandSpawnEntity(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        // entity (entity_type)
        final Key key = args.get(0).toKey();
        
        if (key == null) {
            HariantLogger.error(player, Component.text("Invalid key: %s!".formatted(args.get(0))));
            return;
        }
        
        final VanillaEntityType<? extends LivingEntity> entityType = VanillaEntityType.byKey(key);
        
        if (entityType == null) {
            HariantLogger.error(player, Component.text("Unknown entity type: %s!".formatted(key)));
            return;
        }
        
        final VanillaEntity<? extends LivingEntity> entity = Hariant.createEntity(() -> entityType.spawn(player.getLocation()));
        
        HariantLogger.success(
                player,
                Component.empty()
                         .append(Component.text("Spawned "))
                         .append(entity.getName())
                         .append(Component.text("!"))
        );
    }
    
    @Override
    public @NotNull List<String> tabComplete(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        if (args.length == 1) {
            return VanillaEntityType.listKeys();
        }
        
        return List.of();
    }
}
