package me.hapyl.hariant.profile.message;

import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.database.rank.RankFormatter;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class MessageChannelImpl implements MessageChannel {
    
    private final Component prefix;
    
    MessageChannelImpl(@NotNull Component prefix) {
        this.prefix = prefix;
    }
    
    @NotNull
    @Override
    public Component channelPrefix() {
        return prefix;
    }
    
    @Override
    public boolean isAccessible(@NotNull PlayerProfile profile) {
        return true;
    }
    
    @Override
    public @NotNull Stream<PlayerProfile> recipients() {
        return Hariant.getPlayerProfiles().filter(this::isAccessible);
    }
    
    @Override
    public @NotNull Component formatProfile(@NotNull PlayerProfile profile) {
        return profile.getNameFormattedSocial();
    }
    
}