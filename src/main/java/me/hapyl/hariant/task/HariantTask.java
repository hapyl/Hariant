package me.hapyl.hariant.task;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.hariant.game.GameInstance;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Supplier;

/**
 * Represents a simple implementation of a task that may be scheduler via {@link Scheduler}.
 *
 * <p>
 * Note that tasks are mainly intended to be used in a game, and are <b>always</b> cancelled when a {@link GameInstance} ends.
 * </p>
 */
public abstract class HariantTask implements Runnable, Cancellable {
    
    private static final Set<HariantTask> ALL_TASKS = Sets.newConcurrentHashSet();
    private static final Runnable EMPTY_RUNNABLE = () -> {};
    
    private final BukkitTask bukkitTask;
    
    public HariantTask(@NotNull Scheduler scheduler) {
        this.bukkitTask = scheduler.schedule(this);
        
        ALL_TASKS.add(this);
    }
    
    @EventLike
    public void onCancel() {
    }
    
    @Override
    public synchronized void cancel() {
        this.cancel0();
        ALL_TASKS.remove(this);
    }
    
    public synchronized boolean isCancelled() {
        return this.bukkitTask.isCancelled();
    }
    
    @Override
    public final int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public final boolean equals(Object obj) {
        return this == obj;
    }
    
    private synchronized void cancel0() {
        if (this.bukkitTask.isCancelled()) {
            return;
        }
        
        this.bukkitTask.cancel();
        this.onCancel();
    }
    
    public static void cancelAllTasks() {
        ALL_TASKS.forEach(HariantTask::cancel0);
        ALL_TASKS.clear();
    }
    
    public static @NotNull HariantTask later(@NotNull Runnable runnable, @NotNull Runnable onCancel, int delay) {
        return new HariantTask(Scheduler.ofDelayed(delay)) {
            @Override
            public void run() {
                runnable.run();
            }
            
            @Override
            public void onCancel() {
                onCancel.run();
            }
        };
    }
    
    public static @NotNull HariantTask later(@NotNull Runnable runnable, int delay) {
        return later(runnable, EMPTY_RUNNABLE, delay);
    }
    
    public static @NotNull HariantTask remove(@NotNull Supplier<Removable> supplier, int delay) {
        final Runnable runnable = () -> supplier.get().remove();
        
        return later(runnable, runnable, delay);
    }
    
}