package me.hapyl.hariant.profile.message;

import me.hapyl.hariant.Colors;
import me.hapyl.hariant.database.rank.RankFormatter;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class MessageChannelStaff extends MessageChannelImpl {
    
    MessageChannelStaff() {
        super(Component.text("[STAFF] ", Colors.STAFF));
    }
    
    @Override
    public boolean isAccessible(@NotNull PlayerProfile profile) {
        return profile.getRank().isStaff();
    }
    
}