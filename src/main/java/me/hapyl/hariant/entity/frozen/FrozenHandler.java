package me.hapyl.hariant.entity.frozen;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.VanillaAttributeModifier;
import me.hapyl.hariant.util.TickDuration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Input;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class FrozenHandler implements Ticking, TickDuration, ComponentLike {
    
    private static final Key MODIFIER_KEY = Key.ofString("frozen");
    
    private static final List<? extends VanillaAttributeModifier> ATTRIBUTES = List.of(
            VanillaAttributeModifier.create(MODIFIER_KEY, Attribute.JUMP_STRENGTH, VanillaAttributeModifier.Operation.FLAT, -100),
            VanillaAttributeModifier.create(MODIFIER_KEY, Attribute.MOVEMENT_SPEED, VanillaAttributeModifier.Operation.FLAT, -100)
    );
    
    private static final int KEY_CORRECT_DECREMENT = 8;
    private static final int KEY_INCORRECT_PENALTY = 4;
    
    private static final int KEY_LENGTH = 5;
    private static final long KEY_COOLDOWN = 50;
    
    private final HariantEntity entity;
    private final FrozenKeyStatus[] frozenKeys;
    private final int duration;
    
    private int currentTick;
    private int currentIndex;
    
    private long lastInput;
    
    public FrozenHandler(@NotNull HariantEntity entity, int duration) {
        this.entity = entity;
        this.duration = duration;
        this.currentTick = duration;
        this.frozenKeys = generateKeys();
        this.lastInput = System.currentTimeMillis() + (250 - KEY_COOLDOWN); // Add a tiny delay between first click
    }
    
    public void input(@NotNull Input input) {
        if (currentIndex >= KEY_LENGTH) {
            return;
        }
        
        // Check for cooldown
        final long currentTimeMillis = System.currentTimeMillis();
        
        if (currentTimeMillis - lastInput <= KEY_COOLDOWN) {
            return;
        }
        
        final FrozenKeyStatus key = frozenKeys[currentIndex++];
        lastInput = currentTimeMillis;
        
        // If correct input, decrement
        if (key.getKey().test(input)) {
            currentTick -= KEY_CORRECT_DECREMENT;
            
            // If index is out of bounds, and all indexes are success, end early
            if (currentIndex >= KEY_LENGTH && this.isAllSuccess()) {
                currentTick = 0;
            }
            
            key.setStatus(true);
            entity.playSound(Sound.UI_BUTTON_CLICK, 1.25f);
        }
        // Otherwise, punish for incorrect click
        else {
            currentTick += KEY_INCORRECT_PENALTY;
            
            key.setStatus(false);
            
            entity.playSound(Sound.UI_BUTTON_CLICK, 0.75f);
            entity.playSound(Sound.ITEM_SHIELD_BREAK, 0.0f);
            entity.playSound(Sound.BLOCK_GLASS_BREAK, 0.0f);
        }
    }
    
    @Override
    public void tick() {
        currentTick--;
        entity.onFrozenTick(this);
    }
    
    @Override
    public int currentTick() {
        return currentTick;
    }
    
    @Override
    public int duration() {
        return duration;
    }
    
    @Override
    public boolean isOver() {
        return currentTick <= 0;
    }
    
    public void freeze() {
        // Add modifiers
        ATTRIBUTES.forEach(entity::addVanillaAttributeModifier);
        
        entity.onFreeze(this);
    }
    
    public void unfreeze() {
        ATTRIBUTES.forEach(entity::removeVanillaAttributeModifier);
        
        entity.onUnfreeze(this);
    }
    
    @Override
    public @NotNull Component asComponent() {
        final TextComponent.Builder builder = Component.text();
        
        for (int i = 0; i < KEY_LENGTH; i++) {
            if (i != 0) {
                builder.appendSpace();
            }
            
            builder.append(frozenKeys[i].asComponent(currentIndex == i));
        }
        
        return builder.build();
    }
    
    private boolean isAllSuccess() {
        for (FrozenKeyStatus frozenKey : frozenKeys) {
            final Boolean status = frozenKey.getStatus();
            
            if (status != null && !status) {
                return false;
            }
        }
        
        return true;
    }
    
    @NotNull
    private static FrozenKeyStatus[] generateKeys() {
        final FrozenKeyStatus[] keys = new FrozenKeyStatus[KEY_LENGTH];
        
        for (int i = 0; i < KEY_LENGTH; i++) {
            keys[i] = new FrozenKeyStatus(FrozenKey.randomKey());
        }
        
        return keys;
    }
    
}