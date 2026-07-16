package me.hapyl.hariant.inventory.item.artifact.set.modifier;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ArtifactSetModifierImpl implements ArtifactSetModifier {
    
    private final AttributeType attributeType;
    private final AttributeModifierType modifierType;
    private final double value;
    
    private final Component component;
    
    ArtifactSetModifierImpl(@NotNull AttributeType attributeType, @NotNull AttributeModifierType modifierType, double value, @NotNull Component component) {
        this.attributeType = attributeType;
        this.modifierType = modifierType;
        this.value = value;
        this.component = component;
    }
    
    public @NotNull AttributeType getAttributeType() {
        return attributeType;
    }
    
    public @NotNull AttributeModifierType getModifierType() {
        return modifierType;
    }
    
    public double getValue() {
        return value;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return component;
    }
    
}
