package me.hapyl.hariant.profile.notification;

import me.hapyl.eterna.module.component.Named;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Notification extends Named {
    
    @NotNull Class<? extends DeclaresNotifaction> declaringClass();
    
    @Override
    @NotNull Component getName();
    
    @NotNull NotificationType getNotificationType();
    
    default @Nullable ClickEvent<?> clickEvent() {
        return null;
    }
    
    static @NotNull Notification create(@NotNull Class<? extends DeclaresNotifaction> declaringClass, @NotNull Component name, @NotNull NotificationType notificationType, @Nullable ClickEvent<?> clickEvent) {
        return new NotificationImpl(declaringClass, name, notificationType, clickEvent);
    }
    
}
