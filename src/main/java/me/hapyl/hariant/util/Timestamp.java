package me.hapyl.hariant.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class Timestamp implements ComponentLike, Comparable<Timestamp> {
    
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    private final long timestamp;
    private final Component component;
    
    private Timestamp(long timestamp) {
        this.timestamp = timestamp;
        this.component = Component.text(Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.UTC).format(FORMATTER));
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return component;
    }
    
    @Override
    public int compareTo(@NotNull Timestamp that) {
        return Long.compare(this.timestamp, that.timestamp);
    }
    
    @NotNull
    public static Timestamp ofEpoch(final long epoch) {
        return new Timestamp(epoch);
    }
    
    @NotNull
    public static Timestamp ofNow() {
        return new Timestamp(System.currentTimeMillis());
    }
    
}