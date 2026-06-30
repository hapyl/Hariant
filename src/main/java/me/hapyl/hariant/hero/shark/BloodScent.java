package me.hapyl.hariant.hero.shark;

import com.google.common.collect.Lists;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.task.HariantTickingStepTask;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

public class BloodScent extends HariantTickingTask {
    
    private static final int TPS = 10;
    private static final int TICK_THRESHOLD = 100;
    
    private static final double DISTANCE_THRESHOLD_SQUARED = 0.75 * 0.75;
    private static final Particle.DustTransition DUST_TRANSITION = new Particle.DustTransition(Color.fromRGB(136, 8, 8), Color.fromRGB(152, 0, 2), 1);
    
    private final HariantEntity entity;
    private final Queue<Scent> scents;
    
    public BloodScent(@NotNull HariantEntity entity) {
        super(Scheduler.ofTimer());
        
        this.entity = entity;
        this.scents = Lists.newLinkedList();
    }
    
    @Override
    public void run(int tick) {
        if (tick % TPS != 0) {
            // Remove all scents that are older than the threshold
            scents.removeIf(scent -> tick - scent.tick > TICK_THRESHOLD);
            
            final Location location = entity.getLocation().add(0, 0.2, 0);
            final Scent previousScent = scents.peek();
            
            // If previous scent is close to the current location, skip
            if (previousScent == null || previousScent.location.distanceSquared(location) > DISTANCE_THRESHOLD_SQUARED) {
                scents.offer(new Scent(location, tick));
            }
            else {
            }
            
            // Draw blood using another task, so it's pretty I guess
            this.drawPath(this.computePath());
        }
    }
    
    private void drawPath(@NotNull Queue<? extends Location> locations) {
        new HariantTickingStepTask(Scheduler.ofTimer(), locations.size() / 2) {
            @Override
            public boolean run(int tick, int step) {
                final Location location = locations.poll();
                
                if (location == null) {
                    return true;
                }
                
                entity.spawnWorldParticle(location, Particle.DUST_COLOR_TRANSITION, 1, 0.1, 0.1, 0.1, 0, DUST_TRANSITION);
                return false;
            }
        };
    }
    
    public @NotNull Queue<? extends Location> computePath() {
        return scents.stream().map(scent -> scent.location).collect(Collectors.toCollection(LinkedList::new));
    }
    
    @Override
    public void onCancel() {
        scents.clear();
    }
    
    private static class Scent {
        private final Location location;
        private final int tick;
        
        private Scent(@NotNull Location location, int tick) {
            this.location = location;
            this.tick = tick;
        }
    }
    
}