package me.hapyl.hariant.entity;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.entity.damage.DamageSource;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class HariantMarkerEntity extends HariantEntity {
    
    public HariantMarkerEntity(@NotNull Location location) {
        super(createMarker(location), Attributes.zero());
    }
    
    @Override
    public void onDestroy() {
        entity.remove();
    }
    
    @Override
    public boolean isImmuneTo(@NotNull DamageSource source) {
        return true;
    }
    
    private static LivingEntity createMarker(@NotNull Location location) {
        // Can't use marker because it's not a living entity
        return Entities.ARMOR_STAND.spawn(location, self -> {
            self.setSmall(true);
            self.setInvisible(true);
            self.setMarker(true);
            self.setInvulnerable(true);
            self.setSilent(true);
        });
    }
}
