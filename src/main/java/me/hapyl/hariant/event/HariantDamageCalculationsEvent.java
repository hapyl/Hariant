package me.hapyl.hariant.event;

import me.hapyl.hariant.attribute.instance.AttributesInstanceSnapshot;
import me.hapyl.hariant.entity.damage.DamageSource;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HariantDamageCalculationsEvent extends HariantEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final DamageSource damageSource;
    
    private final AttributesInstanceSnapshot snapshotEntity;
    private final AttributesInstanceSnapshot snapshotAttacker;
    
    public HariantDamageCalculationsEvent(@NotNull DamageSource damageSource, @NotNull AttributesInstanceSnapshot snapshotEntity, @NotNull AttributesInstanceSnapshot snapshotAttacker) {
        this.damageSource = damageSource;
        this.snapshotEntity = snapshotEntity;
        this.snapshotAttacker = snapshotAttacker;
    }
    
    @NotNull
    public DamageSource getDamageSource() {
        return damageSource;
    }
    
    @NotNull
    public AttributesInstanceSnapshot getSnapshotEntity() {
        return snapshotEntity;
    }
    
    @NotNull
    public AttributesInstanceSnapshot getSnapshotAttacker() {
        return snapshotAttacker;
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
