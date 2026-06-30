package me.hapyl.hariant.entity.vanilla;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.eterna.module.util.StringList;
import org.bukkit.Location;
import org.bukkit.entity.Husk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Parched;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public interface VanillaEntityType<E extends LivingEntity> extends Keyed {
    
    @NotNull VanillaEntityType<Husk> HUSK = create(
            Key.ofString("husk"),
            Husk.class,
            Husk::setAdult,
            VanillaEntityTypeHusk::new
    );
    
    @NotNull VanillaEntityType<Parched> PARCHED = create(
            Key.ofString("parched"),
            Parched.class,
            VanillaEntityParched::new
    );
    
    @Override
    @NotNull Key getKey();
    
    @NotNull Class<E> getEntityClass();
    
    @NotNull VanillaEntity<? extends LivingEntity> spawn(@NotNull Location location);
    
    static @Nullable VanillaEntityType<? extends LivingEntity> byKey(@NotNull Key key) {
        return VanillaEntityTypeImpl.VALUES.get(key);
    }
    
    @NotNull
    static StringList listKeys() {
        return StringList.of(VanillaEntityTypeImpl.VALUES.keySet().stream().map(Key::toString).toList());
    }
    
    static @NotNull <E extends LivingEntity> VanillaEntityType<E> create(@NotNull Key key, @NotNull Class<E> entityClass, @NotNull Consumer<E> consumer, @NotNull Function<E, VanillaEntity<? extends E>> entityFactory) {
        return new VanillaEntityTypeImpl<>(key, entityClass, consumer, entityFactory);
    }
    
    static @NotNull <E extends LivingEntity> VanillaEntityType<E> create(@NotNull Key key, @NotNull Class<E> entityClass, @NotNull Function<E, VanillaEntity<? extends E>> entityFactory) {
        return create(key, entityClass, _ -> {}, entityFactory);
    }
    
}
