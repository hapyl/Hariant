package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifierArtifactSet;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantTalentUltimateEvent;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class ItemArtifactLightningInABottle extends ItemArtifact {
    
    public ItemArtifactLightningInABottle(@NotNull Key key) {
        super(key, Component.text("Lightning In a Bottle"), Icon.ofTexture("f0f2185c51b6d7cee7e17042de2ddf8a448552af7b4af1804986fbdd09e9bc08"), new ArtifactSetRecharge());
        
        setDescription(
                Component.text("A lightning trapped in a bottle that apparently can be used as a quick energy refill.")
        );
    }
    
    private static class ArtifactSetRecharge extends ArtifactSet implements Listener {
        
        private final @DisplayField Decimal energyRechargeIncrease = Decimal.ofAttribute(AttributeType.ENERGY_RECHARGE, 25);
        private final @DisplayField Decimal energyRefund = Decimal.ofPercentage(20);
        
        public ArtifactSetRecharge() {
            super(Key.ofString("artifact_set_recharge"), Component.text("Recharge"));
            
            setPieceDescription(
                    PieceCount.TWO_PIECE,
                    Component.empty()
                             .append(Component.text("Increases "))
                             .append(AttributeType.ENERGY_RECHARGE)
                             .append(Component.text(" by "))
                             .append(energyRechargeIncrease)
                             .append(Component.text("."))
            );
            
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
                super(ArtifactSetRecharge.this, PieceCount.TWO_PIECE, applier, HariantConstants.INDEFINITE_DURATION);
                
                of(AttributeType.ENERGY_RECHARGE, AttributeModifierType.FLAT, energyRechargeIncrease.doubleValue());
            }
            
        }
    }
    
}