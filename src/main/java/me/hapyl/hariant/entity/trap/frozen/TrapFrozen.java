package me.hapyl.hariant.entity.trap.frozen;

import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.trap.Trap;
import me.hapyl.hariant.entity.trap.TrapEscape;
import me.hapyl.hariant.entity.trap.TrapName;
import me.hapyl.hariant.util.Priority;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public class TrapFrozen extends Trap {
    
    private static final TrapName TRAP_NAME = new TrapName("Frozen", ElementType.ICE.getStyle());
    
    public TrapFrozen(@NotNull HariantEntity entity, @NotNull HariantEntity source, int duration) {
        super(entity, source, TRAP_NAME, duration);
    }
    
    @Override
    public void onTrap() {
        entity.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2.0f);
    }
    
    @Override
    public void onEscape(@NotNull TrapEscape escape) {
        entity.getHandle().setFreezeTicks(0);
        entity.playWorldSound(Sound.ENTITY_ZOMBIE_INFECT, 1.0f);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        entity.getHandle().setFreezeTicks(100);
    }
    
    @Override
    public @NotNull Priority getPriority() {
        return Priority.HIGH;
    }
    
    @Override
    public boolean blocksTalents() {
        return true;
    }
    
    @Override
    public boolean blocksAttacks() {
        return true;
    }
    
}