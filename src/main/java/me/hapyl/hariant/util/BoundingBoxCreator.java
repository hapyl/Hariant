package me.hapyl.hariant.util;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.entity.EntityCollector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BoundingBoxCreator implements Ticking {
    
    private static final Map<Player, BoundingBoxCreator> PLAYER_CREATORS;
    private static final Style STYLE_INACTIVE;
    
    static {
        PLAYER_CREATORS = Maps.newHashMap();
        STYLE_INACTIVE = Style.style(Colors.GRAY, TextDecoration.STRIKETHROUGH);
        
        // Create handler
        final Handler handler = new Handler();
        
        Bukkit.getPluginManager().registerEvents(handler, Hariant.getPlugin());
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Hariant.getPlugin(), handler, 0, 5);
    }
    
    private final Player player;
    
    private Location locationFirst;
    private Location locationSecond;
    
    private BoundingBoxCreator(@NotNull Player player) {
        this.player = player;
    }
    
    public @NotNull BoundingBox createBoundingBox() throws IllegalStateException {
        if (locationFirst == null || locationSecond == null) {
            throw new IllegalStateException("locationFirst == null || locationSecond == null");
        }
        
        final double minX = Math.min(locationFirst.getX(), locationSecond.getX());
        final double minY = Math.min(locationFirst.getY(), locationSecond.getY());
        final double minZ = Math.min(locationFirst.getZ(), locationSecond.getZ());
        final double maxX = Math.max(locationFirst.getX(), locationSecond.getX()) + 1;
        final double maxY = Math.max(locationFirst.getY(), locationSecond.getY()) + 1;
        final double maxZ = Math.max(locationFirst.getZ(), locationSecond.getZ()) + 1;
        
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }
    
    @Override
    public void tick() {
        boolean inside = false;
        
        // If both locations are defined, render the bounding box
        if (this.isDefined()) {
            final BoundingBox boundingBox = this.createBoundingBox();
            
            if (boundingBox.contains(player.getX(), player.getY(), player.getZ())) {
                inside = true;
            }
            
            EntityCollector.BoundingBoxRenderer.render(boundingBox, player.getWorld(), Color.GREEN, 0.8f);
            
            // Show locations at actionbar
            final Component actionbar = Component.empty()
                                                 .append(locationToComponent(boundingBox, true))
                                                 .append(Component.text("  "))
                                                 .append(sizeComponent(boundingBox))
                                                 .append(Component.text("  "))
                                                 .append(locationToComponent(boundingBox, false))
                                                 .append(inside ? Component.text("  ⬛", Colors.GREEN) : Component.text("  ⬜", Colors.DARK_GRAY));
            
            player.sendActionBar(player.isSneaking() ? Components.applyStyle(actionbar, STYLE_INACTIVE) : actionbar);
        }
        else {
            player.sendActionBar(Component.text("Not yet defined!", Colors.GRAY));
        }
    }
    
    public boolean isDefined() {
        return locationFirst != null && locationSecond != null;
    }
    
    public static void coordinates(@NotNull Player player) {
        final BoundingBoxCreator boundingBoxCreator = PLAYER_CREATORS.get(player);
        
        if (boundingBoxCreator == null) {
            HariantLogger.error(player, Component.text("You don't have a bounding box creator!"));
            return;
        }
        
        if (!boundingBoxCreator.isDefined()) {
            HariantLogger.error(player, Component.text("Either first or second location isn't set!"));
            return;
        }
        
        final BoundingBox boundingBox = boundingBoxCreator.createBoundingBox();
        final String coordinates = "%.1f, %.1f, %.1f, %.1f, %.1f, %.1f".formatted(
                boundingBox.getMinX(),
                boundingBox.getMinY(),
                boundingBox.getMinZ(),
                boundingBox.getMaxX(),
                boundingBox.getMaxY(),
                boundingBox.getMaxZ()
        );
        
        HariantLogger.success(
                player,
                Component.empty()
                         .hoverEvent(HoverEvent.showText(Component.text("Click to copy!", Colors.YELLOW)))
                         .clickEvent(ClickEvent.suggestCommand(coordinates))
                         .append(Component.text("%s".formatted(coordinates)))
                         .appendSpace()
                         .append(Component.text("CLICK TO COPY", Colors.GOLD, TextDecoration.BOLD, TextDecoration.UNDERLINED))
        );
    }
    
    public static void toggle(@NotNull Player player) {
        if (PLAYER_CREATORS.remove(player) == null) {
            PLAYER_CREATORS.put(player, new BoundingBoxCreator(player));
            
            HariantLogger.success(player, Component.text("Enabled bounding box creator."));
        }
        else {
            HariantLogger.success(player, Component.text("Disabled bounding box creator."));
        }
    }
    
    private static @NotNull Component sizeComponent(@NotNull BoundingBox boundingBox) {
        final double sizeX = boundingBox.getMaxX() - boundingBox.getMinX();
        final double sizeY = boundingBox.getMaxY() - boundingBox.getMinY();
        final double sizeZ = boundingBox.getMaxZ() - boundingBox.getMinZ();
        
        return Component.empty()
                        .append(Component.text("(", Colors.DARK_GRAY))
                        .append(Component.text("%.1f".formatted(sizeX), Colors.RED)).append(Component.text(", ", Colors.GRAY))
                        .append(Component.text("%.1f".formatted(sizeY), Colors.GREEN)).append(Component.text(", ", Colors.GRAY))
                        .append(Component.text("%.1f".formatted(sizeZ), Colors.AQUA))
                        .append(Component.text(")", Colors.DARK_GRAY));
    }
    
    private static @NotNull Component locationToComponent(@NotNull BoundingBox boundingBox, boolean min) {
        return Component.empty()
                        .append(Component.text("[", Colors.DARK_GRAY))
                        .append(Component.text("%.1f".formatted(min ? boundingBox.getMinX() : boundingBox.getMaxX()), Colors.RED)).append(Component.text(", ", Colors.GRAY))
                        .append(Component.text("%.1f".formatted(min ? boundingBox.getMinY() : boundingBox.getMaxY()), Colors.GREEN)).append(Component.text(", ", Colors.GRAY))
                        .append(Component.text("%.1f".formatted(min ? boundingBox.getMinZ() : boundingBox.getMaxZ()), Colors.AQUA))
                        .append(Component.text("]", Colors.DARK_GRAY));
    }
    
    private static class Handler implements Listener, Runnable {
        
        private Handler() {
        }
        
        @EventHandler
        public void handlePlayerInteractEvent(PlayerInteractEvent ev) {
            final Player player = ev.getPlayer();
            
            if (ev.getHand() == EquipmentSlot.OFF_HAND || !(ev.getClickedBlock() instanceof Block block) || player.isSneaking()) {
                return;
            }
            
            final BoundingBoxCreator boundingBoxCreator = PLAYER_CREATORS.get(player);
            
            if (boundingBoxCreator == null) {
                return;
            }
            
            if (ev.getAction() == Action.LEFT_CLICK_BLOCK) {
                boundingBoxCreator.locationFirst = block.getLocation();
                
                HariantLogger.sound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 0.75f);
            }
            else if (ev.getAction() == Action.RIGHT_CLICK_BLOCK) {
                boundingBoxCreator.locationSecond = block.getLocation();
                
                HariantLogger.sound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1.25f);
            }
            
            ev.setCancelled(true);
        }
        
        @Override
        public void run() {
            for (BoundingBoxCreator creator : PLAYER_CREATORS.values()) {
                creator.tick();
            }
        }
        
    }
    
}