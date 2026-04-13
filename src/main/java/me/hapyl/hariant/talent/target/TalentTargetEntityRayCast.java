package me.hapyl.hariant.talent.target;

import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public final class TalentTargetEntityRayCast implements TalentTarget {
    
    private final double maxDistance;
    private final double lookupRadius;
    private final Predicate<HariantEntity> filter;
    
    TalentTargetEntityRayCast(double maxDistance, double lookupRadius, @NotNull Predicate<HariantEntity> filter) {
        this.maxDistance = maxDistance;
        this.lookupRadius = lookupRadius;
        this.filter = filter;
    }
    
    @Nullable
    @Override
    public TalentContext createContext(@NotNull HariantPlayer player) {
        final Location location = player.getEyeLocation();
        final World world = location.getWorld();
        final Vector vector = location.getDirection().normalize();
        
        for (double d = 0; d < maxDistance; d += 0.5) {
            final double x = vector.getX() * d;
            final double y = vector.getY() * d;
            final double z = vector.getZ() * d;
            
            location.add(x, y, z);
            
            // Don't really care about `lookupRadius` being accurate
            final HariantEntity entity = world.getNearbyEntities(location, lookupRadius, lookupRadius, lookupRadius)
                                              .stream()
                                              .map(Hariant::getEntityOrNull)
                                              .filter(_entity -> _entity != null && filter.test(_entity))
                                              .findFirst()
                                              .orElse(null);
            
            if (entity != null) {
                return TalentContext.of(entity);
            }
            
            location.subtract(x, y, z);
        }
        
        return null;
    }
    
    @NotNull
    @Override
    public Component errorMessage() {
        return Component.text("No valid target!", NamedTextColor.RED);
    }
    
}
