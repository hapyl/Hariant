package me.hapyl.hariant.inventory.item.artifact;

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
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class ItemArtifactWhoopeeCushion extends ItemArtifact {
    
    public ItemArtifactWhoopeeCushion(@NotNull Key key) {
        super(key, Component.text("Whoopee Cushion"), Icon.ofTexture("f2f8859a07fdbec3072dd8c6af492d3e6176c4890d64e01b630aa7a26c8ba536"), new ArtifactSetFunnyPrank(key));
        
        setDescription(
                Component.empty()
                         .append(Component.text("An item famous for making a funny sound whenever someone sits on it."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("For unknown reasons, it brings luck."))
        );
    }
    
    public static class ArtifactSetFunnyPrank extends ArtifactSet implements Listener {
        
        @DisplayField private final Decimal twoPieceLuckIncrease = Decimal.ofValue(80);
        
        @DisplayField private final Decimal fourPieceLuckIncrease = Decimal.ofPercentage(60);
        @DisplayField private final Decimal fourPieceBonusDuration = Decimal.ofSeconds(4);
        
        public ArtifactSetFunnyPrank(@NotNull Key key) {
            super(key, Component.text("Funny Prank"));
            
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
            if (!(ev.getApplier() instanceof HariantPlayer player)) {
                return;
            }
            
            final PieceCount pieceCount = player.getHeroInstance().countArtifactSetPieces(this);
            final HariantEntity entity = ev.getEntity();
            
            if (pieceCount.isLower(PieceCount.FOUR_PIECE) || player.isSelfOrTeammate(entity) || ev.getEffectType() != EffectType.DEBUFF) {
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
    
}