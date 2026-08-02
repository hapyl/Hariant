package me.hapyl.hariant.profile.notification;

import me.hapyl.eterna.module.component.Named;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Notification extends Named {
    
    @Override
    @NotNull Component getName();
    
    @NotNull NotificationType getNotificationType();
    
    default @Nullable ClickEvent<?> clickEvent() {
        return null;
    }
    
    static @NotNull Notification create(@NotNull Component name, @NotNull NotificationType notificationType) {
        return new NotificationImpl(name, notificationType);
    }
    
}
