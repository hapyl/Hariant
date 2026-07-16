package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Enums;
import me.hapyl.eterna.module.util.StringList;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.game.WinResult;
import me.hapyl.hariant.game.WinType;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.inventory.item.Item;
import me.hapyl.hariant.inventory.item.ItemCreator;
import me.hapyl.hariant.inventory.item.ItemRegistry;
import me.hapyl.hariant.lobby.EnumLobbyItem;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public final class HariantCommandCreate extends HariantPlayerCommand {
    
    private static final StringList OPERATIONS = StringList.ofEnumConstantLowercaseNames(Operation.class);
    
    public HariantCommandCreate(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        // create (operation) [args]
        final Operation operation = args.get(0).toEnum(Operation.class);
        
        if (operation == null) {
            HariantLogger.error(player, Component.text("Invalid operation, valid operations: %s".formatted(OPERATIONS)));
            return;
        }
        
        operation.execute(player, ArgumentList.copyOfRange(args, 1, args.length), playerRank);
    }
    
    @Override
    public @NotNull List<String> tabComplete(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        if (args.length == 0) {
            return List.of();
        }
        if (args.length == 1) {
            return OPERATIONS;
        }
        else {
            final Operation operation = args.get(0).toEnum(Operation.class);
            
            return operation != null ? operation.tabComplete(ArgumentList.copyOfRange(args, 1, args.length)) : List.of();
        }
    }
    
    public enum Operation implements Op {
        
        PLAYER {
            @Override
            public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
                final HariantPlayer existingPlayer = Hariant.getPlayer(player).orElse(null);
                
                if (existingPlayer == null) {
                    Hariant.createPlayer(player, Hariant.getPlayerDatabase(player).heroDirectory.getSelectedHeroInstance());
                    HariantLogger.success(player, Component.text("Successfully created player instance!", Colors.GREEN));
                }
                else {
                    Hariant.destroyEntity(player.getUniqueId());
                    HariantLogger.success(player, Component.text("Successfully deleted player instance!", Colors.RED));
                    
                    EnumLobbyItem.clearInventoryAndGiveAllItems(player);
                }
            }
        },
        
        GAME_INSTANCE {
            @Override
            public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
                if (Hariant.isGameInProgress()) {
                    Hariant.endCurrentGameInstance(WinResult.create(WinType.WIN_CONDITION_MET, List.of()));
                    HariantLogger.success(player, Component.text("Ended current game instance!"));
                }
                else {
                    Hariant.startNewGameInstance();
                    HariantLogger.success(player, Component.text("Created new game instance!"));
                }
            }
        },
        
        HERO {
            @Override
            public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
                final Player target = args.get(0).toPlayer();
                
                if (target == null) {
                    HariantLogger.error(player, Component.text("This player is not online!"));
                    return;
                }
                
                final Key key = args.get(1).toKey();
                
                if (key == null) {
                    HariantLogger.error(player, Component.text("Malformed key: %s".formatted(args.get(1))));
                    return;
                }
                
                final Hero hero = HeroRegistry.getRegistry().get(key).orElse(null);
                
                if (hero == null) {
                    HariantLogger.error(player, Component.text("Hero `%s` doesn't exist!".formatted(key)));
                    return;
                }
                
                final PlayerDatabase playerDatabase = Hariant.getPlayerDatabase(target);
                
                if (playerDatabase.heroDirectory.isOwned(hero)) {
                    HariantLogger.error(player, Component.text("%s already owns this hero!".formatted(target.getName())));
                    return;
                }
                
                final HeroInstance heroInstance = playerDatabase.heroDirectory.createHero(hero);
                
                HariantLogger.success(
                        player,
                        Component.empty()
                                 .append(Component.text("Successfully created `"))
                                 .append(hero.getName())
                                 .append(Component.text("` for %s!".formatted(target.getName())))
                                 .append(Component.text(" HOVER", Colors.GOLD, TextDecoration.BOLD))
                                 .hoverEvent(heroInstance.createHoverEvent())
                );
            }
            
            @NotNull
            @Override
            public List<String> tabComplete(@NotNull ArgumentList args) {
                if (args.length == 1) {
                    return StringList.ofOnlinePlayers();
                }
                else if (args.length == 2) {
                    return HeroRegistry.getRegistry().keysAsString();
                }
                
                return List.of();
            }
        },
        
        ITEM_PREVIEW {
            public enum Registry {
                ITEM {
                    @NotNull
                    @Override
                    public Optional<ItemCreator> get(@NotNull PlayerDatabase database, @NotNull Key key) {
                        final Item item = ItemRegistry.getRegistry().get(key).orElse(null);
                        
                        return item != null ? Optional.of(item.newInstance(database, UUID.randomUUID())) : Optional.empty();
                    }
                    
                    @NotNull
                    @Override
                    public List<String> listNames() {
                        return ItemRegistry.getRegistry().keysAsString();
                    }
                },
                
                TALENT {
                    @NotNull
                    @Override
                    public Optional<ItemCreator> get(@NotNull PlayerDatabase database, @NotNull Key key) {
                        return Optional.ofNullable(TalentRegistry.getRegistry().get(key).orElse(null));
                    }
                    
                    @NotNull
                    @Override
                    public List<String> listNames() {
                        return TalentRegistry.getRegistry().keysAsString();
                    }
                },
                
                ANOMALY {
                    @NotNull
                    @Override
                    public Optional<ItemCreator> get(@NotNull PlayerDatabase database, @NotNull Key key) {
                        final ElementalAnomalyType elementalAnomaly = Enums.byName(ElementalAnomalyType.class, key.toString());
                        
                        if (elementalAnomaly == null) {
                            return Optional.empty();
                        }
                        
                        return Optional.of(() -> {
                            return new ItemBuilder(Material.STONE)
                                    .setName(elementalAnomaly.getName())
                                    .addWrappedLore(elementalAnomaly.getDescription());
                        });
                    }
                    
                    @NotNull
                    @Override
                    public List<String> listNames() {
                        return StringList.ofEnumConstantLowercaseNames(ElementalAnomalyType.class);
                    }
                },
                
                ;
                
                @NotNull
                public Optional<ItemCreator> get(@NotNull PlayerDatabase database, @NotNull Key key) {
                    return Optional.empty();
                }
                
                @NotNull
                public List<String> listNames() {
                    return List.of();
                }
            }
            
            @Override
            public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
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
            
            @NotNull
            @Override
            public List<String> tabComplete(@NotNull ArgumentList args) {
                if (args.length == 1) {
                    return StringList.ofEnumConstantLowercaseNames(Registry.class);
                }
                else if (args.length == 2) {
                    final Registry registry = args.get(0).toEnum(Registry.class);
                    
                    return registry != null ? registry.listNames() : List.of();
                }
                
                return List.of();
            }
        };
        
        @NotNull
        @Override
        public List<String> tabComplete(@NotNull ArgumentList args) {
            return List.of();
        }
        
    }
    
}