package me.hapyl.hariant.talent;

import org.jetbrains.annotations.NotNull;

public class Response {
    
    private static final Response OK = new Response(Status.OK, "");
    private static final Response AWAIT = new Response(Status.AWAIT, "");
    
    private final Status status;
    private final String reason;
    
    private Response(@NotNull Status status, @NotNull String reason) {
        this.status = status;
        this.reason = reason;
    }
    
    @NotNull
    public String getReason() {
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
    
    @NotNull
    public static Response ok() {
        return OK;
    }
    
    @NotNull
    public static Response await() {
        return AWAIT;
    }
    
    @NotNull
    public static Response error(@NotNull String reason) {
        return new Response(Status.ERROR, reason);
    }
    
    public enum Status {
        OK,
        AWAIT,
        ERROR
    }
    
}
