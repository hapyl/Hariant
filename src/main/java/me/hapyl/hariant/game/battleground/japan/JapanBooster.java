package me.hapyl.hariant.game.battleground.japan;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.game.booster.Booster;
import me.hapyl.hariant.task.HariantTask;
import me.hapyl.hariant.util.ImmutableLocation;
import org.bukkit.Sound;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public final class JapanBooster implements Booster {
    
    private final ImmutableLocation location;
    private final double verticalMagnitude;
    private final double horizontalMagnitude;
    private final int horizontalDelay;
    
    JapanBooster(@NotNull ImmutableLocation location, double verticalMagnitude, double horizontalMagnitude, int horizontalDelay) {
        this.location = location;
        this.verticalMagnitude = verticalMagnitude;
        this.horizontalMagnitude = horizontalMagnitude;
        this.horizontalDelay = horizontalDelay;
    }
    
    @Override
    public @NotNull ImmutableLocation getLocation() {
        return location;
    }
    
    @Override
    public void boost(@NotNull HariantPlayer player) {
        player.setVelocity(new Vector(0, verticalMagnitude, 0));
        player.playWorldSound(Sound.ENTITY_WITHER_SHOOT, 0.75f);
        
        player.delegate(HariantTask.later(() -> {
            final Vector vector = player.getLocation().getDirection().setY(0).normalize();
            vector.multiply(horizontalMagnitude);
            
            player.setVelocity(vector);
            player.playWorldSound(Sound.ENTITY_WITHER_SHOOT, 1.25f);
        }, horizontalDelay));
    }
    
}