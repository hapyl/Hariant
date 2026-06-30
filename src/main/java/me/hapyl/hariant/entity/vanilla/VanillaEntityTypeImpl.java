package me.hapyl.hariant.entity.vanilla;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class VanillaEntityTypeImpl<E extends LivingEntity> implements VanillaEntityType<E> {
    
    static final Map<Key, VanillaEntityType<?>> VALUES = Maps.newHashMap();
    
    private final Key key;
    private final Class<E> entityClass;
    private final Consumer<E> consumer;
    private final Function<E, VanillaEntity<? extends E>> entityFactory;
    
    VanillaEntityTypeImpl(@NotNull Key key, @NotNull Class<E> entityClass, @NotNull Consumer<E> consumer, @NotNull Function<E, VanillaEntity<? extends E>> entityFactory) {
        this.key = key;
        this.entityClass = entityClass;
        this.entityFactory = entityFactory;
        this.consumer = consumer;
        
        VALUES.put(key, this);
    }
    
    @Override
    public @NotNull Key getKey() {
        return key;
    }
    
    @Override
    public @NotNull Class<E> getEntityClass() {
        return entityClass;
    }
    
    @Override
    public @NotNull VanillaEntity<? extends LivingEntity> spawn(@NotNull Location location) {
        return entityFactory.apply(location.getWorld().spawn(location, entityClass, consumer));
    }
    
}
