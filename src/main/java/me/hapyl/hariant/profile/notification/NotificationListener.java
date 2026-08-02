package me.hapyl.hariant.profile.notification;

import me.hapyl.hariant.profile.PlayerProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface NotificationListener {
    
    @Nullable Notification listen(@NotNull PlayerProfile profile);
    
}
