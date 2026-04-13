package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Enums;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.element.anomaly.ElementalAnomaly;
import me.hapyl.hariant.element.anomaly.EnumAnomaly;
import me.hapyl.hariant.inventory.item.Item;
import me.hapyl.hariant.inventory.item.ItemCreator;
import me.hapyl.hariant.inventory.item.ItemRegistry;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class HariantCommandCreateItemPreview extends HariantPlayerCommand {
    
    public HariantCommandCreateItemPreview(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    protected void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        enum Registry {
            ITEM {
                @NotNull
                @Override
                public Optional<ItemCreator> get(@NotNull PlayerDatabase database, @NotNull Key key) {
                    final Item item = ItemRegistry.getRegistry().get(key).orElse(null);
                    
                    return item != null ? Optional.of(item.newInstance(database, UUID.randomUUID())) : Optional.empty();
                }
            },
            
            TALENT {
                @NotNull
                @Override
                public Optional<ItemCreator> get(@NotNull PlayerDatabase database, @NotNull Key key) {
                    return Optional.ofNullable(TalentRegistry.getRegistry().get(key).orElse(null));
                }
            },
            
            ANOMALY {
                @NotNull
                @Override
                public Optional<ItemCreator> get(@NotNull PlayerDatabase database, @NotNull Key key) {
                    final EnumAnomaly elementalAnomaly = Enums.byName(EnumAnomaly.class, key.toString());
                    
                    if (elementalAnomaly == null) {
                        return Optional.empty();
                    }
                    
                    return Optional.of(() -> {
                        return new ItemBuilder(Material.STONE)
                                .setName(elementalAnomaly.getName())
                                .addWrappedLore(elementalAnomaly.getDescription());
                    });
                }
            },
            
            ;
            
            @NotNull
            public Optional<ItemCreator> get(@NotNull PlayerDatabase database, @NotNull Key key) {
                return Optional.empty();
            }
        }
        
        final Registry registry = args.get(0).toEnum(Registry.class);
        
        if (registry == null) {
            HariantLogger.error(
                    player,
                    Component.text("Unknown registry, valid values: %s".formatted(Arrays.stream(Registry.values()).map(e -> e.name().toLowerCase()).collect(Collectors.joining(", "))))
            );
            return;
        }
        
        final Key key = args.get(1).toKey();
        
        if (key == null) {
            HariantLogger.error(
                    player,
                    Component.text("Malformed key: `%s`!".formatted(args.get(1).toString()))
            );
            return;
        }
        
        final PlayerDatabase database = Hariant.getPlayerDatabase(player);
        
        registry.get(database, key).ifPresentOrElse(creator -> {
            player.getInventory().addItem(creator.createItem());
            
            HariantLogger.success(player, Component.text("Created item for `%s`!".formatted(key)));
        }, () -> {
            HariantLogger.error(player, Component.text("Could not find item `%s` in `%s` registry.".formatted(key, registry.name().toLowerCase())));
        });
    }
}
