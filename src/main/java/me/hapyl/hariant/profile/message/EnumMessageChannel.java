package me.hapyl.hariant.profile.message;

import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.team.EnumTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public enum EnumMessageChannel implements MessageChannel {
    
    GLOBAL(new MessageChannelImpl(null)),
    
    TEAM(
            new MessageChannelImpl("#") {
                
                @NotNull
                @Override
                public Component channelPrefix(@NotNull PlayerProfile profile) {
                    final EnumTeam team = profile.getTeam();
                    final Style style = team.getStyle();
                    
                    return Component.empty()
                                    .append(Component.text("[", style))
                                    .append(team.asComponent())
                                    .append(Component.text("]", style))
                                    .appendSpace();
                }
                
                @NotNull
                @Override
                public Stream<PlayerProfile> recipients(@NotNull PlayerProfile profile) {
                    return profile.getTeam().getPlayerProfiles();
                }
            }
    ),
    
    STAFF(
            new MessageChannelImpl("!") {
                private static final Component CHANNEL_PREFIX = Component.text("[STAFF] ", TextColor.color(0x1FEEFF));
                
                @NotNull
                @Override
                public Component channelPrefix(@NotNull PlayerProfile profile) {
                    return CHANNEL_PREFIX;
                }
                
                @Override
                public boolean isAccessible(@NotNull PlayerProfile profile) {
                    return profile.getRank().isStaff();
                }
                
                @NotNull
                @Override
                public Stream<PlayerProfile> recipients(@NotNull PlayerProfile profile) {
                    return Hariant.getPlayerProfiles().filter(otherProfile -> otherProfile.getRank().isStaff());
                }
            }
    );
    
    private final MessageChannel messageChannel;
    
    EnumMessageChannel(@NotNull MessageChannel messageChannel) {
        this.messageChannel = messageChannel;
    }
    
    @Nullable
    @Override
    public Pattern lookupPattern() {
        return messageChannel.lookupPattern();
    }
    
    @NotNull
    @Override
    public Component channelPrefix(@NotNull PlayerProfile profile) {
        return messageChannel.channelPrefix(profile);
    }
    
    @Override
    public boolean isAccessible(@NotNull PlayerProfile profile) {
        return messageChannel.isAccessible(profile);
    }
    
    @NotNull
    @Override
    public Stream<PlayerProfile> recipients(@NotNull PlayerProfile profile) {
        return messageChannel.recipients(profile);
    }
    
    @NotNull
    public static MessageChannel getChannel(@NotNull PlayerProfile sender, @NotNull TextComponent message) {
        final String text = message.content();
        
        for (EnumMessageChannel messageChannel : values()) {
            final Pattern lookupPattern = messageChannel.lookupPattern();
            
            if (lookupPattern != null && lookupPattern.matcher(text).find() && messageChannel.isAccessible(sender)) {
                return messageChannel;
            }
        }
        
        return EnumMessageChannel.GLOBAL;
    }
    
}
