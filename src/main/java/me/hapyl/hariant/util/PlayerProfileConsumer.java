package me.hapyl.hariant.util;

import me.hapyl.hariant.profile.PlayerProfile;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface PlayerProfileConsumer extends Consumer<PlayerProfile> {
    
    @Override
    void accept(@NotNull PlayerProfile profile);
}
