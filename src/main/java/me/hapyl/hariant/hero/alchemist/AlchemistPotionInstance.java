package me.hapyl.hariant.hero.alchemist;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.util.TickDuration;
import org.jetbrains.annotations.NotNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public class AlchemistPotionInstance implements TickDuration {
    
    protected final HariantPlayer player;
    
    private final int duration;
    private int tick;
    
    AlchemistPotionInstance(@NotNull HariantPlayer player, @NotNull TalentAlchemistPotion alchemistPotion) {
        this.player = player;
        this.duration = alchemistPotion.getDuration();
        this.tick = duration;
    }
    
    @NotNull
    public HariantPlayer getPlayer() {
        return player;
    }
    
    @OverridingMethodsMustInvokeSuper
    public boolean tick() {
        this.tick--;
        return false;
    }
    
    @Override
    public int currentTick() {
        return tick;
    }
    
    @Override
    public int duration() {
        return duration;
    }
    
}
