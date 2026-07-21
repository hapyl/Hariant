package me.hapyl.hariant.attribute.instance;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.entity.HariantEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.UUID;

public class AttributeModifierSnapshot extends AttributeModifier {
    
    AttributeModifierSnapshot(@NotNull HariantEntity applier, @NotNull Entry[] entries) {
        super(
                // Keys don't matter here
                randomKey(),
                // Neither are names
                Component.empty(),
                // Unfortunately, applier must exist
                applier,
                // You guessed it, duration doesn't matter
                0
        );
        
        this.entries.addAll(Arrays.asList(entries));
    }
    
    private static @NotNull Key randomKey() {
        return Key.ofString(UUID.randomUUID().toString().substring(0, 8));
    }
    
}
