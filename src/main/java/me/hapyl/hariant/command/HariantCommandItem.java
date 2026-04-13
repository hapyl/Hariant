package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.TypeConverter;
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

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class HariantCommandItem extends HariantCommand {
    
    public HariantCommandItem(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    protected void execute(@NotNull CommandSender sender, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        // item (player) (resource) (get, add, subtract) (key) [value]
        // item (player) (item)     (key)
        final Player target = args.get(0).toPlayer();
        
        if (target == null) {
            HariantLogger.error(sender, Component.text("This player is not online!"));
            return;
        }
        
        final PlayerDatabase database = Hariant.getPlayerDatabase(target);
        final TypeConverter argument1 = args.get(1);
        
        switch (argument1.toString().toLowerCase()) {
            case "resource" -> {
                enum Operation {
                    GET {
                        @Override
                        public void perform(@NotNull CommandSender sender, @NotNull Player target, @NotNull HariantInventory inventory, @NotNull Resource resource, int amount) {
                            final int resourceAmount = inventory.getResource(resource);
                            final String targetName = target.getName();
                            final String resourceName = Components.toString(resource.getName());
                            
                            if (resourceAmount == 0) {
                                HariantLogger.info(sender, Component.text("%s doesn't have any `%s`!".formatted(targetName, resourceName)));
                            }
                            else {
                                HariantLogger.info(sender, Component.text("%s's has %,d of `%s`!".formatted(targetName, resourceAmount, resourceName)));
                            }
                        }
                    },
                    
                    ADD {
                        @Override
                        public void perform(@NotNull CommandSender sender, @NotNull Player target, @NotNull HariantInventory inventory, @NotNull Resource resource, int amount) {
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
                        public void perform(@NotNull CommandSender sender, @NotNull Player target, @NotNull HariantInventory inventory, @NotNull Resource resource, int amount) {
                            ADD.perform(sender, target, inventory, resource, -amount);
                        }
                    };
                    
                    public void perform(@NotNull CommandSender sender, @NotNull Player target, @NotNull HariantInventory inventory, @NotNull Resource resource, int amount) {
                    }
                    
                }
                
                final Operation operation = args.get(2).toEnum(Operation.class);
                
                if (operation == null) {
                    HariantLogger.error(sender, Component.text(
                            "Invalid argument, valid arguments: %s.".formatted(Arrays.stream(Operation.values()).map(e -> e.name().toLowerCase()).collect(Collectors.joining(", ")))
                    ));
                    return;
                }
                
                final TypeConverter argument3 = args.get(3);
                final Key key = argument3.toKey();
                
                if (key == null) {
                    HariantLogger.error(sender, Component.text("Key `%s` is malformed!".formatted(argument3.toString())));
                    return;
                }
                
                final Resource resource = ResourceRegistry.getRegistry().get(key).orElse(null);
                
                if (resource == null) {
                    HariantLogger.error(sender, Component.text("Resource `%s` doesn't exist!".formatted(argument3.toString())));
                    return;
                }
                
                operation.perform(sender, target, database.inventory, resource, args.get(4).toInt());
            }
            
            case "item" -> {
                final TypeConverter argument2 = args.get(2);
                final Key key = argument2.toKey();
                
                if (key == null) {
                    HariantLogger.error(sender, Component.text("Key `%s` is malformed!".formatted(argument2.toString())));
                    return;
                }
                
                final Item item = ItemRegistry.getRegistry().get(key).orElse(null);
                
                if (item == null) {
                    HariantLogger.error(sender, Component.text("Item `%s` doesn't exist!".formatted(argument2.toString())));
                    return;
                }
                
                final ItemInstance newInstance = item.newInstance(database, UUID.randomUUID());
                
                database.inventory.createItem(newInstance);
                
                HariantLogger.success(
                        sender,
                        Component.empty()
                                 .append(Component.text("Created "))
                                 .append(item.getName())
                                 .append(Component.text(" for %s! ".formatted(target.getName())))
                                 .hoverEvent(newInstance.createHoverEvent())
                );
            }
            
            default -> HariantLogger.error(sender, Component.text("Invalid argument, must be either `resource` or `item`!"));
        }
    }
    
}
