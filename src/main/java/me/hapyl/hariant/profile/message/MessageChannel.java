package me.hapyl.hariant.profile.message;

import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public interface MessageChannel {
    
    @Nullable
    Pattern lookupPattern();
    
    @NotNull
    Component channelPrefix(@NotNull PlayerProfile profile);
    
    boolean isAccessible(@NotNull PlayerProfile profile);
    
    @NotNull
    Stream<PlayerProfile> recipients(@NotNull PlayerProfile profile);
    
}
