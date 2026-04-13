package me.hapyl.hariant.entity;

import com.google.common.collect.Maps;
import org.bukkit.Location;
import org.bukkit.entity.Husk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

// FIXME @Mar 01, 2026 (xanyjl) ->
public interface VanillaEntityType<E extends LivingEntity> {
    
    @NotNull VanillaEntityType<Husk> HUSK = create("husk", Husk.class);
    @NotNull VanillaEntityType<Sheep> SHEEP = create("sheep", Sheep.class);
    
    @NotNull
    String getName();
    
    @NotNull
    Class<E> getEntityClass();
    
    @NotNull
    default E spawn(@NotNull Location location, @NotNull Consumer<E> consumer) {
        return location.getWorld().spawn(location, getEntityClass(), consumer);
    }
    
    @Nullable
    static VanillaEntityType<? extends LivingEntity> byName(@NotNull String name) {
        return VanillaEntityTypeImpl.values.get(name.toLowerCase());
    }
    
    @NotNull
    private static <E extends LivingEntity> VanillaEntityType<E> create(@NotNull String name, @NotNull Class<E> enityClass) {
        return new VanillaEntityTypeImpl<>(name, enityClass);
    }
    
    class VanillaEntityTypeImpl<E extends LivingEntity> implements VanillaEntityType<E> {
        
        private static final Map<String, VanillaEntityType<?>> values = Maps.newHashMap();
        
        private final String name;
        private final Class<E> clazz;
        
        VanillaEntityTypeImpl(@NotNull String name, @NotNull Class<E> clazz) {
            this.name = name;
            this.clazz = clazz;
            
            values.put(name, this);
        }
        
        @NotNull
        @Override
        public String getName() {
            return name;
        }
        
        @NotNull
        @Override
        public Class<E> getEntityClass() {
            return clazz;
        }
        
    }
    
}
