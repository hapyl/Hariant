package me.hapyl.hariant.database.rank;

import me.hapyl.eterna.module.component.builder.ComponentBuilder;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.util.Prefixed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.object.ObjectContents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    default Component format(@NotNull PlayerProfile profile) {
        class Holder {
            private static final Part[] DEFAULT_PARTS = { Part.PLAYER_HEAD, Part.LEVEL, Part.PREFIX, Part.PLAYER_NAME, Part.SUFFIX };
        }
        
        return this.format(profile, Holder.DEFAULT_PARTS);
    }
    
    @NotNull
    default Component format(@NotNull PlayerProfile profile, @NotNull Part... parts) {
        if (parts.length == 0) {
            throw new IllegalArgumentException("There must be at least one part to format!");
        }
        
        final TextComponent.Builder component = Component.text();
        
        if (CollectionUtils.contains(parts, Part.PLAYER_HEAD)) {
            component.append(profile.asHeadComponent());
            component.appendSpace();
        }
        
        if (CollectionUtils.contains(parts, Part.LEVEL)) {
            component.append(profile.getDatabase().level.asComponent());
            component.appendSpace();
        }
        
        if (CollectionUtils.contains(parts, Part.PREFIX)) {
            final Component prefix = this.getPrefix();
            
            // Skip empty prefixes
            if (Component.IS_NOT_EMPTY.test(prefix)) {
                component.append(prefix);
                component.appendSpace();
            }
        }
        
        if (CollectionUtils.contains(parts, Part.PLAYER_NAME)) {
            component.append(profile.getPlayer().name().color(this.getNameColor()));
        }
        
        // TODO @Feb 16, 2026 (xanyjl) -> Implement suffxies
        
        return component.build();
    }
    
    enum Part {
        PLAYER_HEAD,
        LEVEL,
        PREFIX,
        PLAYER_NAME,
        SUFFIX
    }
    
}
