package me.hapyl.hariant.event.monitor;

import me.hapyl.hariant.Hariant;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

/**
 * A functional interface for listening to a {@code non-cancelled} event with a {@link EventPriority#MONITOR} priority.
 *
 * @param <E> - The event to listen to.
 */
@FunctionalInterface
public interface MonitorEventListener<E extends Event> {
    
    /**
     * A callback method that listens to the given {@link E}.
     *
     * @param event - The executed event.
     */
    void listen(@NotNull E event);
    
    /**
     * Creates a new listener for the given {@link E}; the event is always executed under {@link EventPriority#MONITOR} priority and will not fire if cancelled.
     *
     * @param eventClass - The event class.
     * @param listener   - The listener for the event.
     * @param <E>        - The event type.
     */
    static <E extends Event> void listener(@NotNull Class<E> eventClass, @NotNull MonitorEventListener<E> listener) {
        Bukkit.getPluginManager().registerEvent(eventClass, MonitorEventExecutor.LISTENER, EventPriority.MONITOR, new MonitorEventExecutor<>(listener), Hariant.getPlugin(), true);
    }
    
}
