package me.hapyl.hariant.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

public abstract class HariantEvent extends Event {
    
    /**
     * Calls this {@link Event} and gets the cancel status, where {@code true} if the event
     * was cancelled, {@code false} otherwise.
     *
     * @return {@code true} if this event is {@link Cancellable} and it was cancelled; {@code false} otherwise.
     */
    @Override
    public boolean callEvent() {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.callEvent(this);
        
        return this instanceof Cancellable cancellable && cancellable.isCancelled();
    }
    
}
