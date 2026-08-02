package me.hapyl.hariant.entity.player.combat;

import me.hapyl.hariant.entity.damage.AssistSource;
import org.jetbrains.annotations.NotNull;

public record Assist(@NotNull AssistSource assistSource, long assistedAt) {
    
    public long millisSinceLastAssist() {
        return System.currentTimeMillis() - assistedAt;
    }
    
}
