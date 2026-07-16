package me.hapyl.hariant.profile.message;

import me.hapyl.hariant.Colors;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public interface MessageChannel {
    
    @NotNull Component channelPrefix();
    
    boolean isAccessible(@NotNull PlayerProfile profile);
    
    @NotNull Stream<PlayerProfile> recipients();
    
    @NotNull Component formatProfile(@NotNull PlayerProfile profile);
    
    @ApiStatus.NonExtendable
    default void message(@Nullable PlayerProfile profile, @NotNull Component message) {
        final TextComponent.Builder builder = Component.text();
        
        // Append prefix
        builder.append(this.channelPrefix());
        
        // If profile exists, append the profile
        if (profile != null) {
            builder.append(this.formatProfile(profile));
            builder.append(Component.text(":", Colors.GRAY));
            builder.appendSpace();
        }
        
        // Actually append the message
        builder.append(message);
        
        final TextComponent component = builder.build();
        
        this.recipients().forEach(recipient -> recipient.receiveMessage(this, profile, component));
    }
    
    @ApiStatus.NonExtendable
    default void message(@NotNull Component component) {
        this.message(null, component);
    }
    
}