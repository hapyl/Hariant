package me.hapyl.hariant.entity.player;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class ArtifactAffixAttributeModifier extends AttributeModifier {
    
    private static final Key MODIFIER_KEY = Key.ofString("artifact_affixes");
    private static final Component MODIFIER_NAME = Component.text("Artifact Affixes");
    
    private static final AttributeModifierType ATTRIBUTE_MODIFIER_TYPE = AttributeModifierType.FLAT;
    
    ArtifactAffixAttributeModifier(@NotNull HariantPlayer player, @NotNull Map<? extends @NotNull AttributeType, ? extends @NotNull Double> artifactAffixes) {
        super(MODIFIER_KEY, MODIFIER_NAME, player, HariantConstants.INDEFINITE_DURATION);
        
        artifactAffixes.forEach((attributeType, value) -> of(attributeType, ATTRIBUTE_MODIFIER_TYPE, value));
    }
    
    @Override
    public void display(@NotNull Location location) {
    }
    
}