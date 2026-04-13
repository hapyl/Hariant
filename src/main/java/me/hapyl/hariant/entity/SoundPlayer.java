package me.hapyl.hariant.entity;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public interface SoundPlayer {
    
    float DEFAULT_VOLUME = 3.0f;
    
    // *-* Play Sound @ Self *-* //
    
    void playSound(@NotNull Sound sound, float volume, @Range(from = 0, to = 2) final float pitch);
    
    default void playSound(@NotNull Sound sound, @Range(from = 0, to = 2) final float pitch) {
        this.playSound(sound, DEFAULT_VOLUME, pitch);
    }
    
    // *-* Play Sound @ Location *-* //
    
    void playSound(@NotNull Location location, @NotNull Sound sound, float volume, @Range(from = 0, to = 2) final float pitch);
    
    default void playSound(@NotNull Location location, @NotNull Sound sound, @Range(from = 0, to = 2) final float pitch) {
        this.playSound(location, sound, DEFAULT_VOLUME, pitch);
    }

    // *-* Play World Sound @ Self *-* //
    
    void playWorldSound(@NotNull Sound sound, float volume, @Range(from = 0, to = 2) final float pitch);
    
    default void playWorldSound(@NotNull Sound sound, @Range(from = 0, to = 2) final float pitch) {
        this.playWorldSound(sound, DEFAULT_VOLUME, pitch);
    }
    
    // *-* Play World Sound @ Location *-* //
    
    void playWorldSound(@NotNull Location location, @NotNull Sound sound, float volume, @Range(from = 0, to = 2) float pitch);
    
    default void playWorldSound(@NotNull Location location, @NotNull Sound sound, @Range(from = 0, to = 2) final float pitch) {
        this.playWorldSound(location, sound, DEFAULT_VOLUME, pitch);
    }
    
    @NotNull
    SoundCategory soundCategory();
    
}
