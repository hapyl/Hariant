package me.hapyl.hariant.profile.message;

import me.hapyl.hariant.database.rank.RankFormatter;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.team.EnumTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public final class MessageChannelTeam extends MessageChannelImpl {
    
    private final EnumTeam team;
    
    MessageChannelTeam(@NotNull EnumTeam team) {
        super(createPrefix(team));
        
        this.team = team;
    }
    
    @Override
    public boolean isAccessible(@NotNull PlayerProfile profile) {
        return profile.getTeam() == team;
    }
    
    @Override
    public @NotNull Stream<PlayerProfile> recipients() {
        return team.getPlayerProfiles();
    }
    
    private static @NotNull Component createPrefix(@NotNull EnumTeam team) {
        final Style style = team.getStyle();
        
        return Component.empty()
                        .append(Component.text("[", style))
                        .append(team.asComponent())
                        .append(Component.text("]", style))
                        .appendSpace();
    }
    
}