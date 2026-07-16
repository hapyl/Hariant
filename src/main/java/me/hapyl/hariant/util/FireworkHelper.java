package me.hapyl.hariant.util;

import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;

public final class FireworkHelper {
    
    private FireworkHelper() {
    }
    
    @NotNull
    public static Firework spawn(@NotNull Location location, @NotNull FireworkEdit edit) {
        return location.getWorld().spawn(location, Firework.class, self -> {
            final FireworkMeta fireworkMeta = self.getFireworkMeta();
            
            edit.edit(fireworkMeta);
            self.setFireworkMeta(fireworkMeta);
        });
    }
    
    @NotNull
    public static Firework explode(@NotNull Location location, @NotNull FireworkEdit edit) {
        final Firework firework = spawn(location, edit);
        firework.detonate();
        return firework;
    }
    
    public interface FireworkEdit {
        
        void edit(@NotNull FireworkMeta meta);
        
    }
    
}
