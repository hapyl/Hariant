package me.hapyl.hariant.task;

import me.hapyl.hariant.Hariant;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

/**
 * Prefer {@link HariantTask} unless you know exactly what you're doing.
 */
public final class InternalTasks {
    
    private InternalTasks() {
    }
    
    public static void asynchronously(@NotNull Runnable runnable) {
        runTask(runnable, BukkitRunnable::runTaskAsynchronously);
    }
    
    public static void later(@NotNull Runnable runnable, final int delay) {
        runTask(runnable, (bukkitRunnable, plugin) -> bukkitRunnable.runTaskLater(plugin, delay));
    }
    
    public static void now(@NotNull Runnable runnable) {
        runTask(runnable, BukkitRunnable::runTask);
    }
    
    private static void runTask(@NotNull Runnable runnable, @NotNull BiFunction<BukkitRunnable, Plugin, BukkitTask> function) {
        function.apply(new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }, Hariant.getPlugin());
    }
    
}
