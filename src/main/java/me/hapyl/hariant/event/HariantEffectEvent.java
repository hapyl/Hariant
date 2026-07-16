package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.AssistSource;
import me.hapyl.hariant.entity.effect.Effect;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.ui.ComponentDisplay;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HariantEffectEvent extends HariantEntityEvent implements CancellableWithReason {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final HariantEntity applier;
    private final Effect effect;
    private final boolean hasResisted;
    
    private @Nullable Cancel cancel;
    
    HariantEffectEvent(@NotNull HariantEntity entity, @NotNull HariantEntity applier, @NotNull Effect effect, boolean hasResisted) {
        super(entity);
        
        this.applier = applier;
        this.effect = effect;
        this.hasResisted = hasResisted;
    }
    
    public @NotNull HariantEntity getApplier() {
        return applier;
    }
    
    public @NotNull Effect getEffect() {
        return effect;
    }
    
    public boolean hasResisted() {
        return hasResisted;
    }
    
    @Override
    public @Nullable Cancel getCancel() {
        return cancel;
    }
    
    @Override
    public void setCancel(@NotNull Cancel cancel) {
        this.cancel = cancel;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    public @NotNull
    static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
    public static boolean callEvent(@NotNull HariantEntity entity, @NotNull HariantEntity applier, @NotNull Effect effect) {
        final EffectType effectType = effect.getEffectType();
        
        boolean hasResisted = false;
        
        // If effect type is negative, check for Effect RES
        if (effectType == EffectType.DEBUFF) {
            hasResisted = entity.hasEffectResistance(AssistSource.create(applier, effect));
        }
        
        // Call event, which has the ultimate say of whether to cancel the effect
        final HariantEffectEvent event = new HariantEffectEvent(entity, applier, effect, hasResisted);
        event.callEvent();
        
        final Cancel cancel = event.getCancel();
        
        // If event wasn't cancelled, return whether resisted or not
        if (cancel == null) {
            // Note that Effect RES creates its own component display and only displays it when we're not on internal cooldown,
            // so we should not spawn a display for it, just return whether to cancel the application
            return hasResisted;
        }
        // Otherwise return `true` and spawn a component display
        else {
            ComponentDisplay.ofAscend(cancel.asComponent(), entity.getLocation(), 20, 1.75f);
            return true;
        }
    }
    
}