package me.hapyl.hariant.entity.damage;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents a death message that will be shown upon players death.
 * <p>
 * Any death message must follow the specific pattern, which includes:
 *     <ul>
 *         <li>{@code {player}} tag that will be replaced with the player's name.
 *         <li>Optional killer part, which is wrapped in between {@code []} and must contain {@code {killer}}.
 *     </ul>
 * </p>
 */
public interface DeathMessage {
    
    @NotNull
    DeathMessage DEFAULT = create("{player} was killed [by {killer}]");
    
    @NotNull
    Component deathMessage(@NotNull DeathComponent player, @Nullable DeathComponent killer, @NotNull List<? extends DeathComponent> assists);
    
    @NotNull
    static DeathMessage create(@NotNull String template) {
        return new DeathMessageImpl(template);
    }
    
    @NotNull
    static DeathMessage createWithDefaultKiller(@NotNull String base) throws IllegalArgumentException {
        
        if (base.contains(DeathMessageImpl.PLACEHOLDER_KILLER)) {
            throw new IllegalArgumentException("Death message must not already contain %s!".formatted(DeathMessageImpl.PLACEHOLDER_KILLER));
        }
        
        return create(base + " " + DeathMessageImpl.DEFAULT_KILLER_SUFFIX);
    }
    
}
