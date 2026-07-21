package me.hapyl.hariant.talent;

import me.hapyl.hariant.entity.cooldown.HariantCooldown;
import me.hapyl.hariant.entity.player.HariantPlayer;
import org.jetbrains.annotations.NotNull;

public class Response {
    
    private static final Response OK = new Response(Status.OK, "");
    private static final Response AWAIT = new Response(Status.AWAIT, "");
    private static final Response HOLD = new Response(Status.HOLD, "");
    
    private final Status status;
    private final String reason;
    
    private Response(@NotNull Status status, @NotNull String reason) {
        this.status = status;
        this.reason = reason;
    }
    
    public @NotNull Status getStatus() {
        return status;
    }
    
    public @NotNull String getReason() {
        return reason;
    }
    
    public boolean isOk() {
        return this.status == Status.OK;
    }
    
    public boolean isAwait() {
        return this.status == Status.AWAIT;
    }
    
    public boolean isError() {
        return this.status == Status.ERROR;
    }
    
    public static @NotNull Response ok() {
        return OK;
    }
    
    public static @NotNull Response await() {
        return AWAIT;
    }
    
    public static @NotNull Response hold() {
        return HOLD;
    }
    
    public static @NotNull Response error(@NotNull String reason) {
        return new Response(Status.ERROR, reason);
    }
    
    public enum Status {
        /**
         * The talent was executed successfully; start the cooldown normally.
         */
        OK,
        
        /**
         * The talent was executed successfully; start an indefinite cooldown and delegate actual cooldown to the talent.
         */
        AWAIT {
            @Override
            public void setCooldown(@NotNull HariantPlayer player, @NotNull HariantCooldown cooldown) {
                player.setIndefiniteCooldown(cooldown);
            }
        },
        
        /**
         * The talent was executed successfully; do not start the cooldown as the talent manages its own cooldown.
         */
        HOLD {
            @Override
            public void setCooldown(@NotNull HariantPlayer player, @NotNull HariantCooldown cooldown) {
                // Skip cooldown
            }
        },
        
        /**
         * The talent execution resulted in an error.
         */
        ERROR;
        
        public void setCooldown(@NotNull HariantPlayer player, @NotNull HariantCooldown cooldown) {
            player.setCooldown(cooldown);
        }
        
    }
    
}
