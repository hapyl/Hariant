package me.hapyl.hariant.profile.message;

import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public class MessageChannelImpl implements MessageChannel {
    
    private final Pattern lookupPattern;
    
    MessageChannelImpl(@Nullable String ch) {
        this.lookupPattern = ch != null ? Pattern.compile("^%s\\s*".formatted(Pattern.quote(ch))) : null;
    }
    
    @Nullable
    @Override
    public Pattern lookupPattern() {
        return lookupPattern;
    }
    
    @NotNull
    @Override
    public Component channelPrefix(@NotNull PlayerProfile profile) {
        return Component.empty();
    }
    
    @Override
    public boolean isAccessible(@NotNull PlayerProfile profile) {
        return true;
    }
    
    @NotNull
    @Override
    public Stream<PlayerProfile> recipients(@NotNull PlayerProfile profile) {
        // TODO @Feb 16, 2026 (xanyjl) -> Filter blocks, etc
        return Hariant.getPlayerProfiles();
    }
    
}
