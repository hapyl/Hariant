package me.hapyl.hariant.hero.shark;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.location.Located;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.task.HariantTickingStepTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.Cancellable;
import me.hapyl.hariant.util.Definition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

public class BloodScent implements Ticking, Cancellable, ComponentLike {
    
    public static final int TICK_THRESHOLD = 50;
    
    private static final double DISTANCE_THRESHOLD_SQUARED = 0.75 * 0.75;
    private static final Particle.DustTransition DUST_TRANSITION = new Particle.DustTransition(Color.fromRGB(136, 8, 8), Color.fromRGB(152, 0, 2), 1.25f);
    
    private final HariantPlayer player;
    private final HariantEntity entity;
    private final Queue<Scent> scents;
    
    private final int duration;
    private int tick;
    
    public BloodScent(@NotNull HariantPlayer player, @NotNull HariantEntity entity, final int duration) {
        this.player = player;
        this.entity = entity;
        this.duration = duration;
        this.scents = Lists.newLinkedList();
    }
    
    public @NotNull HariantEntity getEntity() {
        return entity;
    }
    
    @Override
    public void tick() {
        // Remove all scents that are older than the threshold
        scents.removeIf(scent -> tick - scent.tick > TICK_THRESHOLD);
        
        final Location location = entity.getLocation().add(0, 0.2, 0);
        final Scent previousScent = scents.peek();
        
        // If previous scent is close to the current location, skip
        if (previousScent == null || previousScent.location.distanceSquared(location) > DISTANCE_THRESHOLD_SQUARED) {
            scents.offer(new Scent(location, tick));
        }
        
        // Draw blood using another task, so it's pretty I guess
        if (tick % 20 == 0) {
            this.drawPath(this.computePath());
        }
        
        tick++;
    }
    
    @Override
    public void cancel() {
        scents.clear();
    }
    
    public boolean isOver() {
        return entity.isDead() || tick > duration;
    }
    
    public @NotNull Queue<? extends Location> computePath() {
        return scents.stream().map(Scent::getLocation).collect(Collectors.toCollection(LinkedList::new));
    }
    
    @Override
    public @NotNull Component asComponent() {
        return Component.empty()
                        .append(Definition.PREY)
                        .appendSpace()
                        .append(entity.asHeadComponent())
                        .appendSpace()
                        .append(Component.text(Tick.format(duration - tick), Colors.TICK));
    }
    
    private void drawPath(@NotNull Queue<? extends Location> path) {
        new HariantTickingStepTask(Scheduler.ofTimer(), path.size() / 4) {
            @Override
            public boolean run(int tick, int step) {
                final Location location = path.poll();
                
                if (location == null) {
                    return true;
                }
                
                player.spawnParticle(location, Particle.DUST_COLOR_TRANSITION, 1, 0.1, 0.1, 0.1, 0, DUST_TRANSITION);
                return false;
            }
        };
    }
    
    private static class Scent implements Located {
        
        private final Location location;
        private final int tick;
        
        private Scent(@NotNull Location location, int tick) {
            this.location = location;
            this.tick = tick;
        }
        
        @Override
        public @NotNull Location getLocation() {
            return location;
        }
        
    }
    
}