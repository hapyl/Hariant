package me.hapyl.hariant.entity;

import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public enum Outline {
    
    CLEAR {
        @Override
        public void setOutline(@NotNull Player player) {
            player.setWorldBorder(player.getWorld().getWorldBorder());
        }
    },
    
    RED {
        private static final WorldBorder WORLD_BORDER;
        
        static {
            WORLD_BORDER = Bukkit.createWorldBorder();
            WORLD_BORDER.setWarningDistance((int) WORLD_BORDER.getSize());
        }
        
        @Override
        public void setOutline(@NotNull Player player) {
            player.setWorldBorder(WORLD_BORDER);
        }
    };
    
    public void setOutline(@NotNull Player player) {
    }
    
    
}
