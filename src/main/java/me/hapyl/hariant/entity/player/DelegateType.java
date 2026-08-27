package me.hapyl.hariant.entity.player;

import me.hapyl.hariant.entity.damage.AssistSource;
import me.hapyl.hariant.util.Cancellable;

/**
 * Represents a delegate type for a {@link Delegatable#delegate(Cancellable, DelegateType)}.
 */
public enum DelegateType {
    
    /**
     * The delegate can be interrupted via {@link HariantPlayer#interrupt(AssistSource)}, which will cancel the {@link Cancellable}.
     */
    INTERRUPTABLE,
    
    /**
     * The delegate can only be interrupted by entity death.
     */
    PERSISTENT
    
}
