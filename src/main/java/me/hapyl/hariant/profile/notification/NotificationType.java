package me.hapyl.hariant.profile.notification;

import me.hapyl.eterna.module.component.Styled;
import me.hapyl.hariant.util.ComparableOrdinal;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public enum NotificationType implements ComparableOrdinal<NotificationType>, Styled {
    
    /**
     * Normal type of notification, can be cleared at any time.
     */
    NORMAL(Style.style(TextColor.color(0x00E5A0))),
    
    /**
     * Important type of notifications that must be cleared as soon as possible.
     *
     * <p>
     * Eg: daily rewards that will expire unless claimed.
     * </p>
     */
    IMPORTANT(Style.style(TextColor.color(0xFFD700))),
    
    /**
     * The most important of notifications, must be cleared right here and right now.
     *
     * <p>
     * This type of notifications cannot be disabled in settings.
     * </p>
     */
    SEVERE(Style.style(TextColor.color(0xFF5555)));
    
    private final Style style;
    
    NotificationType(@NotNull Style style) {
        this.style = style;
    }
    
    @Override
    public @NotNull Style getStyle() {
        return style;
    }
    
}
