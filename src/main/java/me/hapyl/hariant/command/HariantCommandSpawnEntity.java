package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.TypeConverter;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.vanilla.VanillaEntity;
import me.hapyl.hariant.entity.vanilla.VanillaEntityType;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class HariantCommandSpawnEntity extends HariantPlayerCommand {
    
    private static final String ARGUMENT_PREFIX = "-";
    
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
        
        // Parse spawn arguments
        if (args.length > 1) {
            if ((args.length - 1) % 2 != 0) {
                throw new IllegalArgumentException("Arguments must be divisible by 2!");
            }
            
            for (int i = 1; i < args.length; i += 2) {
                final String argument = args.get(i).toString();
                final TypeConverter argumentValue = args.get(i + 1);
                
                if (!argument.startsWith(ARGUMENT_PREFIX)) {
                    throw new IllegalArgumentException("Arguments must start with `%s`!".formatted(ARGUMENT_PREFIX));
                }
                
                final SpawnArgument spawnArgument = SpawnArgument.VALUES_MAPPED.get(argument.toLowerCase());
                
                if (spawnArgument == null) {
                    throw new IllegalArgumentException("Unknown argument: " + argument);
                }
                
                spawnArgument.apply(entity, argumentValue);
            }
        }
        
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
        else if (args.length % 2 == 0) {
            return SpawnArgument.VALUES_AS_STRING;
        }
        
        return List.of();
    }
    
    public enum SpawnArgument {
        
        SET_HEALTH("-h") {
            @Override
            public void apply(@NotNull HariantEntity entity, @NotNull TypeConverter value) throws RuntimeException {
                final double health = value.toDouble();
                
                if (health <= 0) {
                    throw new IllegalArgumentException("Value cannot be negative!");
                }
                
                entity.getAttributes().set(AttributeType.MAX_HEALTH, health);
                entity.setHealth(health);
            }
        };
        
        private static final List<String> VALUES_AS_STRING = Arrays.stream(values()).map(argument -> argument.argument).toList();
        private static final Map<String, SpawnArgument> VALUES_MAPPED = Arrays.stream(values()).collect(Collectors.toMap(argument -> argument.argument, argument -> argument));
        
        private final String argument;
        
        SpawnArgument(@NotNull String argument) {
            this.argument = argument;
        }
        
        public void apply(@NotNull HariantEntity entity, @NotNull TypeConverter value) throws RuntimeException {
            throw new IllegalStateException();
        }
        
    }
    
}