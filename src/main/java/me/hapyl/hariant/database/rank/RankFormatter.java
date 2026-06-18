package me.hapyl.hariant.database.rank;

import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.util.Prefixed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

public interface RankFormatter extends Prefixed {
    
    @Override
    @NotNull
    Component getPrefix();
    
    // We're limited by teams so have to use the vanilla color, you know who to blame ¯\_(ツ)_/¯
    @NotNull
    NamedTextColor getNameColor();
    
    @NotNull
    Style getMessageStyle();
    
    default boolean displayJoinMessages() {
        return true;
    }
    
    @NotNull
    default Component format(@NotNull PlayerProfile profile, @NotNull FormatRules formatRules) {
        final TextComponent.Builder builder = Component.text();
        
        if (formatRules.playerHead()) {
            builder.append(profile.asHeadComponent());
            builder.appendSpace();
        }
        
        if (formatRules.level()) {
            builder.append(profile.getDatabase().level.asComponent());
            builder.appendSpace();
        }
        
        if (formatRules.prefix()) {
            final Component prefix = this.getPrefix();
            
            // Skip empty prefixes
            if (Component.IS_NOT_EMPTY.test(prefix)) {
                builder.append(prefix);
                builder.appendSpace();
            }
        }
        
        if (formatRules.playerName()) {
            builder.append(profile.getPlayer().name().color(this.getNameColor()));
        }
        
        // TODO @Feb 16, 2026 (xanyjl) -> Implement suffixes
        
        return builder.build();
    }
    
}
