package me.hapyl.hariant.profile.message;

import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.team.EnumTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public enum EnumMessageChannel implements MessageChannel {
    
    GLOBAL(null, new MessageChannelImpl(Component.empty())),
    TEAM_RED(lookupPatternTeam(), new MessageChannelTeam(EnumTeam.RED)),
    TEAM_GREEN(lookupPatternTeam(), new MessageChannelTeam(EnumTeam.GREEN)),
    TEAM_BLUE(lookupPatternTeam(), new MessageChannelTeam(EnumTeam.BLUE)),
    TEAM_ORANGE(lookupPatternTeam(), new MessageChannelTeam(EnumTeam.ORANGE)),
    TEAM_PURPLE(lookupPatternTeam(), new MessageChannelTeam(EnumTeam.PURPLE)),
    TEAM_WHITE(lookupPatternTeam(), new MessageChannelTeam(EnumTeam.WHITE)),
    TEAM_BLACK(lookupPatternTeam(), new MessageChannelTeam(EnumTeam.BLACK)),
    STAFF(lookupPattern("!"), new MessageChannelStaff());
    
    private final @Nullable Pattern lookupPattern;
    private final @NotNull MessageChannel messageChannel;
    
    EnumMessageChannel(@Nullable Pattern lookupPattern, @NotNull MessageChannel messageChannel) {
        this.lookupPattern = lookupPattern;
        this.messageChannel = messageChannel;
    }
    
    public @Nullable Pattern lookupPattern() {
        return lookupPattern;
    }
    
    @Override
    public @NotNull Component formatProfile(@NotNull PlayerProfile profile) {
        return messageChannel.formatProfile(profile);
    }
    
    @Override
    public @NotNull Component channelPrefix() {
        return messageChannel.channelPrefix();
    }
    
    @Override
    public boolean isAccessible(@NotNull PlayerProfile profile) {
        return messageChannel.isAccessible(profile);
    }
    
    @Override
    public @NotNull Stream<PlayerProfile> recipients() {
        return messageChannel.recipients();
    }
    
    public static @NotNull EnumMessageChannel fromMessage(@NotNull PlayerProfile profile, @NotNull TextComponent message) {
        final String text = message.content();
        
        for (EnumMessageChannel messageChannel : values()) {
            final Pattern lookupPattern = messageChannel.lookupPattern;
            
            if (lookupPattern != null && lookupPattern.matcher(text).find() && messageChannel.isAccessible(profile)) {
                return messageChannel;
            }
        }
        
        return EnumMessageChannel.GLOBAL;
    }
    
    private static @NotNull Pattern lookupPattern(@NotNull String ch) {
        return Pattern.compile("^%s\\s*".formatted(Pattern.quote(ch)));
    }
    
    private static @NotNull Pattern lookupPatternTeam() {
        return lookupPattern("#");
    }
    
}
