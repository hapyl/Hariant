package me.hapyl.hariant.entity.player;

import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.util.TickDuration;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class HeartStyle implements Ticking, TickDuration {
    
    private static final int EFFECT_DURATION = 5;
    
    private final int duration;
    private int currentTick;
    
    HeartStyle(int duration) {
        this.duration = duration;
        this.currentTick = duration;
    }
    
    @Override
    public void tick() {
        if (currentTick != HariantConstants.INDEFINITE_COOLDOWN) {
            currentTick--;
        }
    }
    
    @Override
    public int currentTick() {
        return currentTick;
    }
    
    @Override
    public int duration() {
        return duration;
    }
    
    public abstract void apply(@NotNull HariantPlayer player);
    
    @Nullable
    public static HeartStyle red() {
        return null;
    }
    
    @NotNull
    public static HeartStyle white(int duration) {
        return new HeartStyleWhiteImpl(duration);
    }
    
    @NotNull
    public static HeartStyle white() {
        return white(HariantConstants.INDEFINITE_DURATION);
    }
    
    @NotNull
    public static HeartStyle green(int duration) {
        return new HeartStyleGreenImpl(duration);
    }
    
    @NotNull
    public static HeartStyle green() {
        return green(HariantConstants.INDEFINITE_DURATION);
    }
    
    @NotNull
    public static HeartStyle black(int duration) {
        return new HeartStyleBlackImpl(duration);
    }
    
    @NotNull
    public static HeartStyle black() {
        return black(HariantConstants.INDEFINITE_DURATION);
    }
    
    public static class HeartStyleWhiteImpl extends HeartStyle {
        HeartStyleWhiteImpl(int duration) {
            super(duration);
        }
        
        @Override
        public void apply(@NotNull HariantPlayer player) {
            player.getHandle().setFreezeTicks(10);
        }
    }
    
    public static class HeartStyleGreenImpl extends HeartStyle {
        HeartStyleGreenImpl(int duration) {
            super(duration);
        }
        
        @Override
        public void apply(@NotNull HariantPlayer player) {
            player.addVanillaEffect(PotionEffectType.POISON, 0, EFFECT_DURATION);
        }
    }
    
    public static class HeartStyleBlackImpl extends HeartStyle {
        HeartStyleBlackImpl(int duration) {
            super(duration);
        }
        
        @Override
        public void apply(@NotNull HariantPlayer player) {
            player.addVanillaEffect(PotionEffectType.WITHER, 0, EFFECT_DURATION);
        }
    }
    
}