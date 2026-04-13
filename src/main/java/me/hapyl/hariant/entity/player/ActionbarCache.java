package me.hapyl.hariant.entity.player;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.Streamable;
import me.hapyl.eterna.module.util.cache.Cache;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Stream;

public class ActionbarCache implements Streamable<Component> {
    
    private static final long DURATION = 1_500;
    
    private final Map<Class<?>, Entry> cache;
    
    public ActionbarCache() {
        this.cache = Maps.newHashMap();
    }
    
    public void add(@NotNull Class<?> origin, @NotNull Component component) {
        this.cache.put(origin, new Entry(component));
    }
    
    @NotNull
    @Override
    public Stream<Component> stream() {
        // Actually call removal, don't filter
        this.cache.values().removeIf(Entry::isExpired);
        
        return this.cache.isEmpty() ? Stream.empty() : this.cache.values().stream().map(Entry::getComponent);
    }
    
    private static class Entry implements Cache.Entry {
        
        private final Component component;
        private final long createdAt;
        
        Entry(@NotNull Component component) {
            this.component = component;
            this.createdAt = System.currentTimeMillis();
        }
        
        @NotNull
        public Component getComponent() {
            return component;
        }
        
        @Override
        public boolean isExpired() {
            return System.currentTimeMillis() - createdAt >= DURATION;
        }
    }
    
}
