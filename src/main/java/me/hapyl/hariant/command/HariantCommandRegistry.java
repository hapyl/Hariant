package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.command.CommandProcessor;
import me.hapyl.eterna.module.command.SimpleCommand;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.math.Vector3;
import me.hapyl.eterna.module.math.geometry.Drawable;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.TypeConverter;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.HariantPlugin;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstance;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.element.anomaly.EnumAnomaly;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.cooldown.CooldownHandlerImpl;
import me.hapyl.hariant.entity.heal.HealingSource;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.entity.type.HariantEntityDummy;
import me.hapyl.hariant.game.WinResult;
import me.hapyl.hariant.game.WinType;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.math.Shape;
import me.hapyl.hariant.math.ShapeProperties;
import me.hapyl.hariant.math.Shapes;
import me.hapyl.hariant.menu.hero.MenuHeroArtifactEquip;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.team.TeamData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class HariantCommandRegistry {
    
    private final CommandProcessor commandProcessor;
    
    public HariantCommandRegistry(@NotNull HariantPlugin plugin) {
        commandProcessor = new CommandProcessor(plugin);
        
        register("rank", HariantCommandRank::new);
        register("item", HariantCommandItem::new);
        register("manage_hero", HariantCommandManageHero::new);
        register("create_item_preview", HariantCommandCreateItemPreview::new);
        register("spawn_entity", HariantCommandSpawnEntity::new);
        register("battleground", HariantCommandBattleground::new);
        register("hero", HariantCommandHero::new);
        register("ping", HariantCommandPing::new);
        register("temper", HariantTemperCommand::new);
        
        registerTestCommand("create_or_delete_player", (player, args) -> {
            final HariantPlayer existingPlayer = Hariant.getPlayer(player).orElse(null);
            
            if (existingPlayer == null) {
                Hariant.createPlayer(player, Hariant.getPlayerDatabase(player).hero.getSelectedHeroInstance());
                HariantLogger.success(player, Component.text("Successfully created player instance!"));
            }
            else {
                Hariant.destroyEntity(player.getUniqueId());
                HariantLogger.success(player, Component.text("Successfully deleted player instance!"));
            }
        });
        
        registerTestCommand("show_attributes", (player, args) -> {
            Hariant.getPlayer(player).ifPresentOrElse(_player -> {
                final AttributesInstance attributes = _player.getAttributes();
                
                for (AttributeType attributeType : AttributeType.values()) {
                    player.sendMessage(
                            Component.empty()
                                     .append(attributeType.asComponent())
                                     .appendSpace()
                                     .append(attributeType.format(attributes.get(attributeType)))
                    );
                }
            }, () -> {
                HariantLogger.error(player, Component.text("Not in a game."));
            });
        });
        
        registerTestCommand("view_artifact_equip_menu", (player, args) -> {
            new MenuHeroArtifactEquip(player, Hariant.getPlayerDatabase(player).hero.getSelectedHeroInstance());
        });
        
        registerTestCommand("draw_shape", (player, args) -> {
            final String shapeName = args.get(0).toString().toLowerCase();
            final double rotationAngle = args.get(1).toDouble();
            
            final Shape shape = switch (shapeName) {
                case "octahedron" -> Shapes.OCTAHEDRON;
                case "icosahedron" -> Shapes.ICOSAHEDRON;
                default -> null;
            };
            
            if (shape == null) {
                HariantLogger.error(player, Component.text("Unknown shape!"));
                return;
            }
            
            final ShapeProperties shapeProperties = ShapeProperties.create(10, 0.1, Vector3.zero());
            
            shape.draw(player.getLocation(), Drawable.worldParticle(Particle.FLAME), shapeProperties);
            
            HariantLogger.success(player, Component.text("Drawn %s shape with %.1f rotation.".formatted(shapeName, rotationAngle)));
        });
        
        registerTestCommand("charge_ultimate", (player, args) -> {
            Hariant.getPlayer(player).ifPresent(HariantPlayer::chargeUltimate);
        });
        
        registerTestCommand("create_or_delete_game_instance", (player, args) -> {
            if (Hariant.isGameInProgress()) {
                Hariant.endCurrentGameInstance(WinResult.create(WinType.WIN_CONDITION_MET, List.of()));
                HariantLogger.success(player, Component.text("Ended current game instance!"));
            }
            else {
                Hariant.startNewGameInstance();
                HariantLogger.success(player, Component.text("Created new game instance!"));
            }
        });
        
        registerTestCommand("jointeam", (player, args) -> {
            final EnumTeam newTeam = args.get(0).toEnum(EnumTeam.class);
            final PlayerProfile profile = Hariant.getPlayerProfile(player);
            
            if (newTeam == null) {
                HariantLogger.error(player, Component.text("Unknown team!"));
                return;
            }
            
            newTeam.addPlayer(profile);
        });
        
        registerTestCommand("testherohead", (player, args) -> {
            final Hero hero = args.get(0).toStaticConstant(HeroRegistry.class, Hero.class).orElse(null);
            
            if (hero == null) {
                HariantLogger.error(player, Component.text("Даун."));
                return;
            }
            
            player.sendMessage(Component.text("Head: ").append(hero.asHeadComponent()));
        });
        
        registerTestCommand("togglespectator", (player, args) -> {
            final PlayerProfile profile = Hariant.getPlayerProfile(player);
            
            profile.setSpectator(!profile.isSpectator());
            
            HariantLogger.success(
                    player,
                    profile.isSpectator()
                    ? Component.text("You are now spectating.")
                    : Component.text("You are no longer spectating.")
            );
        });
        
        registerTestCommand("cooldown", (player, args) -> {
            Hariant.getPlayer(player).ifPresent(hariantPlayer -> {
                hariantPlayer.resetCooldowns();
                hariantPlayer.resetUltimate();
                hariantPlayer.chargeUltimate();
                
                HariantLogger.success(player, Component.text("Reset cooldowns and charged ultimate!"));
            });
        });
        
        registerTestCommand("heal", (player, args) -> {
            Hariant.getPlayer(player).ifPresent(hariantPlayer -> {
                final double healing = args.get(0).toDouble(1);
                
                hariantPlayer.heal(HealingSource.create(healing, hariantPlayer));
                hariantPlayer.sendMessage(Component.text("Healed for %.0f!".formatted(healing), NamedTextColor.GREEN));
            });
        });
        
        registerTestCommand("setteamdata", (player, args) -> {
            enum Type {
                KILLS {
                    @Override
                    public void set(@NotNull TeamData data, int value) {
                        data.kills = value;
                    }
                },
                DEATHS {
                    @Override
                    public void set(@NotNull TeamData data, int value) {
                        data.deaths = value;
                    }
                };
                
                public void set(@NotNull TeamData data, int value) {
                }
            }
            
            final Type type = args.get(0).toEnum(Type.class);
            final int value = args.get(1).toInt();
            
            if (type == null || value < 0) {
                return;
            }
            
            final PlayerProfile profile = Hariant.getPlayerProfile(player);
            final EnumTeam team = profile.getTeam();
            
            Hariant.getCurrentGameInstance().ifPresent(gameInstance -> {
                type.set(gameInstance.getTeamData().getData(team), value);
                
                HariantLogger.success(profile, Component.text("Set your team %s to %s!".formatted(type, value)));
            });
        });
        
        registerTestCommand("triggerElementalAnomaly", (player, args) -> {
            Hariant.getPlayer(player).ifPresent(hariantPlayer -> {
                final TypeConverter arg0 = args.get(0);
                final EnumAnomaly elementalAnomaly = arg0.toEnum(EnumAnomaly.class);
                
                final String argument = args.get(1).toString();
                
                if (!argument.isEmpty() && !argument.startsWith("-")) {
                    HariantLogger.error(hariantPlayer, Component.text("Argument must start with `-`!"));
                    return;
                }
                
                if (elementalAnomaly == null) {
                    HariantLogger.error(hariantPlayer, Component.text("Unknown anomaly `%s`!".formatted(arg0)));
                    return;
                }
                
                // Process arguments
                final HariantEntity source = argument.equals("-s") ? null : hariantPlayer;
                
                // Trigger anomaly
                hariantPlayer.triggerAnomaly(elementalAnomaly, source);
                
                HariantLogger.success(
                        hariantPlayer,
                        Component.empty()
                                 .append(Component.text("Triggered "))
                                 .append(elementalAnomaly.getName())
                                 .append(Component.text(" anomaly!"))
                );
            });
        });
        
        registerTestCommand("dummy", (player, args) -> {
            final Location location = player.getLocation();
            location.setPitch(0.0f);
            
            Hariant.createEntity(() -> new HariantEntityDummy(location));
            
            HariantLogger.success(player, Component.text("Spawned a training dummy!"));
        });
        
        registerTestCommand("iWantToIgnoreTalentCooldownsForDebugReasonsISwearIWontAbuseItInARealGame", (player, args) -> {
            CooldownHandlerImpl.debugNoCooldowns = !CooldownHandlerImpl.debugNoCooldowns;
            
            HariantLogger.system(player, CooldownHandlerImpl.debugNoCooldowns ? Component.text("Now ignoring cooldowns.") : Component.text("No longer ignoring cooldowns."));
        });
        
        registerTestCommand("player_head", (player, args) -> {
            final ItemBuilder builder = ItemBuilder.playerHead(args.get(0).toString());
            
            player.getInventory().addItem(builder.asIcon());
        });
    }
    
    public void registerTestCommand(@NotNull String command, @NotNull BiConsumer<Player, ArgumentList> consumer) {
        commandProcessor.registerCommand(new HariantCommand(command, PlayerRank.ADMIN) {
            @Override
            protected void execute(@NotNull CommandSender sender, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
                if (!(sender instanceof Player player)) {
                    HariantLogger.error(sender, Component.text("You must be a player to execute test commands!"));
                    return;
                }
                
                consumer.accept(player, args);
            }
        });
    }
    
    public void register(@NotNull String command, @NotNull Function<String, SimpleCommand> function) {
        commandProcessor.registerCommand(function.apply(command));
    }
    
}
