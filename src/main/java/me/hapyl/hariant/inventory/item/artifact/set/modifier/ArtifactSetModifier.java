package me.hapyl.hariant.inventory.item.artifact.set.modifier;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public interface ArtifactSetModifier extends ComponentLike {
    
    @NotNull AttributeType getAttributeType();
    
    @NotNull AttributeModifierType getModifierType();
    
    double getValue();
    
    @Override
    @NotNull Component asComponent();
}
