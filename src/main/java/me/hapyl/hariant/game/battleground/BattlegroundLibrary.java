package me.hapyl.hariant.game.battleground;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.alphabet.Alphabet;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.cooldown.HariantCooldown;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DamageSourceImpl;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.game.GameInstance;
import me.hapyl.hariant.game.WinResult;
import me.hapyl.hariant.game.battleground.feature.BattlegroundFeatureImpl;
import me.hapyl.hariant.inventory.drop.CommonDroppable;
import me.hapyl.hariant.inventory.drop.DropTable;
import me.hapyl.hariant.inventory.drop.Droppable;
import me.hapyl.hariant.inventory.item.ItemRegistry;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.ImmutableLocation;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class BattlegroundLibrary extends BattlegroundImpl {
    
    private static final int VOID_INCREMENT_TELEPORT = 1;
    private static final int VOID_LIMIT = 7; // This shit is kinda hardcoded but make sure it's the same as length of VOID_CHARS
    private static final int VOID_DECREMENT_THRESHOLD = 200;
    
    private static final Decimal VOID_DAMAGE_OF_MAX_HEALTH = Decimal.ofPercentage(30);
    
    private static final Component VOID_COMPONENT_5 = Component.text("ᴛʜᴇ ᴠᴏɪᴅ ɪꜱ ᴡᴀᴛᴄʜɪɴɢ...", Colors.DARK_PURPLE);
    private static final Component VOID_COMPONENT_6 = Component.text("ᴛʜᴇ ᴠᴏɪᴅ ɪꜱ ʟᴜʀᴋɪɴɢ...", Colors.DARK_PURPLE);
    private static final Component VOID_COMPONENT_7 = Component.text("ᴛʜᴇ ᴠᴏɪᴅ ᴄᴏɴꜱᴜᴍᴇꜱ ʏᴏᴜ...", Colors.DARK_PURPLE);
    
    private static final Style VOID_STYLE_FILLED = Style.style(Colors.LIGHT_PURPLE, TextDecoration.UNDERLINED);
    private static final Style VOID_STYLE_EMPTY = Style.style(Colors.DARK_GRAY);
    
    private static final char[] VOID_CHARS = Alphabet.FUTHARK.translateTo("pustota").toCharArray();
    
    private static final HariantCooldown VOID_TELEPORT_COOLDOWN = HariantCooldown.ofSeconds(Key.ofString("the_void_teleport_cooldown"), 1.5f);
    
    private static final DamageSourceIdentity DAMAGE_SOURCE_IDENTITY = DamageSourceIdentity.create(
            Key.ofString("the_void"),
            Component.text("The Void"),
            DeathMessage.create("{player} was consumed by the Void [while running from {killer}]")
    );
    
    BattlegroundLibrary() {
        super(
                Component.text("Infinite Library"),
                Component.text("A chunk of a massive library that is seemingly stuck in the void."),
                new DropTableLibrary(),
                Icon.ofMaterial(Material.BOOKSHELF)
        );
        
        setSpawnLocations(
                ImmutableLocation.create(4000, 64.1, 0, -180, 0),
                ImmutableLocation.create(3991, 74, 5, -180f, 0f),
                ImmutableLocation.create(4018, 74, -7, 90f, 0f),
                ImmutableLocation.create(3990.0, 65.0, -14.0),
                ImmutableLocation.create(4009.0, 74.0, -14.0),
                ImmutableLocation.create(3991.0, 74.0, -14.0)
        );
        
        setTimeBeforePlayersReveal(Tick.fromSeconds(5));
        setSize(Size.MEDIUM);
        
        setFeatures(new FeatureVoid());
    }
    
    private static class FeatureVoid extends BattlegroundFeatureImpl {
        
        private final List<? extends VoidPortal> portals;
        private final Map<HariantPlayer, Integer> voidValues;
        
        private int tick;
        
        FeatureVoid() {
            super(
                    Component.text("Void Portals"),
                    Component.empty()
                             .append(Component.text("A series of Abyssal portals, capable of transport within the Library."))
                             .appendNewline()
                             .appendNewline()
                             .append(Component.text("But be aware, continuous use of Abyssal power may as well consume you..."))
            );
            
            this.portals = List.of(
                    new VoidPortal(new BoundingBox(3976.0, 64.0, -10.0, 3982.0, 70.0, -3.0), ImmutableLocation.create(3984.0, 64.0, -7.0, -90f, 0f)),
                    new VoidPortal(new BoundingBox(4006.0, 64.0, -28.0, 4013.0, 70.0, -22.0), ImmutableLocation.create(4009.0, 64.0, -20.0)),
                    new VoidPortal(new BoundingBox(4019.0, 64.0, -10.0, 4025.0, 70.0, -3.0), ImmutableLocation.create(4016.0, 64.0, -7.0, 90f, 0f)),
                    new VoidPortal(new BoundingBox(3988.0, 74.0, 21.0, 3995.0, 80.0, 27.0), ImmutableLocation.create(3991.0, 74.0, 18.0, -180f, 0f)),
                    new VoidPortal(new BoundingBox(3976.0, 74.0, -10.0, 3982.0, 80.0, -3.0), ImmutableLocation.create(3984.0, 74.0, -7.0, -90f, 0f)),
                    new VoidPortal(new BoundingBox(3988.0, 74.0, -28.0, 3995.0, 80.0, -22.0), ImmutableLocation.create(3991.0, 74.0, -20.0)),
                    new VoidPortal(new BoundingBox(4006.0, 74.0, -28.0, 4013.0, 80.0, -22.0), ImmutableLocation.create(4009.0, 74.0, -20.0)),
                    new VoidPortal(new BoundingBox(4019.0, 74.0, -10.0, 4025.0, 80.0, -3.0), ImmutableLocation.create(4016.0, 74.0, -7.0, 90f, 0f))
            );
            
            this.voidValues = Maps.newHashMap();
        }
        
        @Override
        public void onDeath(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player, @Nullable HariantEntity killer) {
            voidValues.remove(player);
        }
        
        @Override
        public void onDestroy(@NotNull Iterable<? extends HariantPlayer> players, @NotNull WinResult result) {
            voidValues.clear();
        }
        
        @Override
        public void tick() {
            Hariant.getPlayers().forEach(player -> {
                if (player.hasCooldown(VOID_TELEPORT_COOLDOWN)) {
                    return;
                }
                
                // Check if player is within portals bounding box
                for (VoidPortal portal : portals) {
                    if (portal.entrance.contains(player.x(), player.y(), player.z())) {
                        final VoidPortal exitPortal = findExitPortal(portal);
                        
                        final Location location = exitPortal.exit.getCenteredLocation();
                        final Location playerLocation = player.getLocation();
                        
                        // Merge player's pitch
                        location.setPitch(playerLocation.getPitch());
                        
                        player.teleport(location);
                        
                        // Increment void for the player
                        incrementVoid(player, VOID_INCREMENT_TELEPORT);
                        
                        player.setCooldown(VOID_TELEPORT_COOLDOWN);
                    }
                }
                
                // Decrement void charges
                if (tick % VOID_DECREMENT_THRESHOLD == 0 && voidValues.containsKey(player)) {
                    final Integer newValue = voidValues.computeIfPresent(player, (_, v) -> v <= 1 ? null : v - 1);
                    
                    showVoid(player, newValue != null ? newValue : 0);
                }
            });
            
            // Spawn fx on each portal
            if (tick % 5 == 0) {
                for (VoidPortal portal : portals) {
                    PlayerLib.spawnParticle(portal.centre, Particle.PORTAL, 10, 0.2, 1.5, 0.2, 1.0f);
                    PlayerLib.spawnParticle(portal.centre, Particle.REVERSE_PORTAL, 10, 0.2, 1.5, 0.2, 0.5f);
                    PlayerLib.spawnParticle(portal.centre, Particle.ENCHANT, 10, 0.2, 1.5, 0.2, 1.0f);
                }
            }
            
            tick++;
        }
        
        private void incrementVoid(@NotNull HariantPlayer player, int value) {
            final int newValue = voidValues.merge(player, value, (a, b) -> Math.min(a + b, VOID_LIMIT));
            
            showVoid(player, newValue);
            
            // If new value is the limit, deal damage
            if (newValue == VOID_LIMIT) {
                player.damage(new DamageSourceTheVoid(VOID_DAMAGE_OF_MAX_HEALTH.doubleValue() * player.getMaxHealth()));
                
                // TODO (xanyjl @ Sunday, August 30) -> Achievement
            }
        }
        
        private void showVoid(@NotNull HariantPlayer player, int value) {
            final TextComponent.Builder title = Component.text();
            
            for (int i = 0; i < VOID_CHARS.length; i++) {
                title.append(Component.text(VOID_CHARS[i], i < value ? VOID_STYLE_FILLED : VOID_STYLE_EMPTY));
            }
            
            final Component subtitle;
            
            // Create subtitle, which is kinda hardcoded
            switch (value) {
                case 5 -> subtitle = VOID_COMPONENT_5;
                case 6 -> subtitle = VOID_COMPONENT_6;
                case 7 -> subtitle = VOID_COMPONENT_7;
                default -> subtitle = Component.empty();
            }
            
            player.showTitle(Title.title(title.asComponent(), subtitle, 0, 40, 10));
            player.playSound(Sound.AMBIENT_SOUL_SAND_VALLEY_MOOD, 2.0f);
        }
        
        private @NotNull VoidPortal findExitPortal(@NotNull VoidPortal voidPortal) {
            int random = Hariant.getRandom().nextInt(portals.size() - 1);
            
            if (random == voidPortal.index) {
                random = (random + 1) % portals.size();
            }
            
            return portals.get(random);
        }
        
    }
    
    private static class DamageSourceTheVoid extends DamageSourceImpl {
        DamageSourceTheVoid(double damage) {
            super(DAMAGE_SOURCE_IDENTITY, null, DamageType.ANOMALY, ElementType.AETHER, List.of(), Set.of(), damage, 0);
        }
    }
    
    private static class VoidPortal {
        
        private static int INDEX;
        
        private final BoundingBox entrance;
        private final Location centre;
        private final ImmutableLocation exit;
        private final int index;
        
        private VoidPortal(@NotNull BoundingBox entrance, @NotNull ImmutableLocation exit) {
            this.entrance = entrance;
            this.centre = new Location(Hariant.WORLD, entrance.getCenterX(), entrance.getCenterY(), entrance.getCenterZ());
            this.exit = exit;
            this.index = INDEX++;
        }
        
    }
    
    private static class DropTableLibrary extends DropTable {
        
        DropTableLibrary() {
            super(
                    List.of(
                            CommonDroppable.CAT_COINS,
                            CommonDroppable.ARTIFACT_ARTIFICER,
                            CommonDroppable.HERO_RECRUIT_VOUCHER,
                            Droppable.ofItem(ItemRegistry.ARTIFACT_MAGIC_CODEX, 50)
                    ),
                    DEFAULT_DROP_TABLE_AMOUNT
            );
        }
        
    }
    
}