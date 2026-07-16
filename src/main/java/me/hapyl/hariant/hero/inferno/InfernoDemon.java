package me.hapyl.hariant.hero.inferno;

import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.math.geometry.Drawable;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.Pet;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.util.TickDuration;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface InfernoDemon extends Pet, Removable, TickDuration {
    
    @NotNull
    InfernoDemonType getDemonType();
    
    @Override
    @NotNull
    HariantEntity owner();
    
    void onForm(@NotNull HariantPlayer player, @NotNull HeroDataInferno data);
    
    void onReform(@NotNull HariantPlayer player, @NotNull HeroDataInferno data);
    
    @Override
    int currentTick();
    
    @Override
    int duration();
    
    @Override
    void remove();
    
    void swingArm();
    
    static void drawParticleBox(@NotNull HariantPlayer player, @NotNull Drawable drawable, double height) {
        class Drawable {
            private static void drawEdge(@NotNull Location location, double x, double y, double z, @NotNull me.hapyl.eterna.module.math.geometry.Drawable drawable) {
                LocationHelper.offset(location, x, y, z, drawable::draw);
            }
        }
        
        final Location location = player.getLocation();
        
        final double width = 1.0;
        final double depth = 1.0;
        final double step = 0.2;
        
        for (double x = -width / 2; x <= width / 2; x += step) {
            Drawable.drawEdge(location, x, 0, -depth / 2, drawable);
            Drawable.drawEdge(location, x, 0, depth / 2, drawable);
            Drawable.drawEdge(location, x, height, -depth / 2, drawable);
            Drawable.drawEdge(location, x, height, depth / 2, drawable);
        }
        
        for (double z = -depth / 2; z <= depth / 2; z += step) {
            Drawable.drawEdge(location, -width / 2, 0, z, drawable);
            Drawable.drawEdge(location, width / 2, 0, z, drawable);
            Drawable.drawEdge(location, -width / 2, height, z, drawable);
            Drawable.drawEdge(location, width / 2, height, z, drawable);
        }
        
        for (double y = 0; y <= height; y += step) {
            Drawable.drawEdge(location, -width / 2, y, -depth / 2, drawable);
            Drawable.drawEdge(location, width / 2, y, -depth / 2, drawable);
            Drawable.drawEdge(location, -width / 2, y, depth / 2, drawable);
            Drawable.drawEdge(location, width / 2, y, depth / 2, drawable);
        }
    }
    
}