package me.hapyl.hariant.entity;

import me.hapyl.hariant.Hariant;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.logging.Logger;

public class EntityGarbageCollector implements Listener {
    
    private static final NamespacedKey GARBAGE_KEY = new NamespacedKey(Hariant.getPlugin(), "garbage_entity");
    private static final String GARBAGE_VALUE = UUID.randomUUID().toString().substring(0, 8);
    
    public EntityGarbageCollector() {
    }
    
    public static void add(@NotNull Entity entity) {
        entity.getPersistentDataContainer().set(GARBAGE_KEY, PersistentDataType.STRING, GARBAGE_VALUE);
    }
    
    public static void add(@NotNull HariantEntity entity) {
        entity.listGarbage().forEach(EntityGarbageCollector::add);
    }
    
    private static boolean isGarbageEntity(@NotNull Entity entity) {
        final PersistentDataContainer persistentDataContainer = entity.getPersistentDataContainer();
        final String value = persistentDataContainer.get(GARBAGE_KEY, PersistentDataType.STRING);
        
        return value != null && !value.equals(GARBAGE_VALUE);
    }
    
    @EventHandler
    public void handleChunkLoadEvent(ChunkLoadEvent ev) {
        final Chunk chunk = ev.getChunk();
        int removedEntities = 0;
        
        for (Entity entity : chunk.getEntities()) {
            if (isGarbageEntity(entity)) {
                entity.remove();
                removedEntities++;
            }
        }
        
        if (removedEntities == 0) {
            return;
        }
        
        final Logger logger = Hariant.getPlugin().getLogger();
        
        logger.info("Removed %s garbage entities in chunk [%s, %s]!".formatted(removedEntities, chunk.getX(), chunk.getZ()));
    }
    
}