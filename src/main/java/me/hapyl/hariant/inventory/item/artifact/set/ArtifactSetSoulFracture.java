package me.hapyl.hariant.inventory.item.artifact.set;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstanceSnapshot;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierArtifactSet;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageCalculationsEvent;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.ArtifactSetModifier;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.CommonArtifactSetModifiers;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class ArtifactSetSoulFracture extends ArtifactSet implements Listener {
    
    private final ArtifactSetModifier elementalMasteryIncrease = CommonArtifactSetModifiers.ELEMENTAL_MASTERY;
    private final Decimal aetherResistanceIgnore = Decimal.ofAttribute(AttributeType.AETHER_RESISTANCE, 20);
    
    public ArtifactSetSoulFracture(@NotNull Key key) {
        super(key, Component.text("Soul Fracture"));
        
        setArtifactTags(AttributeType.ELEMENTAL_MASTERY, AttributeType.AETHER_DAMAGE_BONUS);
        
        setPieceDescription(PieceCount.TWO_PIECE, elementalMasteryIncrease);
        
        setPieceDescription(
                PieceCount.FOUR_PIECE,
                Component.empty()
                         .append(Component.text("Dealing "))
                         .append(ElementType.AETHER.asComponentDamage())
                         .append(Component.text(" ignores "))
                         .append(aetherResistanceIgnore)
                         .append(Component.text(" of target's "))
                         .append(AttributeType.AETHER_RESISTANCE)
                         .append(Component.text("."))
        );
    }
    
    @Override
    public @NotNull ElementType getEffectiveElementType() {
        return ElementType.AETHER;
    }
    
    @EventHandler
    public void handleHariantDamageCalculationsEvent(HariantDamageCalculationsEvent ev) {
        final AttributesInstanceSnapshot attacker = ev.getAttacker();
        final HariantEntity entity = attacker.entity().orElse(null);
        
        if (!(entity instanceof HariantPlayer player)) {
            return;
        }
        
        final PieceCount pieceCount = player.getHeroInstance().countArtifactSetPieces(this);
        
        if (pieceCount != PieceCount.FOUR_PIECE) {
            return;
        }
        
        attacker.addModifier(player, AttributeModifier.entry(AttributeType.AETHER_RESISTANCE, AttributeModifierType.FLAT, -aetherResistanceIgnore.doubleValue()));
    }
    
    @Override
    public void applyEffect(@NotNull HariantPlayer player, @NotNull PieceCount pieceCount) {
        if (pieceCount.isOrHigher(PieceCount.TWO_PIECE)) {
            player.getAttributes().addModifier(new ModifierTwoPiece(player));
        }
    }
    
    public class ModifierTwoPiece extends AttributeModifierArtifactSet {
        ModifierTwoPiece(@NotNull HariantEntity applier) {
            super(ArtifactSetSoulFracture.this, PieceCount.TWO_PIECE, applier, elementalMasteryIncrease);
        }
    }
    
}
