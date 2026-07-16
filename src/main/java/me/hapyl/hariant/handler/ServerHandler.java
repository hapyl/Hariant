package me.hapyl.hariant.handler;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.CachedServerIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class ServerHandler implements Listener {
    
    private final CachedServerIcon serverIcon;
    private final Component motd;
    
    public ServerHandler() {
        this.serverIcon = loadServerIcon();
        this.motd = createMotd();
    }
    
    @EventHandler
    public void handlePaperServerListPingEvent(PaperServerListPingEvent ev) {
        ev.setServerIcon(serverIcon);
        ev.motd(motd);
        ev.setMaxPlayers(ev.getNumPlayers() + 1);
    }
    
    private static @Nullable CachedServerIcon loadServerIcon() {
        try {
            final InputStream favicon = Hariant.getPlugin().getResource("favicon.png");
            
            if (favicon == null) {
                return null;
            }
            
            final BufferedImage bufferedImage = ImageIO.read(favicon);
            final int width = bufferedImage.getWidth();
            final int height = bufferedImage.getHeight();
            
            if (width != 64 || height != 64) {
                throw new IllegalArgumentException("Favicon must be 64x64, not %sx%s!".formatted(width, height));
            }
            
            return Bukkit.loadServerIcon(bufferedImage);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Error loading `favicon.png`", ex);
        }
    }
    
    private static @NotNull Component createMotd() {
        return Component.empty().append(
                Component.empty()
                         .append(
                                 Component.empty()
                                          .append(Component.text("˚", Colors.GRAY))
                                          .append(Component.text(" 　　　"))
                                          .append(Component.text("✦", Colors.WHITE))
                                          .append(Component.text("　　"))
                                          .append(Component.text(".", Colors.GRAY))
                                          .append(Component.text("　　"))
                                          .append(Component.text(".", Colors.GRAY))
                                          .append(Component.text(" 　"))
                                          .append(Component.text("˚", Colors.WHITE))
                                          .append(Component.text("　"))
                                          .append(Component.text(".", Colors.GRAY))
                                          .append(Component.text("　　　 "))
                                          .append(Component.text(".", Colors.GRAY))
                                          .append(Component.text(" "))
                                          .append(Component.text("✦", Colors.WHITE))
                                          .append(Component.text(" "))
                                          .append(Hariant.GAME_NAME)
                                          .append(Component.text(" "))
                                          .append(Component.text("「v%s」".formatted(Hariant.getVersion()), Colors.DARK_GRAY))
                         )
                         .appendNewline()
                         .append(
                                 Component.empty()
                                          .append(
                                                  Component.empty()
                                                           .append(Component.text("　"))
                                                           .append(Component.text(".", Colors.GRAY))
                                                           .append(Component.text(" 　"))
                                                           .append(Component.text("˚", Colors.GRAY))
                                                           .append(Component.text("　　　　"))
                                                           .append(Component.text("*", Colors.GRAY))
                                                           .append(Component.text(" 　　"))
                                                           .append(Component.text("✦", Colors.WHITE))
                                                           .append(Component.text("　　"))
                                                           .append(Component.text(".", Colors.GRAY))
                                                           .append(Component.text("  "))
                                                           .append(Component.text(""))
                                          )
                                          .append(Component.text("「", Colors.DARK_GRAY))
                                          .append(Hariant.UPDATE_TOPIC)
                                          .append(Component.text("」", Colors.DARK_GRAY))
                         ));
    }
    
}
