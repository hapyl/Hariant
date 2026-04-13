package me.hapyl.hariant.util;

import me.hapyl.hariant.entity.player.HariantPlayer;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public interface SoundFx {
    
    @NotNull
    Sound sound();
    
    @Range(from = 0, to = 2)
    float pitch();
    
    default void play(@NotNull HariantPlayer player) {
        player.playSound(this.sound(), this.pitch());
    }
    
    @NotNull
    static SoundFx create(@NotNull Sound sound, @Range(from = 0, to = 2) float pitch) {
        return new SoundFxImpl(sound, pitch);
    }
    
    @Nullable
    static SoundFx createNullable(@Nullable Sound sound) {
        return sound != null ? create(sound, 1.0f) : null;
    }
    
    class SoundFxImpl implements SoundFx {
        private final Sound sound;
        private final float pitch;
        
        SoundFxImpl(@NotNull Sound sound, float pitch) {
            this.sound = sound;
            this.pitch = pitch;
        }
        
        @NotNull
        @Override
        public Sound sound() {
            return sound;
        }
        
        @Range(from = 0, to = 2)
        @Override
        public float pitch() {
            return pitch;
        }
    }
}
