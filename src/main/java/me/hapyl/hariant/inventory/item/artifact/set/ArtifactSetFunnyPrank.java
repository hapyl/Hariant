package me.hapyl.hariant.inventory.item.artifact.set;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifierArtifactSet;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantEffectEvent;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class ArtifactSetFunnyPrank extends ArtifactSet implements Listener {
    
    private final Decimal twoPieceLuckIncrease = Decimal.ofValue(80);
    
    private final Decimal fourPieceLuckIncrease = Decimal.ofPercentage(60);
    private final Decimal fourPieceBonusDuration = Decimal.ofSeconds(4);
    
    ArtifactSetFunnyPrank(@NotNull Key key) {
        super(key, Component.text("Funny Prank"));
        
        setArtifactTags(AttributeType.LUCK);
        
        setPieceDescription(
                PieceCount.TWO_PIECE,
                Component.empty()
                         .append(Component.text("Increases "))
                         .append(AttributeType.LUCK)
                         .append(Component.text(" by "))
                         .append(twoPieceLuckIncrease)
                         .append(Component.text("."))
        );
        
        setPieceDescription(
                PieceCount.FOUR_PIECE,
                Component.empty()
                         .append(Component.text("Successfully "))
                         .append(Component.text("impairing", Colors.ARCHETYPE_HEXBANE))
                         .append(Component.text(" an enemy increases "))
                         .append(AttributeType.LUCK)
                         .append(Component.text(" by "))
                         .append(fourPieceLuckIncrease)
                         .append(Component.text(" for "))
                         .append(fourPieceBonusDuration)
                         .append(Component.text("."))
        );
    }
    
    @Override
    public void applyEffect(@NotNull HariantPlayer player, @NotNull PieceCount pieceCount) {
        if (pieceCount.isOrHigher(PieceCount.TWO_PIECE)) {
            player.getAttributes().addModifier(new ModifierTwoPiece(player));
        }
    }
    
    @EventHandler
    public void handleHariantEffectEvent(HariantEffectEvent ev) {
        if (!(ev.getApplier() instanceof HariantPlayer player) || ev.hasResisted()) {
            return;
        }
        
        final PieceCount pieceCount = player.getHeroInstance().countArtifactSetPieces(this);
        final HariantEntity entity = ev.getEntity();
        
        if (pieceCount.isLower(PieceCount.FOUR_PIECE) || player.isSelfOrTeammate(entity) || ev.getEffect().getEffectType() != EffectType.DEBUFF) {
            return;
        }
        
        player.getAttributes().addModifier(new ModifierFourPiece(player));
    }
    
    public class ModifierTwoPiece extends AttributeModifierArtifactSet {
        ModifierTwoPiece(@NotNull HariantEntity applier) {
            super(ArtifactSetFunnyPrank.this, PieceCount.TWO_PIECE, applier, HariantConstants.INDEFINITE_DURATION);
            
            of(AttributeType.LUCK, AttributeModifierType.FLAT, twoPieceLuckIncrease.doubleValue());
        }
    }
    
    public class ModifierFourPiece extends AttributeModifierArtifactSet {
        ModifierFourPiece(@NotNull HariantEntity applier) {
            super(ArtifactSetFunnyPrank.this, PieceCount.FOUR_PIECE, applier, fourPieceBonusDuration.intValue());
            
            of(AttributeType.LUCK, AttributeModifierType.MULTIPLICATIVE, fourPieceLuckIncrease.doubleValue());
        }
    }
    
}
