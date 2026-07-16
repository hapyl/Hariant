package me.hapyl.hariant.hero.inferno;

import me.hapyl.eterna.module.annotate.DefensiveCopy;
import me.hapyl.eterna.module.location.Located;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.util.Disposable;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.entity.player.HariantPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

public class InfernoFire implements Located, Disposable {
    
    private static final BlockData FIRE_BLOCK_DATA = Material.SOUL_FIRE.createBlockData();
    
    private final HariantPlayer player;
    private final Location location;
    private final BoundingBox boundingBox;
    
    InfernoFire(@NotNull HariantPlayer player, @NotNull @DefensiveCopy Location location) {
        this.player = player;
        this.location = LocationHelper.copyOf(location);
        this.boundingBox = LocationHelper.toBoundingBox(location, 0.5, 0.8, 0.5);
    }
    
    @Override
    public @NotNull Location getLocation() {
        return location;
    }
    
    public void light() {
        Hariant.globalBlockChange(location, FIRE_BLOCK_DATA);
        
        // Fx
        player.playWorldSound(location, Sound.ITEM_FIRECHARGE_USE, 0.25f, 1.0f);
    }
    
    @Override
    public void dispose() {
        Hariant.globalBlockChange(location);
    }
    
    @NotNull
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
}