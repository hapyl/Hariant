package me.hapyl.hariant.entity.effect.status;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.effect.EffectType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum EnumStatusEffect implements StatusEffect {
    
    ROSE_IVY(new StatusEffectRoseIvy()),
    INVISIBILITY(new StatusEffectInvisibility()),
    
    ABYSSAL_CORROSION_1(new StatusEffectAbyssalCorrosion.Level1()),
    ABYSSAL_CORROSION_2(new StatusEffectAbyssalCorrosion.Level2()),
    ABYSSAL_CORROSION_3(new StatusEffectAbyssalCorrosion.Level3()),
    
    ;
    
    private final StatusEffect statusEffect;
    
    EnumStatusEffect(@NotNull StatusEffect statusEffect) {
        this.statusEffect = statusEffect;
    }
    
    @NotNull
    @Override
    public Key getKey() {
        return statusEffect.getKey();
    }
    
    @NotNull
    @Override
    public Component getName() {
        return statusEffect.getName();
    }
    
    @NotNull
    @Override
    public Component getDescription() {
        return statusEffect.getDescription();
    }
    
    @NotNull
    @Override
    public DeathMessage getDeathMessage() {
        return statusEffect.getDeathMessage();
    }
    
    @NotNull
    @Override
    public EffectType getEffectType() {
        return statusEffect.getEffectType();
    }
    
    @Override
    public void onApply(@NotNull HariantEntity entity, @Nullable HariantEntity applier) {
        statusEffect.onApply(entity, applier);
    }
    
    @Override
    public void onRemove(@NotNull HariantEntity entity, @Nullable HariantEntity applier) {
        statusEffect.onRemove(entity, applier);
    }
    
    @Override
    public void onTick(@NotNull HariantEntity entity, @Nullable HariantEntity applier, int tick) {
        statusEffect.onTick(entity, applier, tick);
    }
    
}
