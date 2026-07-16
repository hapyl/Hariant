package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.command.CommandProcessor;
import me.hapyl.eterna.module.command.SimpleCommand;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.TypeConverter;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.HariantPlugin;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstance;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.element.ElementSource;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.element.anomaly.EnumAnomaly;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.cooldown.CooldownHandlerImpl;
import me.hapyl.hariant.entity.damage.AssistSource;
import me.hapyl.hariant.entity.damage.tracker.CombatData;
import me.hapyl.hariant.entity.effect.Effect;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.entity.mutator.Decay;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.entity.shield.Shield;
import me.hapyl.hariant.entity.shield.ShieldStrength;
import me.hapyl.hariant.entity.type.HariantEntityDummy;
import me.hapyl.hariant.game.battleground.EnumBattleground;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.hero.shark.BloodScent;
import me.hapyl.hariant.inventory.drop.DropSummary;
import me.hapyl.hariant.inventory.drop.DropTable;
import me.hapyl.hariant.menu.hero.MenuHeroUnlock;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.task.HariantTickingStepTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.team.TeamData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class HariantCommandRegistry {
    
    private final CommandProcessor commandProcessor;
    
    public HariantCommandRegistry(@NotNull HariantPlugin plugin) {
        commandProcessor = new CommandProcessor(plugin);
        
        register("rank", HariantCommandRank::new);
        register("item", HariantCommandItem::new);
        register("spawn_entity", HariantCommandSpawnEntity::new);
        register("battleground", HariantCommandBattleground::new);
        register("hero", HariantCommandHero::new);
        register("temper", HariantCommandTemper::new);
        register("debug", HariantCommandDebug::new);
        register("create", HariantCommandCreate::new);
        register("team", HariantCommandTeam::new);
        register("sound", HariantCommandSound::new);
        register("achievement", HariantCommandAchievement::new);
        register("status_effect", HariantCommandStatusEffect::new);
        register("color", HariantCommandColor::new);
        register("trim", HariantCommandTrim::new);
        register("dump_item", HariantCommandDumpItem::new);
        
        register("show_attributes", context -> {
            final HariantPlayer player = context.getHariantPlayer();
            final AttributesInstance attributes = player.getAttributes();
            
            for (AttributeType attributeType : AttributeType.values()) {
                player.sendMessage(
                        Component.empty()
                                 .append(attributeType.asComponent())
                                 .appendSpace()
                                 .append(attributeType.format(attributes.get(attributeType)))
                );
            }
        });
        
        register("setTeamData", context -> {
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
            
            final HariantPlayer player = context.getHariantPlayer();
            final Type type = context.get(0).toEnum(Type.class);
            final int value = context.get(1).toInt();
            
            if (type == null || value < 0) {
                return;
            }
            
            final EnumTeam team = player.getPlayerTeam();
            
            Hariant.getCurrentGameInstance().ifPresent(gameInstance -> {
                type.set(gameInstance.getTeamData().getData(team), value);
                
                HariantLogger.success(player, Component.text("Set your team %s to %s!".formatted(type, value)));
            });
        });
        
        register("triggerElementalAnomaly", context -> {
            final HariantPlayer player = context.getHariantPlayer();
            final TypeConverter arg0 = context.get(0);
            final EnumAnomaly elementalAnomaly = arg0.toEnum(EnumAnomaly.class);
            
            final String argument = context.get(1).toString();
            
            if (!argument.isEmpty() && !argument.startsWith("-")) {
                player.messageError(Component.text("Argument must start with `-`!"));
                return;
            }
            
            if (elementalAnomaly == null) {
                player.messageError(Component.text("Unknown anomaly `%s`!".formatted(arg0)));
                return;
            }
            
            // Process arguments
            final HariantEntity source = argument.equals("-s") ? null : player;
            
            // Trigger anomaly
            player.triggerAnomaly(elementalAnomaly, source);
            
            HariantLogger.success(
                    player,
                    Component.empty()
                             .append(Component.text("Triggered "))
                             .append(elementalAnomaly.getName())
                             .append(Component.text(" anomaly!"))
            );
        });
        
        register("dummy", context -> {
            final Player player = context.getPlayer();
            final Location location = player.getLocation();
            location.setPitch(0.0f);
            
            Hariant.createEntity(() -> new HariantEntityDummy(location));
            
            HariantLogger.success(player, Component.text("Spawned a training dummy!"));
        });
        
        register("iWantToIgnoreAllCooldownsForDebugReasonsAndByAllIMeanAllThisWillEvenIgnoreDamageCooldowns", context -> {
            CooldownHandlerImpl.debugNoCooldowns = !CooldownHandlerImpl.debugNoCooldowns;
            
            HariantLogger.system(context.getPlayer(), CooldownHandlerImpl.debugNoCooldowns ? Component.text("Now ignoring cooldowns.") : Component.text("No longer ignoring cooldowns."));
        });
        
        register("player_head", context -> {
            final Player player = context.getPlayer();
            final ItemBuilder builder = ItemBuilder.playerHead(context.get(0).toString());
            
            player.getInventory().addItem(builder.asIcon());
        });
        
        register("interrupt", context -> {
            final HariantPlayer player = context.getHariantPlayer();
            
            player.interrupt(AssistSource.create(player, Component.text("Command")));
            player.messageSuccess(Component.text("Interrupted current action!"));
        });
        
        register("respawn", context -> {
            final HariantPlayer player = context.getHariantPlayer();
            
            if (!player.isDead()) {
                player.messageError(Component.text("You must be dead to respawn!"));
                return;
            }
            
            player.respawn(5);
        });
        
        register("add_elemental_units", context -> {
            final HariantPlayer player = context.getHariantPlayer();
            final ElementType elementType = context.get(0).toEnum(ElementType.class);
            final double units = context.get(1).toDouble();
            
            if (elementType == null) {
                player.messageError(Component.text("Unknown element!"));
                return;
            }
            
            if (units <= 0) {
                player.messageError(Component.text("Cannot add zero or negative units!"));
            }
            
            player.applyElement(ElementSource.create(elementType, null, units));
            player.messageSuccess(Component.text("Added %s %s elemental units to you!".formatted(units, elementType.name())));
        });
        
        register("apply_decay", context -> {
            final HariantPlayer player = context.getHariantPlayer();
            final double percentage = context.get(0).toDouble(25);
            final int duration = context.get(1).toInt(60);
            
            if (percentage < 0 || percentage > 100) {
                player.messageError(Component.text("Decay value must be a percentage between 0 and 100!"));
                return;
            }
            
            if (duration < 0) {
                player.messageError(Component.text("Decay duration cannot be negative!"));
                return;
            }
            
            player.addHealthMutator(Decay.create(percentage / 100 * player.getMaxHealth(), duration));
            player.messageSuccess(Component.text("Applied decay worth %s%% of max health for %s.".formatted(percentage, Tick.format(duration))));
        });
        
        register("draw_bounding_box_outlines", context -> {
            EntityCollector.BoundingBoxRenderer.DEBUG_DRAW_BOUNDING_BOX_OUTLINES = !EntityCollector.BoundingBoxRenderer.DEBUG_DRAW_BOUNDING_BOX_OUTLINES;
            
            HariantLogger.system(
                    context.getPlayer(),
                    EntityCollector.BoundingBoxRenderer.DEBUG_DRAW_BOUNDING_BOX_OUTLINES
                    ? Component.text("The bounding box outlines will now be drawn.")
                    : Component.text("The bounding box outlines will no longer be drawn.")
            );
        });
        
        register("shield", context -> {
            final HariantPlayer player = context.getHariantPlayer();
            
            final double amount = context.get(0).toDouble(200);
            final double strength = context.get(1).toDouble(1.0);
            final int duration = context.get(2).toInt(200);
            
            player.setShield(new Shield(player, player, ShieldStrength.strength(strength), amount, duration));
            player.messageSuccess(Component.text("Applied shield with capacity %s and strength %s for %s!".formatted(amount, strength, Tick.format(duration))));
        });
        
        register("open_hero_unlock_menu", context -> {
            context.get(0).toRegistryItem(HeroRegistry.getRegistry()).ifPresent(hero -> {
                new MenuHeroUnlock(context.getPlayer(), hero);
            });
        });
        
        register("start_game_countdown", context -> {
            Hariant.startCountdown();
        });
        
        register("los", context -> {
            final Player player = context.getPlayer();
            final Block targetBlock = player.getTargetBlockExact(20);
            
            if (targetBlock == null) {
                HariantLogger.error(player, Component.text("No target block!"));
                return;
            }
            
            final Location location = targetBlock.getLocation();
            final double x = location.getX();
            final double y = location.getY();
            final double z = location.getZ();
            
            final String locationString = "%.1f, %.1f, %.1f".formatted(x, y, z);
            
            HariantLogger.success(player, Component.text("Target block information:"));
            HariantLogger.info(player, Component.text(" Type: ", Colors.GRAY).append(Component.translatable(targetBlock.translationKey(), Colors.WHITE)));
            HariantLogger.info(player,
                               Component.empty()
                                        .append(Component.text(" Position: ", Colors.GRAY))
                                        .append(Component.text(locationString, Colors.WHITE))
                                        .append(Component.text(" ᴄᴏᴘʏ", Colors.GOLD, TextDecoration.BOLD))
                                        .hoverEvent(HoverEvent.showText(Component.text("Click to copy!")))
                                        .clickEvent(ClickEvent.suggestCommand(locationString))
            );
        });
        
        register("trigger_drop_table", context -> {
            final Player player = context.getPlayer();
            final EnumBattleground battleground = context.get(0).toEnum(EnumBattleground.class);
            final int times = context.get(1).toInt();
            
            if (battleground == null) {
                HariantLogger.error(player, Component.text("Invalid battleground!"));
                return;
            }
            
            if (times <= 0) {
                HariantLogger.error(player, Component.text("Trigger times cannot be negative or zero."));
                return;
            }
            
            final DropTable dropTable = battleground.getDropTable();
            final DropSummary dropSummary = DropSummary.create();
            final PlayerProfile profile = Hariant.getPlayerProfile(player);
            
            HariantLogger.info(player, Component.text("Rolling drop tables %,d times!".formatted(times)));
            
            for (int i = 0; i < times; i++) {
                dropSummary.append(dropTable.generateLoot(profile));
            }
            
            dropSummary.showSummary(player);
        });
        
        register("trigger_effect", context -> {
            final HariantPlayer player = context.getHariantPlayer();
            final EffectType effectType = context.get(0).toEnum(EffectType.class);
            
            if (effectType == null) {
                player.messageError(Component.text(
                        "Invalid effect type, must be one of the following: " + Arrays.stream(EffectType.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.joining(", "))
                ));
                return;
            }
            
            player.triggerEffect(player, Effect.create(Key.ofString("dummy_effect"), Component.text("Command"), effectType));
            player.messageSuccess(Component.text("Triggered %s effect!".formatted(effectType)));
        });
        
        register("create_blood_scent", context -> {
            class Holder {
                static BloodScent bloodScent;
            }
            
            final HariantPlayer player = context.getHariantPlayer();
            
            if (Holder.bloodScent != null) {
                player.setGameMode(GameMode.SPECTATOR);
                
                if (context.get(0).toString().equals("follow")) {
                    final Queue<? extends Location> path = Holder.bloodScent.computePath();
                    player.sendMessage(Component.text("Following the trail."));
                    
                    new HariantTickingStepTask(Scheduler.ofTimer(), 1) {
                        @Override
                        public boolean run(int tick, int step) {
                            final Location location = path.poll();
                            final Location next = path.peek();
                            
                            if (location == null) {
                                player.setGameMode(GameMode.SURVIVAL);
                                player.sendMessage(Component.text("Finished following the trail."));
                                return true;
                            }
                            
                            // If next exists, merge yaw and pitch
                            
                            if (next != null) {
                                final Vector direction = next.toVector().subtract(location.toVector()).normalize();
                                final double PI_2 = Math.PI * 2;
                                
                                final double x = direction.getX();
                                final double z = direction.getZ();
                                
                                final float yaw = (float) Math.toDegrees((Math.atan2(-x, z) + PI_2) % PI_2);
                                final float pitch = (float) Math.toDegrees(Math.atan2(-direction.getY(), Math.sqrt(x * x + z * z))) * 0.1f;
                                
                                location.setYaw(yaw);
                                location.setPitch(pitch);
                                
                                player.teleport(location);
                            }
                            
                            return false;
                        }
                    };
                    
                    return;
                }
                
                Holder.bloodScent.cancel();
                Holder.bloodScent = null;
                
                player.messageSuccess(Component.text("Removed blood scent."));
                return;
            }
            
            Holder.bloodScent = new BloodScent(player);
            player.messageSuccess(Component.text("Created blood scent."));
        });
        
        register("show_damage_feedback", context -> {
            final HariantPlayer player = context.getHariantPlayer();
            
            player.sendMessage(Component.text("Total DMG Dealt [HOVER]").hoverEvent(player.getCombatTracker().createHoverEvent(CombatData.Type.OUTGOING)));
            player.sendMessage(Component.text("Total DMG Taken [HOVER]").hoverEvent(player.getCombatTracker().createHoverEvent(CombatData.Type.INCOMING)));
        });
    }
    
    public void register(@NotNull String command, @NotNull Consumer<CommandContext> context) {
        commandProcessor.registerCommand(new HariantPlayerCommand(command, PlayerRank.ADMIN) {
            @Override
            public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
                context.accept(new CommandContextImpl(player, args, playerRank));
            }
        });
    }
    
    public void register(@NotNull String command, @NotNull Function<String, SimpleCommand> function) {
        commandProcessor.registerCommand(function.apply(command));
    }
    
}
