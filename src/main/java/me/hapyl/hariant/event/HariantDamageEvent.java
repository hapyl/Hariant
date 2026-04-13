package me.hapyl.hariant.event;

import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageFlag;
import me.hapyl.hariant.entity.damage.DamageFlagged;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.util.Identified;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HariantDamageEvent extends HariantEvent implements Cancellable, DamageFlagged {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final DamageInstance damageInstance;
    
    private boolean cancel;
    
    public HariantDamageEvent(@NotNull DamageInstance damageInstance) {
        this.damageInstance = damageInstance;
    }
    
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    @NotNull
    public DamageInstance getDamageInstance() {
        return damageInstance;
    }
    
    @NotNull
    public HariantEntity getEntity() {
        return damageInstance.getEntity();
    }
    
    @Nullable
    public HariantEntity getAttacker() {
        return damageInstance.getAttacker();
    }
    
    public boolean isCritical() {
        return damageInstance.isCritical();
    }
    
    @NotNull
    public ElementType getElementType() {
        return damageInstance.getSource().getElementType();
    }
    
    @NotNull
    public DamageType getDamageType() {
        return damageInstance.getSource().getDamageType();
    }
    
    @Override
    @NotNull
    public List<? extends DamageFlag> getDamageFlags() {
        return damageInstance.getSource().getDamageFlags();
    }
    
    @Override
    public boolean isFlagged(@NotNull DamageFlag damageFlag) {
        return damageInstance.getSource().isFlagged(damageFlag);
    }
    
    public double getBaseDamage() {
        return damageInstance.getSource().getDamage();
    }
    
    public double getDamage() {
        return damageInstance.getDamage();
    }
    
    public void multiplyDamage(@NotNull Identified identity, double multiplier) {
        this.damageInstance.multiplyDamage(identity, multiplier);
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
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}
