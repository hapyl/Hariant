package me.hapyl.hariant;

import me.hapyl.eterna.module.text.prefix.Prefix;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public final class HariantLogger {
    
    public static final Prefix PREFIX_INFO = create(Component.text("ℹ", TextColor.color(0xB6CFE1)), Style.style(Colors.GRAY));
    public static final Prefix PREFIX_SUCCESS = createDefault(Component.text("✔", TextColor.color(0x12CD08)), Style.style(Colors.SUCCESS));
    public static final Prefix PREFIX_ERROR = createDefault(Component.text("✘", TextColor.color(0xAF0000)), Style.style(Colors.ERROR));
    public static final Prefix PREFIX_SYSTEM = createDefault(Component.text("SYSTEM", TextColor.color(0xFF2F31), TextDecoration.BOLD), Style.style(TextColor.color(0xD6BEB8)));
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm:ss");
    private static final Logger LOGGER = Hariant.getPlugin().getLogger();
    
    private HariantLogger() {
    }
    
    public static void info(@NotNull Audience audience, @NotNull Component message) {
        PREFIX_INFO.sendMessage(audience, message);
    }
    
    public static void success(@NotNull Audience audience, @NotNull Component message) {
        PREFIX_SUCCESS.sendMessage(audience, message);
    }
    
    public static void error(@NotNull Audience audience, @NotNull Component message) {
        PREFIX_ERROR.sendMessage(audience, message);
    }
    
    public static void system(@NotNull Audience audience, @NotNull Component message) {
        PREFIX_SYSTEM.sendMessage(audience, message);
    }
    
    public static void debug(@NotNull Object object) {
        final TextComponent message = Component.empty()
                                               .append(Component.text("[DEBUG] ", Colors.RED))
                                               .append(Component.text("%s ".formatted(TIME_FORMATTER.format(LocalTime.now())), Colors.DARK_GRAY))
                                               .append(Component.text(String.valueOf(object), Colors.YELLOW));
        
        Bukkit.getOnlinePlayers().stream()
              .filter(ServerOperator::isOp) // Fuck it, just check for operator instead of rank, since it throws errors when used wrongly
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
    
    public static void sound(@NotNull Player player, @NotNull Sound sound, @Range(from = 0, to = 2) float pitch) {
        player.playSound(player, sound, 3.0f, Math.clamp(pitch, 0.0f, 2.0f));
    }
    
    @NotNull
    public static Logger logger() {
        return LOGGER;
    }
    
    private static @NotNull Prefix create(@NotNull Component prefix, @NotNull Style messageStyle) {
        return new HariantPrefix(prefix, Component.space(), messageStyle);
    }
    
    private static @NotNull Prefix createDefault(@NotNull Component prefix, @NotNull Style messageStyle) {
        return create(
                Component.empty()
                         .append(Component.text("「", Colors.DARK_GRAY))
                         .append(prefix)
                         .append(Component.text("」", Colors.DARK_GRAY)),
                messageStyle
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
    
    public static class HariantPrefix implements Prefix {
        
        private final Component prefix;
        private final Component separator;
        private final Style messageStyle;
        
        HariantPrefix(@NotNull Component prefix, @NotNull Component separator, @NotNull Style messageStyle) {
            this.prefix = prefix;
            this.separator = separator;
            this.messageStyle = messageStyle;
        }
        
        @Override
        public @NotNull Component getPrefix() {
            return prefix;
        }
        
        @Override
        public @NotNull Component getSeparator() {
            return separator;
        }
        
        @Override
        public void sendMessage(@NotNull Audience audience, @NotNull Component message) {
            audience.sendMessage(this.prefix(message));
        }
        
        @Override
        public void broadcastMessage(@NotNull Component message) {
            Bukkit.broadcast(this.prefix(message));
        }
        
        private @NotNull Component prefix(@NotNull Component message) {
            return Component.empty()
                            .append(prefix)
                            .append(separator)
                            .append(message.style(messageStyle));
        }
        
    }
    
}
