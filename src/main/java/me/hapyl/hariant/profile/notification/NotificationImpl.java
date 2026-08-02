package me.hapyl.hariant.profile.notification;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class NotificationImpl implements Notification {
    
    private final Component name;
    private final NotificationType notificationType;
    
    NotificationImpl(@NotNull Component name, @NotNull NotificationType notificationType) {
        this.name = name;
        this.notificationType = notificationType;
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public @NotNull NotificationType getNotificationType() {
        return notificationType;
    }
    
}