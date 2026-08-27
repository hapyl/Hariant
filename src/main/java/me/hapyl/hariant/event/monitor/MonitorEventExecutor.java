package me.hapyl.hariant.event.monitor;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

public final class MonitorEventExecutor<E extends Event> implements EventExecutor {
    
    // We have to have a Listener reference for bukkit to register the event executor, a singleton works
    static final Listener LISTENER = new Listener() {};
    
    private final MonitorEventListener<E> listener;
    
    MonitorEventExecutor(@NotNull MonitorEventListener<E> listener) {
        this.listener = listener;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) throws EventException {
        this.listener.listen((E) event);
        
        if (event instanceof Cancellable cancellable && cancellable.isCancelled()) {
            throw new IllegalStateException("Monitor listener must not cancel events.");
        }
    }
    
}