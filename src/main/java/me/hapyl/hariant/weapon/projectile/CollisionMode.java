package me.hapyl.hariant.weapon.projectile;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public interface CollisionMode {
    
    @NotNull
    CollisionMode DEFAULT = new CollisionMode() {
        @Override
        public boolean canPassThrough(@NotNull Block block) {
            return !block.getType().isOccluding();
        }
    };
    
    boolean canPassThrough(@NotNull Block block);
    
}
