package me.hapyl.hariant.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface MessageSender {
    
    default void sendMessage(@NotNull Audience audience, @NotNull Component message) {
        audience.sendMessage(message);
    }
    
    default void sendActionbar(@NotNull Audience audience, @NotNull Component actionbar) {
        audience.sendActionBar(actionbar);
    }
    
}
