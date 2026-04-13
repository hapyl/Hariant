package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.text.Capitalizable;
import me.hapyl.eterna.module.util.TypeConverter;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.entity.EntitySpawner;
import me.hapyl.hariant.entity.VanillaEntityType;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HariantCommandSpawnEntity extends HariantPlayerCommand {
    
    public HariantCommandSpawnEntity(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    protected void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        // entity (entity_type)
        final TypeConverter argument0 = args.get(0);
        final VanillaEntityType<? extends LivingEntity> entityType = VanillaEntityType.byName(argument0.toString());
        
        if (entityType == null) {
            HariantLogger.error(player, Component.text("Invalid entity type: %s".formatted(argument0)));
            return;
        }
        
        Hariant.createEntity(EntitySpawner.ofBukkit(player.getLocation(), entityType, self -> {}));
        
        HariantLogger.success(player, Component.text("Spawned %s!".formatted(Capitalizable.capitalize(entityType.getName()))));
    }
    
}
