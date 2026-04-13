package me.hapyl.hariant.entity.frozen;

import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.util.ComponentProgress;
import me.hapyl.hariant.util.TickDuration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Input;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class FrozenHandler implements Ticking, TickDuration {
    
    private static final Map<Attribute, NamespacedKey> MODIFIER_KEYS = Map.ofEntries(
            entry(Attribute.JUMP_STRENGTH, "jump_strength"),
            entry(Attribute.MOVEMENT_SPEED, "movement_speed")
    );
    
    private static final int KEY_CORRECT_INCREMENT = 10;
    private static final int KEY_INCORRECT_DECREMENT = 20;
    
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
        
        
        // FIXME @Apr 02, 2026 (xanyjl) -> Might want to switch to spam space or A D ?
        
        // Add a tiny delay between first click
        this.lastInput = System.currentTimeMillis() + (250 - KEY_COOLDOWN);
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
            currentTick -= KEY_CORRECT_INCREMENT;
            
            // If index is out of bounds, and all indexes are success, end early
            if (currentIndex >= KEY_LENGTH && this.isAllSuccess()) {
                currentTick = 0;
            }
            
            key.setStatus(true);
            entity.playSound(Sound.UI_BUTTON_CLICK, 1.25f);
        }
        // Otherwise, punish for incorrect click
        else {
            currentTick += KEY_INCORRECT_DECREMENT;
            
            key.setStatus(false);
            
            entity.playSound(Sound.UI_BUTTON_CLICK, 0.75f);
            entity.playSound(Sound.ITEM_SHIELD_BREAK, 0.0f);
            entity.playSound(Sound.BLOCK_GLASS_BREAK, 0.0f);
        }
    }
    
    @Override
    public void tick() {
        currentTick--;
        
        // Players can accelerate pressing the button
        if (entity instanceof HariantPlayer player) {
            // Show the keys
            final TextComponent.Builder builder = Component.text();
            
            for (int i = 0; i < KEY_LENGTH; i++) {
                final FrozenKeyStatus key = frozenKeys[i];
                final boolean isCurrentKey = currentIndex == i;
                
                if (i != 0) {
                    builder.appendSpace();
                }
                
                builder.append(key.asComponent(isCurrentKey));
            }
            
            // Build frozen title
            final double progress = (double) currentTick / duration;
            
            player.sendTitleSubtitle(
                    ComponentProgress.create("ꜰʀᴏᴢᴇɴ", ElementType.ICE.getStyle(), progress),
                    builder.build(),
                    0, 5, 0
            );
        }
        
        // Fx
        entity.getHandle().setFreezeTicks(100);
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
        MODIFIER_KEYS.forEach((attribute, key) -> {
            final AttributeInstance vanillaAttribute = entity.getVanillaAttribute(attribute);
            
            // Love the "Fuck you this modifier already exists fuck you fuCK YOU FUCK YOU" treatment from bukkit, kys
            vanillaAttribute.removeModifier(key);
            vanillaAttribute.addTransientModifier(new AttributeModifier(key, -100, AttributeModifier.Operation.ADD_NUMBER));
        });
        
        // TODO @Mar 11, 2026 (xanyjl) -> Add icicles
        
        entity.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2.0f);
    }
    
    public void unfreeze() {
        // Remove modifiers
        MODIFIER_KEYS.forEach((attribute, key) -> entity.getVanillaAttribute(attribute).removeModifier(key));
        
        entity.getHandle().setFreezeTicks(0);
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
    
    @NotNull
    private static Map.Entry<Attribute, NamespacedKey> entry(@NotNull Attribute attribute, @NotNull String key) {
        return Map.entry(attribute, new NamespacedKey(Hariant.getPlugin(), "frozen_" + key));
    }
    
}