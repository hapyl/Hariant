package me.hapyl.hariant.entity;

import me.hapyl.hariant.entity.player.HariantPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Defines a rule for {@link HariantPlayer#streamPlayers(StreamRules)}.
 */
public interface StreamRules {
    
    /**
     * A static constant for a stream rule that only includes the player themselves.
     */
    @NotNull StreamRules SELF = create(true, false, false);
    
    /**
     * A static constant for a stream rule that only includes the teammates.
     */
    @NotNull StreamRules TEAMMATES = create(false, true, false);
    
    /**
     * A static constant for a stream rule that only includes other players.
     */
    @NotNull StreamRules OTHERS = create(false, false, true);
    
    /**
     * A static constant for a stream rule that includes teammates and other players, but not the player themselves.
     */
    @NotNull StreamRules NOT_SELF = create(false, true, true);
    
    /**
     * A static constant for a stream rule that includes the player themselves and other players, but not teammates.
     */
    @NotNull StreamRules NOT_TEAMMATES = create(true, false, true);
    
    /**
     * A static constant for a stream rule that includes the player themselves and teammates, but not other players.
     */
    @NotNull StreamRules NOT_OTHERS = create(true, true, false);
    
    /**
     * A static constant for stream rule that includes everyone.
     */
    @NotNull StreamRules ALL = create(true, true, true);
    
    /**
     * Gets whether to include the player themselves.
     *
     * @return {@code true} to include the player themselves.
     */
    boolean includeSelf();
    
    /**
     * Gets whether to include teammates.
     *
     * @return {@code true} to include the teammates.
     */
    boolean includeTeammates();
    
    /**
     * Gets whether to include other players.
     *
     * @return {@code true} to include other players.
     */
    boolean includeOthers();
    
    private static @NotNull StreamRules create(boolean includeSelf, boolean includeTeammates, boolean includeOthers) {
        return new StreamRulesImpl(includeSelf, includeTeammates, includeOthers);
    }
    
}
