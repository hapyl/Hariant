package me.hapyl.hariant.profile.notification;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record NotificationResult(@NotNull List<? extends Notification> notifications, int sizeTotal, int sizeFiltered) implements ComponentLike {
    
    @Override
    public @NotNull Component asComponent() {
        return sizeTotal == sizeFiltered
               ? Component.text("(%s)".formatted(sizeTotal))
               : Component.text("(%s/%s)".formatted(sizeFiltered, sizeTotal));
    }
    
    public boolean hasOf(@NotNull Class<? extends DeclaresNotifaction> declaringClass) {
        for (Notification notification : notifications) {
            if (notification.declaringClass() == declaringClass) {
                return true;
            }
        }
        
        return false;
    }
    
    public int countOf(@NotNull Class<? extends DeclaresNotifaction> declaringClass) {
        int count = 0;
        
        for (Notification notification : notifications) {
            if (notification.declaringClass() == declaringClass) {
                count++;
            }
        }
        
        return count;
    }
    
    public boolean isEmpty() {
        return sizeTotal == 0;
    }
    
}
