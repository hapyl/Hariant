package me.hapyl.hariant.inventory.item.artifact.set;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.modifier.AttributeModifierArtifactSet;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageComputeEvent;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.ArtifactSetModifier;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.CommonArtifactSetModifiers;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class ArtifactSetOpenWounds extends ArtifactSet implements Listener {
    
    private final ArtifactSetModifier twoPieceAttackIncrease = CommonArtifactSetModifiers.ATTACK;
    
    private final Decimal fourPieceDamageBonus = Decimal.ofPercentage(15);
    
    ArtifactSetOpenWounds(@NotNull Key key) {
        super(key, Component.text("Open Wounds"));
       
        // FIXME (xanyjl @ Saturday, September 19) -> Maybe make it stackable?
        
        setPieceDescription(PieceCount.TWO_PIECE, twoPieceAttackIncrease);
        
        setPieceDescription(
                PieceCount.FOUR_PIECE,
                Component.empty()
                         .append(Component.text("Increases DMG dealt to "))
                         .append(ElementalAnomalyType.BLEED.asComponent().append(Component.text("ing", ElementalAnomalyType.BLEED.getStyle())))
                         .append(Component.text(" enemies by "))
                         .append(fourPieceDamageBonus)
                         .append(Component.text("."))
        );
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handleHariantDamageComputeEvent(HariantDamageComputeEvent ev) {
        if (!(ev.getAttacker() instanceof HariantPlayer player)) {
            return;
        }
        
        if (!player.getHeroInstance().countArtifactSetPieces(this).isOrHigher(PieceCount.FOUR_PIECE)) {
            return;
        }
        
        final HariantEntity entity = ev.getEntity();
        
        if (!entity.isElementalAnomalyActive(ElementalAnomalyType.BLEED)) {
            return;
        }
        
        ev.mutateDamage(() -> "Open Wounds", DamageMutator.multiply(), 1 + fourPieceDamageBonus.doubleValue());
    }
    
    @Override
    public void applyEffect(@NotNull HariantPlayer player, @NotNull PieceCount pieceCount) {
        if (pieceCount.isOrHigher(PieceCount.TWO_PIECE)) {
            player.getAttributes().addModifier(new ModifierTwoPiece(player));
        }
    }
    
    public class ModifierTwoPiece extends AttributeModifierArtifactSet {
        
        ModifierTwoPiece(@NotNull HariantEntity applier) {
            super(ArtifactSetOpenWounds.this, PieceCount.TWO_PIECE, applier, twoPieceAttackIncrease);
        }
        
    }
    
}