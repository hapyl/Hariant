package me.hapyl.hariant;

import me.hapyl.eterna.module.text.prefix.Prefix;
import me.hapyl.hariant.database.rank.PlayerRank;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public final class HariantLogger {
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm:ss");
    
    private static final Prefix PREFIX_ERROR = defaultPrefix(Component.text("✘", TextColor.color(0xAF0000)));
    private static final Prefix PREFIX_SUCCESS = defaultPrefix(Component.text("✔", TextColor.color(0x12CD08)));
    private static final Prefix PREFIX_SYSTEM = defaultPrefix(Component.text("SYSTEM", TextColor.color(0xFF2F31), TextDecoration.BOLD));
    
    private static final Logger LOGGER = Hariant.getPlugin().getLogger();
    
    private HariantLogger() {
    }
    
    public static void info(@NotNull Audience audience, @NotNull Component message) {
        audience.sendMessage(message.color(Colors.DEFAULT_COLOR));
    }
    
    public static void success(@NotNull Audience audience, @NotNull Component message) {
        PREFIX_SUCCESS.sendMessage(audience, message.color(Colors.SUCCESS));
    }
    
    public static void error(@NotNull Audience audience, @NotNull Component message) {
        PREFIX_ERROR.sendMessage(audience, message.color(Colors.ERROR));
    }
    
    public static void system(@NotNull Audience audience, @NotNull Component message) {
        PREFIX_SYSTEM.sendMessage(audience, message.color(TextColor.color(0xD6BEB8)));
    }
    
    public static void debug(@NotNull Object object) {
        final TextComponent message = Component.empty()
                                               .append(Component.text("[DEBUG] ", NamedTextColor.RED))
                                               .append(Component.text("%s ".formatted(TIME_FORMATTER.format(LocalTime.now())), NamedTextColor.DARK_GRAY))
                                               .append(Component.text(String.valueOf(object), NamedTextColor.YELLOW));
        
        Bukkit.getOnlinePlayers().stream()
              .filter(player -> PlayerRank.getRank(player).isOrHigher(PlayerRank.ADMIN))
              .forEach(player -> player.sendMessage(message));
        
        Bukkit.getConsoleSender().sendMessage(message);
    }
    
    public static void debugValues(@NotNull Object... values) {
        if (values.length % 2 != 0) {
            throw new IllegalArgumentException("Values length must be divisible by 2!");
        }
        
        final StringBuilder builder = new StringBuilder();
        
        for (int i = 0; i < values.length; i++) {
            final Object object = values[i];
            
            if (i % 2 == 0) {
                if (!(object instanceof String string)) {
                    throw new IllegalArgumentException("First value must be String, not %s!".formatted(object.getClass().getSimpleName()));
                }
                
                if (i != 0) {
                    builder.append(", ");
                }
                
                builder.append("%s=".formatted(string));
            }
            else {
                builder.append(object);
            }
        }
        
        debug(builder.toString());
    }
    
    @NotNull
    public static Logger logger() {
        return LOGGER;
    }
    
    private static Prefix defaultPrefix(@NotNull Component prefix) {
        return Prefix.create(
                Component.empty()
                         .append(Component.text("[", NamedTextColor.DARK_GRAY))
                         .append(prefix)
                         .append(Component.text("]", NamedTextColor.DARK_GRAY)),
                Component.text(" ")
        );
    }
    
    public interface Sender extends ForwardingAudience.Single {
        
        @NotNull
        @Override
        Audience audience();
        
        default void messageInfo(@NotNull Component message) {
            info(this, message);
        }
        
        default void messageSuccess(@NotNull Component message) {
            success(this, message);
        }
        
        default void messageError(@NotNull Component message) {
            error(this, message);
        }
        
        default void messageSystem(@NotNull Component message) {
            system(this, message);
        }
    }
    
}
