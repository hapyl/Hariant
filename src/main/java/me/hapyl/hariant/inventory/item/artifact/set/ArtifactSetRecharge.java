package me.hapyl.hariant.inventory.item.artifact.set;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifierArtifactSet;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantTalentUltimateEvent;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.ArtifactSetModifier;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.CommonArtifactSetModifiers;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class ArtifactSetRecharge extends ArtifactSet implements Listener {
    
    private final ArtifactSetModifier energyRechargeIncrease = CommonArtifactSetModifiers.ENERGY_RECHARGE;
    private final Decimal energyRefund = Decimal.ofPercentage(20);
    
    ArtifactSetRecharge(@NotNull Key key) {
        super(key, Component.text("Recharge"));
        
        setArtifactTags(AttributeType.ENERGY_RECHARGE);
        
        setPieceDescription(PieceCount.TWO_PIECE, energyRechargeIncrease);
        
        setPieceDescription(
                PieceCount.FOUR_PIECE,
                Component.empty()
                         .append(Component.text("After using an "))
                         .append(Component.text("ultimate", Colors.ULTIMATE_RESOURCE_ENERGY))
                         .append(Component.text(", refund "))
                         .append(energyRefund)
                         .append(Component.text(" of the "))
                         .append(UltimateResourceType.ENERGY)
                         .append(Component.text(" spent."))
        );
    }
    
    @Override
    public void applyEffect(@NotNull HariantPlayer player, @NotNull PieceCount pieceCount) {
        player.getAttributes().addModifier(new ModifierTwoPiece(player));
    }
    
    @EventHandler
    public void handleHariantTalentEvent(HariantTalentUltimateEvent ev) {
        final HariantPlayer player = ev.getPlayer();
        
        if (ev.getResourceType() != UltimateResourceType.ENERGY) {
            return;
        }
        
        if (!player.getHeroInstance().countArtifactSetPieces(this).isOrHigher(PieceCount.FOUR_PIECE)) {
            return;
        }
        
        final double resourceConsumed = ev.getResourceConsumed();
        final double refund = Math.max(0, resourceConsumed * energyRefund.doubleValue());
        
        if (refund <= 0) {
            return;
        }
        
        player.incrementUltimateResource(refund, false);
    }
    
    public class ModifierTwoPiece extends AttributeModifierArtifactSet {
        
        ModifierTwoPiece(@NotNull HariantEntity applier) {
            super(ArtifactSetRecharge.this, PieceCount.TWO_PIECE, applier, HariantConstants.INDEFINITE_DURATION, energyRechargeIncrease);
        }
        
    }
}
