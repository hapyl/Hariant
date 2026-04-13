package me.hapyl.hariant.entity;

import me.hapyl.hariant.attribute.instance.Attributes;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface EntitySpawner<H extends HariantEntity> {
    
    @NotNull
    H spawn();
    
    @NotNull
    static <E extends LivingEntity> EntitySpawner<HariantEntity> ofBukkit(@NotNull Location location, @NotNull VanillaEntityType<E> entityType, @NotNull Consumer<E> consumer) {
        return () -> new HariantEntity(entityType.spawn(location, consumer), Attributes.common());
    }
    
}
