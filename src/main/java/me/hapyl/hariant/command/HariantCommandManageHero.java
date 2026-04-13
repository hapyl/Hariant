package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.TypeConverter;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.inventory.item.artifact.ArtifactSlot;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifactInstance;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class HariantCommandManageHero extends HariantPlayerCommand {
    
    public HariantCommandManageHero(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    protected void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        // hero (hero) (create, has)
        // hero (hero) (artifact)    (slot) [uuid]
        final PlayerDatabase database = Hariant.getPlayerDatabase(player);
        
        final TypeConverter argument0 = args.get(0);
        final Key key = argument0.toKey();
        
        if (key == null) {
            HariantLogger.error(player, Component.text("Malformed key: %s".formatted(argument0.toString())));
            return;
        }
        
        final Hero hero = HeroRegistry.getRegistry().get(key).orElse(null);
        
        if (hero == null) {
            HariantLogger.error(player, Component.text("Hero `%s` doesn't exist!".formatted(key)));
            return;
        }
        
        enum Operation {
            CREATE {
                @Override
                public void perform(@NotNull Player player, @NotNull Hero hero, @NotNull PlayerDatabase database, @NotNull ArgumentList args) {
                    if (database.hero.isOwned(hero)) {
                        HariantLogger.error(player, Component.text("You already own this hero!"));
                        return;
                    }
                    
                    final HeroInstance heroInstance = database.hero.createHero(hero);
                    
                    HariantLogger.success(
                            player,
                            Component.empty()
                                     .append(Component.text("Successfully created `"))
                                     .append(hero.getName())
                                     .append(Component.text("` for you!"))
                                     .hoverEvent(heroInstance.createHoverEvent())
                    );
                }
            },
            
            HAS {
                @Override
                public void perform(@NotNull Player player, @NotNull Hero hero, @NotNull PlayerDatabase database, @NotNull ArgumentList args) {
                    if (database.hero.isOwned(hero)) {
                        HariantLogger.success(player, Component.empty().append(Component.text("You own ")).append(hero.getName()).append(Component.text('!')));
                    }
                    else {
                        HariantLogger.error(player, Component.empty().append(Component.text("You do not own ").append(hero.getName()).append(Component.text("!"))));
                    }
                }
            },
            
            ARTIFACT {
                @Override
                public void perform(@NotNull Player player, @NotNull Hero hero, @NotNull PlayerDatabase database, @NotNull ArgumentList args) {
                    final TypeConverter argument0 = args.get(0);
                    final ArtifactSlot slot = argument0.toEnum(ArtifactSlot.class);
                    
                    final HeroInstance heroInstance = database.hero.getHero(hero).orElse(null);
                    
                    if (heroInstance == null) {
                        HariantLogger.error(player, Component.text("You do not own this hero!"));
                        return;
                    }
                    
                    if (slot == null) {
                        HariantLogger.error(player, Component.text("Invalid slot: %s".formatted(argument0.toString())));
                        return;
                    }
                    
                    final TypeConverter argument1 = args.get(1);
                    final UUID uuid = argument1.toUuid();
                    
                    // Null uuid means we're checking artifact
                    if (uuid == null) {
                        final ItemArtifactInstance equippedArtifact = heroInstance.getArtifact(slot).orElse(null);
                        
                        if (equippedArtifact == null) {
                            HariantLogger.error(player, Component.text("Nothing is equipped at %s!".formatted(slot)));
                        }
                        else {
                            HariantLogger.success(
                                    player,
                                    Component.empty()
                                             .append(equippedArtifact.getOrigin().getName())
                                             .append(Component.text(" is equipped at %s!".formatted(slot)))
                            );
                        }
                        
                        return;
                    }
                    
                    final ItemArtifactInstance artifactInstance = database.inventory.getItemByUuid(uuid, ItemArtifactInstance.class).orElse(null);
                    
                    if (artifactInstance == null) {
                        HariantLogger.error(player, Component.text("Artifact `%s` doesn't exist!".formatted(uuid.toString())));
                        return;
                    }
                    
                    heroInstance.setArtifact(slot, artifactInstance);
                    HariantLogger.success(
                            player,
                            Component.empty()
                                     .append(Component.text("Equipped "))
                                     .append(artifactInstance.getOrigin().getName())
                                     .append(Component.text(" at slot %s!".formatted(slot)))
                    );
                }
            };
            
            public void perform(@NotNull Player player, @NotNull Hero hero, @NotNull PlayerDatabase database, @NotNull ArgumentList args) {
            }
        }
        
        final Operation operation = args.get(1).toEnum(Operation.class);
        
        if (operation == null) {
            HariantLogger.error(player, Component.text(
                    "Invalid operation, valid operations: %s".formatted(Arrays.stream(Operation.values()).map(e -> e.name().toLowerCase()).collect(Collectors.joining(", ")))
            ));
            return;
        }
        
        operation.perform(player, hero, database, ArgumentList.copyOfRange(args, 2, args.length));
    }
    
}
