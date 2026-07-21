package me.hapyl.hariant.entity.effect.status;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.effect.EffectType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public enum StatusEffectType implements StatusEffect {
    
    ROSE_IVY(new StatusEffectRoseIvy()),
    INVISIBILITY(new StatusEffectInvisibility()),
    
    ABYSSAL_CORROSION_1(new StatusEffectAbyssalCorrosion.Level1()),
    ABYSSAL_CORROSION_2(new StatusEffectAbyssalCorrosion.Level2()),
    ABYSSAL_CORROSION_3(new StatusEffectAbyssalCorrosion.Level3()),
    
    ARCANE_MUTE(new StatusEffectArcaneMute()),
    TALENT_LOCK(new StatusEffectTalentLock()),
    RESPAWN_RESISTANCE(new StatusEffectRespawnResistance()),
    HELLBURN(new StatusEffectHellburn()),
    STUNNED(new StatusEffectStunned()),
    
    ;
    
    private final StatusEffect statusEffect;
    
    StatusEffectType(@NotNull StatusEffect statusEffect) {
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
    public void onApply(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int duration) {
        statusEffect.onApply(entity, applier, duration);
    }
    
    @Override
    public void onRemove(@NotNull HariantEntity entity, @NotNull HariantEntity applier) {
        statusEffect.onRemove(entity, applier);
    }
    
    @Override
    public void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick) {
        statusEffect.onTick(entity, applier, tick);
    }
    
}
