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
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
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
import me.hapyl.hariant.inventory.drop.DropSummary;
import me.hapyl.hariant.inventory.drop.DropTable;
import me.hapyl.hariant.menu.hero.MenuHeroUnlock;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.task.HariantTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.team.TeamData;
import me.hapyl.hariant.util.ComponentShine;
import me.hapyl.hariant.util.RiptideFx;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class HariantCommandRegistry {
    
    private final CommandProcessor commandProcessor;
    
    public HariantCommandRegistry(@NotNull HariantPlugin plugin) {
        commandProcessor = new CommandProcessor(plugin);
        
        register("rank", HariantCommandRank::new);
        register("item", HariantCommandItem::new);
        register("spawnEntity", HariantCommandSpawnEntity::new);
        register("battleground", HariantCommandBattleground::new);
        register("hero", HariantCommandHero::new);
        register("temper", HariantCommandTemper::new);
        register("debug", HariantCommandDebug::new);
        register("create", HariantCommandCreate::new);
        register("team", HariantCommandTeam::new);
        register("sound", HariantCommandSound::new);
        register("achievement", HariantCommandAchievement::new);
        register("statusEffect", HariantCommandStatusEffect::new);
        register("color", HariantCommandColor::new);
        register("trim", HariantCommandTrim::new);
        register("dumpItem", HariantCommandDumpItem::new);
        register("skipDialog", HariantCommandSkipDialog::new);
        register("ready", HariantCommandReady::new);
        
        register("showAttributes", context -> {
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
            final ElementalAnomalyType elementalAnomaly = arg0.toEnum(ElementalAnomalyType.class);
            
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
        
        register("playerHead", context -> {
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
        
        register("addElementalUnits", context -> {
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
        
        register("applyDecay", context -> {
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
        
        register("drawBoundingBoxOutlines", context -> {
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
        
        register("openHeroUnlockMenu", context -> {
            context.get(0).toRegistryItem(HeroRegistry.getRegistry()).ifPresent(hero -> {
                new MenuHeroUnlock(context.getPlayer(), hero);
            });
        });
        
        register("startGameCountdown", context -> {
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
        
        register("showDamageFeedback", context -> {
            final HariantPlayer player = context.getHariantPlayer();
            
            player.sendMessage(Component.text("Total DMG Dealt [HOVER]").hoverEvent(player.getCombatTracker().createHoverEvent(CombatData.Type.OUTGOING)));
            player.sendMessage(Component.text("Total DMG Taken [HOVER]").hoverEvent(player.getCombatTracker().createHoverEvent(CombatData.Type.INCOMING)));
        });
        
        register("googleanim", context -> {
            class FinalSlashAnimation extends BukkitRunnable {
                
                private final Player player;
                private final Location startLocation;
                private final Vector forward;
                private final Vector u; // Tilted horizontal axis for the slash plane
                private final Set<UUID> hitEntities = new HashSet<>();
                
                // Animation Settings
                private final int maxTicks = 8; // How long the slash travels (8 ticks = 0.4 seconds)
                private int tick = 0;
                
                // Physics & Visuals
                private final double speed = 1.2;          // Distance traveled forward per tick (blocks)
                private final double initialRadius = 1.2;  // Starting width of the slash arc
                private final double expansionRate = 0.25; // How much the slash widens as it travels
                private final double depth = 0.8;          // How sharp the crescent curve is
                private final double damage = 12.0;        // Damage dealt to targets hit
                
                public FinalSlashAnimation(Player player) {
                    this.player = player;
                    // Start slightly in front of the player, around chest level
                    this.startLocation = player.getEyeLocation().subtract(0, 0.3, 0);
                    this.forward = startLocation.getDirection().normalize();
                    
                    // 1. Calculate a "right" vector perpendicular to the forward direction
                    Vector right = new Vector(-forward.getZ(), 0, forward.getX());
                    if (right.lengthSquared() == 0) {
                        right = new Vector(1, 0, 0); // Fallback for looking straight up/down
                    }
                    else {
                        right.normalize();
                    }
                    
                    // 2. Calculate an "up" vector perpendicular to both forward and right
                    Vector up = right.clone().crossProduct(forward).normalize();
                    
                    // 3. Tilt the horizontal vector 'u' to create a diagonal slash (e.g., -35 degrees)
                    double tiltAngle = Math.toRadians(-35);
                    this.u = right.clone().multiply(Math.cos(tiltAngle))
                                  .add(up.clone().multiply(Math.sin(tiltAngle)))
                                  .normalize();
                    
                    // Play initial sweep sounds
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.5f, 0.5f);
                    player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_THROW, 1.0f, 0.6f);
                }
                
                @Override
                public void run() {
                    if (tick >= maxTicks) {
                        // Optional: Spawn a small impact burst when the slash dissipates
                        Location finalCenter = startLocation.clone().add(forward.clone().multiply(tick * speed));
                        finalCenter.getWorld().spawnParticle(Particle.EXPLOSION, finalCenter, 2, 0.2, 0.2, 0.2, 0.1);
                        cancel();
                        return;
                    }
                    
                    // Calculate current distance and radius for this tick
                    double distance = tick * speed;
                    double radius = initialRadius + (tick * expansionRate);
                    Location center = startLocation.clone().add(forward.clone().multiply(distance));
                    
                    // Define energy colors (Cyan and Navy blue for a magical/wind aesthetic)
                    Particle.DustOptions primaryColor = new Particle.DustOptions(Color.fromRGB(0, 220, 255), 1.2f);
                    Particle.DustOptions secondaryColor = new Particle.DustOptions(Color.fromRGB(0, 100, 255), 0.8f);
                    
                    // Arc bounds (from -65 degrees to +65 degrees)
                    double maxAngle = Math.toRadians(65);
                    double step = Math.toRadians(4); // Smaller step size = denser, smoother line
                    
                    for (double theta = -maxAngle; theta <= maxAngle; theta += step) {
                        // Math for the crescent geometry
                        double horizontalOffset = Math.sin(theta) * radius;
                        double forwardOffset = Math.cos(theta) * depth; // Center curves forward, edges trail back
                        
                        // Calculate the 3D offset relative to player's looking direction
                        Vector offset = u.clone().multiply(horizontalOffset)
                                         .add(forward.clone().multiply(forwardOffset));
                        
                        Location particleLoc = center.clone().add(offset);
                        
                        // Spawn the primary slash particle trail
                        particleLoc.getWorld().spawnParticle(Particle.DUST, particleLoc, 1, 0, 0, 0, 0, primaryColor);
                        
                        // Spawn secondary core particles every other tick for visual depth
                        if (tick % 2 == 0) {
                            particleLoc.getWorld().spawnParticle(Particle.DUST, particleLoc, 1, 0, 0, 0, 0, secondaryColor);
                        }
                        
                        // Spawn subtle wind trails at the very tips of the crescent
                        if (Math.abs(theta) > maxAngle - 0.1) {
                            particleLoc.getWorld().spawnParticle(Particle.CLOUD, particleLoc, 1, 0.02, 0.02, 0.02, 0.01);
                        }
                        
                        // Perform bounding-box hit detection along the slash path
                        for (Entity entity : particleLoc.getWorld().getNearbyEntities(particleLoc, 0.8, 0.8, 0.8)) {
                            if (entity instanceof LivingEntity target && entity != player && !hitEntities.contains(entity.getUniqueId())) {
                                hitEntities.add(target.getUniqueId());
                                
                                // Apply damage attributed to the player
                                target.damage(damage, player);
                                
                                // Hit feedback (sound, blood/crit particles, and directional knockback)
                                target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0, 1, 0), 10, 0.2, 0.2, 0.2, 0.3);
                                target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.2f, 1.1f);
                                
                                Vector knockback = forward.clone().multiply(0.4).setY(0.15);
                                target.setVelocity(target.getVelocity().add(knockback));
                            }
                        }
                    }
                    
                    tick++;
                }
            }
            
            new FinalSlashAnimation(context.getPlayer()).runTaskTimer(plugin, 0, 1);
        });
        
        register("googleanim1", context -> {
            class JudgmentCutExplosion extends BukkitRunnable {
                
                private final Player caster;
                private final Location portalLoc;
                private final int maxTicks = 10; // Rapid flurry over 0.5 seconds (10 ticks)
                private int tick = 0;
                
                private final double domeRadius = 3.5;
                private final double damage = 14.0;
                
                public JudgmentCutExplosion(Player caster, Location portalLoc) {
                    this.caster = caster;
                    this.portalLoc = portalLoc.clone();
                    this.portalLoc.setY(portalLoc.getBlockY()); // Clamps to the ground portal level
                    
                    // Sound of rapid air-slicing
                    portalLoc.getWorld().playSound(portalLoc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.5f, 1.2f);
                    portalLoc.getWorld().playSound(portalLoc, Sound.ITEM_TRIDENT_THROW, 1.0f, 1.5f);
                }
                
                @Override
                public void run() {
                    if (tick >= maxTicks) {
                        triggerFinalShatter();
                        cancel();
                        return;
                    }
                    
                    Random random = Hariant.getRandom();
                    
                    // Spawn 3 random slash lines inside the dome space per tick
                    for (int i = 0; i < 3; i++) {
                        // Randomize height (forces the explosion visual upward over the portal)
                        double heightOffset = random.nextDouble(0.2, 3.0);
                        Location slashCenter = portalLoc.clone().add(0, heightOffset, 0);
                        
                        // Randomize direction, tilt, and radius of this individual cut
                        double radius = random.nextDouble(1.2, 2.5);
                        double randomYaw = random.nextDouble(0, 2 * Math.PI);
                        double randomTilt = random.nextDouble(-Math.toRadians(45), Math.toRadians(45));
                        
                        // Setup temporary axes for this random cut
                        Vector forward = new Vector(Math.cos(randomYaw), 0, Math.sin(randomYaw)).normalize();
                        Vector right = new Vector(-forward.getZ(), 0, forward.getX()).normalize();
                        Vector up = new Vector(0, 1, 0);
                        
                        Vector u = right.clone().multiply(Math.cos(randomTilt))
                                        .add(up.clone().multiply(Math.sin(randomTilt)))
                                        .normalize();
                        
                        drawSingleSlash(slashCenter, u, forward, radius);
                    }
                    
                    if (tick % 2 == 0) {
                        portalLoc.getWorld().playSound(portalLoc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.8f, 1.4f);
                    }
                    
                    tick++;
                }
                
                private void drawSingleSlash(Location center, Vector u, Vector forward, double radius) {
                    double maxTheta = Math.toRadians(55); // 110-degree arc
                    double step = Math.toRadians(5);
                    Particle.DustOptions sharpDust = new Particle.DustOptions(Color.fromRGB(240, 245, 255), 0.9f); // Bright white/silver
                    
                    for (double theta = -maxTheta; theta <= maxTheta; theta += step) {
                        double horizontal = Math.sin(theta) * radius;
                        double forwardOffset = Math.cos(theta) * radius * 0.35; // Curve depth
                        
                        Vector offset = u.clone().multiply(horizontal).add(forward.clone().multiply(forwardOffset));
                        Location particleLoc = center.clone().add(offset);
                        
                        // Spawn sharp white dust lines [2]
                        particleLoc.getWorld().spawnParticle(Particle.DUST, particleLoc, 1, 0, 0, 0, 0, sharpDust);
                        
                        // Random sparks along the slash
                        if (Hariant.getRandom().nextDouble() < 0.15) {
                            particleLoc.getWorld().spawnParticle(Particle.CRIT, particleLoc, 1, 0.05, 0.05, 0.05, 0.1);
                        }
                    }
                }
                
                private void triggerFinalShatter() {
                    // Heavy breaking glass/shattering explosion sounds
                    portalLoc.getWorld().playSound(portalLoc, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.9f);
                    portalLoc.getWorld().playSound(portalLoc, Sound.ENTITY_IRON_GOLEM_DEATH, 1.2f, 1.4f);
                    
                    // Huge central explosion cloud
                    portalLoc.getWorld().spawnParticle(
                            Particle.EXPLOSION,
                            portalLoc.clone().add(0, 1.5, 0),
                            4, 1.0, 1.0, 1.0, 0.1
                    );
                    
                    // Ring of sweeping wind blades
                    portalLoc.getWorld().spawnParticle(
                            Particle.SWEEP_ATTACK,
                            portalLoc.clone().add(0, 1.0, 0),
                            10, 1.5, 0.5, 1.5, 0.1
                    );
                }
            }
            
            new JudgmentCutExplosion(context.getPlayer(), context.getPlayer().getLocation()).runTaskTimer(plugin, 0, 1);
        });
        
        register("test_component_shine", (Function<String, SimpleCommand>) name -> new HariantPlayerCommand(name, PlayerRank.ADMIN) {
            @Override
            public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
                final String string = args.joinString(0);
                
                ComponentShine.builder(string).build().display(player, Component.empty(), 2);
            }
        });
        
        register("togglewasd", context -> {
            class Holder extends HariantTask {
                private static Holder holder = null;
                
                private final Player player;
                
                Holder(@NotNull Player player) {
                    super(Scheduler.ofTimer());
                    
                    this.player = player;
                }
                
                @Override
                public void run() {
                    final Input input = player.getCurrentInput();
                    final TextComponent.Builder builder = Component.text();
                    
                    append(builder, Component.text("W"), input.isForward());
                    append(builder, Component.text("A"), input.isLeft());
                    append(builder, Component.text("S"), input.isBackward());
                    append(builder, Component.text("D"), input.isRight());
                    
                    player.showTitle(Title.title(builder.build(), Component.empty(), 0, 5, 0));
                }
                
                private static TextComponent.Builder append(@NotNull TextComponent.Builder builder, @NotNull Component component, boolean condition) {
                    builder.append(component.color(condition ? Colors.GREEN : Colors.DARK_GRAY).decorate(TextDecoration.BOLD));
                    builder.appendSpace();
                    return builder;
                }
            }
            
            final Player player = context.getPlayer();
            
            if (Holder.holder == null) {
                Holder.holder = new Holder(player);
                
                HariantLogger.success(player, Component.text("Now showing WASD."));
            }
            else {
                Holder.holder.cancel();
                Holder.holder = null;
                
                HariantLogger.success(player, Component.text("No longer showing WASD."));
            }
        });
        
        register("bubbleme", context -> {
            final HariantPlayer player = context.getHariantPlayer();
            
            TalentRegistry.BUBBLE_TRAP.execute(player, TalentContext.create(player));
        });
        
        register("riptidefx", context -> {
            class Holder {
                static RiptideFx riptideFx;
            }
            
            final Player player = context.getPlayer();
            
            if (Holder.riptideFx != null) {
                Holder.riptideFx.remove();
                Holder.riptideFx = null;
                
                HariantLogger.success(player, Component.text("Removed riptide!"));
            }
            else {
                final double scale = context.get(0).toDouble(1.0);
                
                Holder.riptideFx = new RiptideFx(player.getLocation(), scale);
                
                HariantLogger.success(player, Component.text("Created riptide with scale %.1f!".formatted(scale)));
            }
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
