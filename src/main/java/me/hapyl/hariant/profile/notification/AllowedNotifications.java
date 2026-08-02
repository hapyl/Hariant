package me.hapyl.hariant.profile.notification;

import me.hapyl.eterna.module.component.Described;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public enum AllowedNotifications implements ComponentLike, Described {
    
    ALL(
            Component.text("All"),
            Component.text("You will receive all notifications.")
    ),
    
    IMPORTANT_ONLY(
            Component.text("Important Only"),
            Component.text("You will only receive important notifications, eg: rewards that expire.")
    ),
    
    SEVERE_ONLY(
            Component.text("Severe Only"),
            Component.text("You will only receive notifications of severe importance.")
    );
    
    private final Component name;
    private final Component description;
    
    AllowedNotifications(@NotNull Component name, @NotNull Component description) {
        this.name = name;
        this.description = description;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return name;
    }
    
    @Override
    public @NotNull Component getDescription() {
        return description;
    }
    
    public boolean isAllowed(@NotNull Notification notification) {
        return switch (this) {
            case ALL -> true;
            case IMPORTANT_ONLY -> notification.getNotificationType().isOrHigher(NotificationType.IMPORTANT);
            case SEVERE_ONLY -> notification.getNotificationType().isOrHigher(NotificationType.SEVERE);
        };
    }
    
}