package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.StringList;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.inventory.HariantInventory;
import me.hapyl.hariant.inventory.item.*;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class HariantCommandItem extends HariantPlayerCommand {
    
    public HariantCommandItem(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        // item (player) (resource) (key) (get, add, subtract) [value]
        // item (player) (item)     (key)
        final Player target = args.get(0).toPlayer();
        
        if (target == null) {
            HariantLogger.error(player, Component.text("This player is not online!"));
            return;
        }
        
        final PlayerDatabase database = Hariant.getPlayerDatabase(target);
        final Operation operation = args.get(1).toEnum(Operation.class);
        
        if (operation == null) {
            HariantLogger.error(player, Component.text("Invalid operation!"));
            return;
        }
        
        final Key key = args.get(2).toKey();
        
        if (key == null) {
            HariantLogger.error(player, Component.text("Malformed key: %s!".formatted(args.get(2))));
            return;
        }
        
        operation.execute(player, target, database, key, ArgumentList.copyOfRange(args, 3, args.length));
    }
    
    @Override
    public @NotNull List<String> tabComplete(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        if (args.length == 1) {
            return StringList.ofOnlinePlayers();
        }
        else if (args.length == 2) {
            return StringList.ofEnumConstantLowercaseNames(Operation.class);
        }
        else if (args.length >= 3) {
            final Operation operation = args.get(1).toEnum(Operation.class);
            
            return operation != null ? operation.tabComplete(ArgumentList.copyOfRange(args, 3, args.length)) : List.of();
        }
        
        return List.of();
    }
    
    public enum Operation implements TabCompleter {
        RESOURCE {
            private static final int AMOUNT_LIMIT = 1_000_000;
            
            public enum SubOperation {
                GET {
                    @Override
                    public void execute(@NotNull CommandSender sender, @NotNull Player target, @NotNull HariantInventory inventory, @NotNull Resource resource, int amount) {
                        final int resourceAmount = inventory.getResource(resource);
                        final String targetName = target.getName();
                        final String resourceName = Components.toString(resource.getName());
                        
                        if (resourceAmount == 0) {
                            HariantLogger.success(sender, Component.text("%s doesn't have any `%s`!".formatted(targetName, resourceName)));
                        }
                        else {
                            HariantLogger.success(sender, Component.text("%s's has %,d of `%s`!".formatted(targetName, resourceAmount, resourceName)));
                        }
                    }
                },
                
                ADD {
                    @Override
                    public void execute(@NotNull CommandSender sender, @NotNull Player target, @NotNull HariantInventory inventory, @NotNull Resource resource, int amount) {
                        inventory.addResource(resource, amount);
                        
                        HariantLogger.success(sender, Component.text("%s %s's `%s` by `%s`, new value `%s`.".formatted(
                                amount > 0 ? "Added" : "Subtracted",
                                target.getName(),
                                Components.toString(resource.getName()),
                                amount,
                                inventory.getResource(resource)
                        )));
                    }
                },
                
                SUBTRACT {
                    @Override
                    public void execute(@NotNull CommandSender sender, @NotNull Player target, @NotNull HariantInventory inventory, @NotNull Resource resource, int amount) {
                        ADD.execute(sender, target, inventory, resource, -amount);
                    }
                };
                
                public void execute(@NotNull CommandSender sender, @NotNull Player target, @NotNull HariantInventory inventory, @NotNull Resource resource, int amount) {
                    throw new IllegalStateException();
                }
            }
            
            @Override
            public void execute(@NotNull Player player, @NotNull Player target, @NotNull PlayerDatabase database, Key key, @NotNull ArgumentList args) {
                final SubOperation subOperation = args.get(0).toEnum(SubOperation.class);
                
                if (subOperation == null) {
                    HariantLogger.error(player, Component.text("Invalid sub-operation!"));
                    return;
                }
                
                final Resource resource = ResourceRegistry.getRegistry().get(key).orElse(null);
                
                if (resource == null) {
                    HariantLogger.error(player, Component.text("Resource `%s` doesn't exist!".formatted(args.get(1))));
                    return;
                }
                
                final int amount = args.get(1).toInt();
                
                if (subOperation != SubOperation.GET && amount < 1 || amount > AMOUNT_LIMIT) {
                    HariantLogger.error(player, Component.text("Amount cannot be negative nor higher than %s!".formatted(AMOUNT_LIMIT)));
                    return;
                }
                
                subOperation.execute(player, target, database.inventory, resource, amount);
            }
            
            @NotNull
            @Override
            public List<String> tabComplete(@NotNull ArgumentList args) {
                // Since key is parsed in Operation, we check for length 0
                if (args.length == 0) {
                    return StringList.ofRegistryKeys(ResourceRegistry.getRegistry());
                }
                else if (args.length == 1) {
                    return StringList.ofEnumConstantLowercaseNames(SubOperation.class);
                }
                
                return List.of();
            }
            
        },
        
        ITEM {
            @Override
            public void execute(@NotNull Player player, @NotNull Player target, @NotNull PlayerDatabase database, Key key, @NotNull ArgumentList args) {
                final Item item = ItemRegistry.getRegistry().get(key).orElse(null);
                
                if (item == null) {
                    HariantLogger.error(player, Component.text("Item `%s` doesn't exist!".formatted(key)));
                    return;
                }
                
                final ItemInstance itemInstance = database.inventory.createItem(item);
                
                HariantLogger.success(
                        player,
                        Component.empty()
                                 .append(Component.text("Created "))
                                 .append(item.getName())
                                 .append(Component.text(" for %s! ".formatted(target.getName())))
                                 .hoverEvent(itemInstance.createHoverEvent())
                );
            }
            
            @NotNull
            @Override
            public List<String> tabComplete(@NotNull ArgumentList args) {
                if (args.length == 0) {
                    return StringList.ofRegistryKeys(ItemRegistry.getRegistry());
                }
                
                return List.of();
            }
            
        };
        
        public void execute(@NotNull Player player, @NotNull Player target, @NotNull PlayerDatabase database, Key key, @NotNull ArgumentList args) {
            throw new IllegalStateException();
        }
        
        @NotNull
        @Override
        public List<String> tabComplete(@NotNull ArgumentList args) {
            return List.of();
        }
    }
    
}
