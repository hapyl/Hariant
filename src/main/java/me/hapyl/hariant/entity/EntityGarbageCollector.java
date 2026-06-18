package me.hapyl.hariant.entity;

import me.hapyl.hariant.Hariant;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

// FIXME (xanyjl @ Sunday, May 24) -> This doesn't work for some reason?
public class EntityGarbageCollector implements Listener {
    
    private static final String GARBAGE_ENTITY_TAG = "__garbage_entity__";
    
    public static void add(@NotNull Entity entity) {
        entity.addScoreboardTag(GARBAGE_ENTITY_TAG);
    }
    
    public static void add(@NotNull HariantEntity entity) {
        entity.listGarbage().forEach(EntityGarbageCollector::add);
    }
    
    public static void clearGarbage() {
        int entitiesRemoved = 0;
        
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (isGarbageEntity(entity)) {
                    entity.remove();
                    entitiesRemoved++;
                }
            }
        }
        
        final Logger logger = Hariant.getPlugin().getLogger();
        
        if (entitiesRemoved == 0) {
            logger.info("No garbage entities detected.");
        }
        else {
            logger.info("Removed %s garbage entities!".formatted(entitiesRemoved));
        }
    }
    
    private static boolean isGarbageEntity(@NotNull Entity entity) {
        return entity.getScoreboardTags().contains(GARBAGE_ENTITY_TAG);
    }
    
}