package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.AffectResult;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSource;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HariantAttackEvent extends HariantEvent implements Cancellable {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final HariantEntity attacker;
    private final HariantEntity entity;
    private final DamageSource damageSource;
    
    private AffectResult affectResult;
    private boolean cancel;
    
    public HariantAttackEvent(@NotNull HariantEntity attacker, @NotNull HariantEntity entity, @NotNull DamageSource damageSource, AffectResult affection) {
        this.attacker = attacker;
        this.entity = entity;
        this.damageSource = damageSource;
        this.affectResult = affection;
    }
    
    @NotNull
    public HariantEntity getAttacker() {
        return attacker;
    }
    
    @NotNull
    public HariantEntity getEntity() {
        return entity;
    }
    
    @NotNull
    public DamageSource getDamageSource() {
        return damageSource;
    }
    
    @NotNull
    public AffectResult getAffectResult() {
        return affectResult;
    }
    
    public void setAffectResult(@NotNull AffectResult affectResult) {
        this.affectResult = affectResult;
    }
    
    @Override
    public boolean isCancelled() {
        return cancel;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
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
    
}