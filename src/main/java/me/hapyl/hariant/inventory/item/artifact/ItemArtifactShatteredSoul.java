package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstanceSnapshot;
import me.hapyl.hariant.attribute.modifier.AttributeModifierArtifactSet;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageCalculationsEvent;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.term.Terminology;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemArtifactShatteredSoul extends ItemArtifact {
    public ItemArtifactShatteredSoul(@NotNull Key key) {
        super(key, Component.text("Shattered Soul"), Icon.ofTexture("932a6f27680e38d3c6f63f70a28b4130817bd99c6126e6c9285a3d9c82afa1c9"), new ArtifactSetSoulFracture(key));
        
        setDescription(Component.text("A shattered soul that seem to be stuck in time, trying to escape, but never succeeds."));
    }
    
    private static class ArtifactSetSoulFracture extends ArtifactSet implements Listener {
        
        @DisplayField private final Decimal elementalMasteryIncrease = Decimal.ofAttribute(AttributeType.ELEMENTAL_MASTERY, 120);
        @DisplayField private final Decimal aetherDamageBonus = Decimal.ofAttribute(AttributeType.AETHER_DAMAGE_BONUS, 40);
        
        ArtifactSetSoulFracture(@NotNull Key key) {
            super(key, Component.text("Soul Fracture"));
            
            setPieceDescription(
                    PieceCount.TWO_PIECE,
                    Component.empty()
                             .append(Component.text("Increases "))
                             .append(AttributeType.ELEMENTAL_MASTERY)
                             .append(Component.text(" by "))
                             .append(elementalMasteryIncrease)
                             .append(Component.text("."))
            );
            
            setPieceDescription(
                    PieceCount.FOUR_PIECE,
                    Component.empty()
                             .append(Component.text("Increases "))
                             .append(ElementType.AETHER.asComponentDamage())
                             .append(Component.text(" of "))
                             .append(Component.text("ranged", Terminology.TERM_STYLE))
                             .append(Component.text(" attacks by "))
                             .append(aetherDamageBonus)
                             .append(Component.text("."))
            );
        }
        
        @Nullable
        @Override
        public ElementType getEffectiveElementType() {
            return ElementType.AETHER;
        }
        
        @EventHandler
        public void handleHariantDamageCalculationsEvent(HariantDamageCalculationsEvent ev) {
            final AttributesInstanceSnapshot snapshotAttacker = ev.getSnapshotAttacker();
            final HariantEntity entity = snapshotAttacker.entity().orElse(null);
            
            if (!(entity instanceof HariantPlayer player)) {
                return;
            }
            
            final PieceCount pieceCount = player.getHeroInstance().countArtifactSetPieces(this);
            
            if (pieceCount != PieceCount.FOUR_PIECE) {
                return;
            }
            
            final DamageSource damageSource = ev.getDamageSource();
            
            if (damageSource.getDamageType() != DamageType.RANGED) {
                return;
            }
            
            snapshotAttacker.addModifier(new ModifierFourPiece(player));
        }
        
        @Override
        public void applyEffect(@NotNull HariantPlayer player, @NotNull PieceCount pieceCount) {
            if (pieceCount.isOrHigher(PieceCount.TWO_PIECE)) {
                player.getAttributes().addModifier(new ModifierTwoPiece(player));
            }
        }
        
        public class ModifierTwoPiece extends AttributeModifierArtifactSet {
            ModifierTwoPiece(@NotNull HariantEntity applier) {
                super(ArtifactSetSoulFracture.this, PieceCount.TWO_PIECE, applier, HariantConstants.INDEFINITE_DURATION);
                
                of(AttributeType.ELEMENTAL_MASTERY, AttributeModifierType.FLAT, elementalMasteryIncrease.doubleValue());
            }
        }
        
        public class ModifierFourPiece extends AttributeModifierArtifactSet {
            ModifierFourPiece(@NotNull HariantEntity applier) {
                super(ArtifactSetSoulFracture.this, PieceCount.FOUR_PIECE, applier, HariantConstants.INDEFINITE_DURATION);
                
                of(AttributeType.AETHER_DAMAGE_BONUS, AttributeModifierType.FLAT, aetherDamageBonus.doubleValue());
            }
        }
    }
}
