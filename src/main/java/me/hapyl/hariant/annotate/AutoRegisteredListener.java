package me.hapyl.hariant.annotate;

import me.hapyl.hariant.Hariant;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface AutoRegisteredListener {
    
    class Registry {
        public static void register(@NotNull Object object) {
            if (!(object instanceof Listener listener)) {
                return;
            }
            
            Bukkit.getPluginManager().registerEvents(listener, Hariant.getPlugin());
        }
    }
    
}
