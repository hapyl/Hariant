package me.hapyl.hariant.game.battleground.clouds;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.hariant.entity.SitHandler;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.game.booster.Booster;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.ImmutableLocation;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public final class CloudsBooster implements Booster {
    
    private static final int BOOST_DURATION = Tick.fromSeconds(2);
    
    private final ImmutableLocation from;
    private final ImmutableLocation to;
    private final double height;
    
    CloudsBooster(@NotNull ImmutableLocation from, @NotNull ImmutableLocation to, double height) {
        this.from = from;
        this.to = to;
        this.height = height;
    }
    
    CloudsBooster(@NotNull ImmutableLocation from, @NotNull ImmutableLocation to) {
        this(from, to, Math.PI);
    }
    
    @Override
    public @NotNull ImmutableLocation getLocation() {
        return from;
    }
    
    @Override
    public void boost(@NotNull HariantPlayer player) {
        player.delegate(new CloudsBoosterTask(player, this), DelegateType.PERSISTENT);
        
        // Fx
        player.playWorldSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0f);
    }
    
    private static class CloudsBoosterTask extends HariantTickingTask {
        
        private final HariantPlayer player;
        private final CloudsBooster cloudsBooster;
        private final SitHandler sitHandler;
        
        CloudsBoosterTask(@NotNull HariantPlayer player, @NotNull CloudsBooster cloudsBooster) {
            super(Scheduler.ofTimer());
            
            this.player = player;
            this.cloudsBooster = cloudsBooster;
            this.sitHandler = player.setSitting(player.getCenterLocation(), false);
        }
        
        @Override
        public void run(int tick) {
            if (tick > BOOST_DURATION) {
                this.cancel();
                return;
            }
            
            final double progress = (double) tick / BOOST_DURATION;
            final double progressEased = progress < 0.5 ? 2 * progress * progress : -2 * progress * progress + 4 * progress - 1;
            
            final double angle = Math.sin(Math.PI * progress) * cloudsBooster.height;
            
            // Calculate the location and teleport the armor stand to it
            final double x = (cloudsBooster.to.x() - cloudsBooster.from.x()) * progressEased + 0.5;
            final double y = (cloudsBooster.to.y() - cloudsBooster.from.y()) * progressEased + angle;
            final double z = (cloudsBooster.to.z() - cloudsBooster.from.z()) * progressEased + 0.5;
            
            sitHandler.move(cloudsBooster.from.toLocation(cloudsBooster.from.getWorld()).add(x, y, z));
        }
        
        @Override
        public void onCancel() {
            player.unsetSitting();
        }
        
    }
    
}