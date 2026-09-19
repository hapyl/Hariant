package me.hapyl.hariant.registry;

import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.eterna.module.registry.SimpleRegistry;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class StaticRegistryMap<K extends Keyed & Registrable> extends SimpleRegistry<K> {
    
    @Override
    public @NotNull K register(@NotNull K k) {
        final K register = super.register(k);
        k.onRegister();
        
        return register;
    }
    
    @Override
    public final boolean unregister(@NotNull K k) {
        throw new UnsupportedOperationException("Unregistering is not supported");
    }
    
    public @NotNull Optional<@NotNull K> getFromDocument(@NotNull Document document, @NotNull String key) {
        final String stringKey = document.getString(key);
        
        return stringKey != null ? get(stringKey) : Optional.empty();
    }
    
}
