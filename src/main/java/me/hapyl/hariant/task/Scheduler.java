package me.hapyl.hariant.task;

import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public interface Scheduler {
    
    @NotNull
    BukkitTask schedule(@NotNull Runnable runnable);
    
    @NotNull
    static Scheduler ofNow() {
        return create(BukkitScheduler::runTask);
    }
    
    @NotNull
    static Scheduler ofDelayed(final int delay) {
        return create((scheduler, plugin, runnable) -> scheduler.runTaskLater(plugin, runnable, delay));
    }
    
    @NotNull
    static Scheduler ofTimer(final int delay, final int period) {
        return create(((scheduler, plugin, runnable) -> scheduler.runTaskTimer(plugin, runnable, delay, period)));
    }
    
    @NotNull
    static Scheduler ofTimer(final int period) {
        return ofTimer(0, period);
    }
    
    @NotNull
    static Scheduler ofTimer() {
        return ofTimer(0, 1);
    }
    
    @NotNull
    private static Scheduler create(@NotNull BukkitSchedulerSupplier supplier) {
        return runnable -> supplier.supply(Bukkit.getScheduler(), Hariant.getPlugin(), runnable);
    }
    
    interface BukkitSchedulerSupplier {
        @NotNull
        BukkitTask supply(@NotNull BukkitScheduler scheduler, @NotNull HariantPlugin plugin, @NotNull Runnable runnable);
    }
    
}
