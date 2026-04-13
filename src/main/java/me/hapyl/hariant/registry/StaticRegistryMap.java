package me.hapyl.hariant.registry;

import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.eterna.module.registry.SimpleRegistry;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class StaticRegistryMap<K extends Keyed> extends SimpleRegistry<K> {
    
    @NotNull
    @Override
    public K register(@NotNull K k) {
        final K register = super.register(k);
        
        if (register instanceof Registrable registrable) {
            registrable.onRegister();
        }
        
        return register;
    }
    
    @Override
    public boolean unregister(@NotNull K k) {
        if (super.unregister(k)) {
            if (k instanceof Registrable registrable) {
                registrable.onUnregister();
            }
            
            return true;
        }
        
        return false;
    }
    
    @NotNull
    public Optional<K> getFromDocument(@NotNull Document document, @NotNull String key) {
        final String stringKey = document.getString(key);
        
        return stringKey != null ? get(stringKey) : Optional.empty();
    }
    
}
