package me.hapyl.hariant.database;

import java.lang.annotation.*;

/**
 * Indicates that the annotated database-related operation requires the player to be connected to the server.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface RequiresOnlinePlayer {
}
