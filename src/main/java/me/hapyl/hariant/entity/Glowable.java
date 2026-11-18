package me.hapyl.hariant.entity;

import me.hapyl.eterna.module.reflect.team.PacketTeamColor;
import me.hapyl.hariant.entity.player.HariantPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface Glowable {
    
    /**
     * Makes this entity glow for the given {@link HariantPlayer}.
     *
     * <p>
     * If this entity is already glowing for the given player, only the color will change.
     * </p>
     *
     * @param player - The player for whom to glow.
     * @param color  - The glowing color.
     * @throws IllegalArgumentException if attempted to glow entity for themselves
     */
    default void setGlowing(@NotNull HariantPlayer player, @NotNull PacketTeamColor color) throws IllegalArgumentException {
        this.setGlowing(player.getHandle(), color);
    }
    
    /**
     * Makes this entity glow for the given {@link Player}.
     *
     * <p>
     * If this entity is already glowing for the given player, only the color will change.
     * </p>
     *
     * @param player - The player for whom to glow.
     * @param color  - The glowing color.
     * @throws IllegalArgumentException if attempted to glow entity for themselves
     */
    void setGlowing(@NotNull Player player, @NotNull PacketTeamColor color) throws IllegalArgumentException;
    
    /**
     * Stops this entity from glowing for the given {@link HariantPlayer}.
     *
     * @param player - The player for whom to stop glowing.
     */
    default void stopGlowing(@NotNull HariantPlayer player) {
        this.stopGlowing(player.getHandle());
    }
    
    /**
     * Stops this entity from glowing for the given {@link Player}.
     *
     * @param player - The player for whom to stop glowing.
     */
    void stopGlowing(@NotNull Player player);
    
}