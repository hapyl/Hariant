package me.hapyl.hariant.entity;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SitHandlerImpl implements SitHandler {
    
    private static final double Y_OFFSET = 0.2;
    
    private final HariantEntity entity;
    private final Entity sitEntity;
    private final boolean allowDismount;
    
    public SitHandlerImpl(@NotNull HariantEntity entity, @NotNull Location location, boolean allowDismount) {
        this.entity = entity;
        this.sitEntity = createArmorStand(entity, location);
        this.allowDismount = allowDismount;
    }
    
    @Override
    public boolean allowDismount() {
        return allowDismount;
    }
    
    @Override
    public void move(@NotNull Location location) {
        sitEntity.teleport(location);
    }
    
    @Override
    public void onMount() {
    }
    
    @Override
    public void onDismount() {
        sitEntity.remove();
        
        // The entity will be dismounted when the `sitEntity` is removed, but vanilla dismount
        // is annoying, so we forcefully put the entity at the top of the block
        final Location location = entity.getLocation();
        final Block block = location.getBlock();
        
        location.setY(Math.max(location.getY(), block.getBoundingBox().getMaxY()));
        
        entity.teleport(location);
    }
    
    private static @NotNull Entity createArmorStand(@NotNull HariantEntity player, @NotNull Location location) {
        return player.getWorld().spawn(location.add(0, Y_OFFSET, 0), ArmorStand.class, self -> {
            self.setInvisible(true);
            self.setMarker(true);
            self.setSilent(true);
            
            Objects.requireNonNull(self.getAttribute(Attribute.SCALE)).setBaseValue(0.1);
            EntityGarbageCollector.add(self);
            
            self.addPassenger(player.getHandle());
        });
    }
    
}