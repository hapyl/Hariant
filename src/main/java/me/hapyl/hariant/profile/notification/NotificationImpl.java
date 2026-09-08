package me.hapyl.hariant.profile.notification;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NotificationImpl implements Notification {
    
    private final Class<? extends DeclaresNotifaction> declaringClass;
    private final Component name;
    private final NotificationType notificationType;
    private final ClickEvent<?> clickEvent;
    
    NotificationImpl(@NotNull Class<? extends DeclaresNotifaction> declaringClass, @NotNull Component name, @NotNull NotificationType notificationType, ClickEvent<?> clickEvent) {
        this.declaringClass = declaringClass;
        this.name = name;
        this.notificationType = notificationType;
        this.clickEvent = clickEvent;
    }
    
    @Override
    public @NotNull Class<? extends DeclaresNotifaction> declaringClass() {
        return declaringClass;
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public @NotNull NotificationType getNotificationType() {
        return notificationType;
    }
    
    @Override
    public @Nullable ClickEvent<?> clickEvent() {
        return clickEvent;
    }
    
}