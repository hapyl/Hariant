package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.effect.EffectType;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HariantEffectEvent extends HariantEntityEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final HariantEntity applier;
    private final EffectType effectType;
    
    public HariantEffectEvent(@NotNull HariantEntity entity, @NotNull HariantEntity applier, @NotNull EffectType effectType) {
        super(entity);
        
        this.applier = applier;
        this.effectType = effectType;
    }
    
    @NotNull
    public HariantEntity getApplier() {
        return applier;
    }
    
    @NotNull
    public EffectType getEffectType() {
        return effectType;
    }
    
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
    public static void triggerDummyEvent(@NotNull HariantEntity entity, @NotNull HariantEntity applier, boolean buff) {
        new HariantEffectEvent(entity, applier, buff ? EffectType.BUFF : EffectType.DEBUFF).callEvent();
    }
    
}
