package me.hapyl.hariant.inventory.item.artifact.set;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.modifier.AttributeModifierArtifactSet;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageComputeEvent;
import me.hapyl.hariant.event.HariantElementalAnomalyEvent;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.ArtifactSetModifier;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.CommonArtifactSetModifiers;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class ArtifactSetProofOfPower extends ArtifactSet implements Listener {
    
    private final ArtifactSetModifier twoPieceBonus = CommonArtifactSetModifiers.PHYSICAL_DAMAGE_BONUS;
    
    private final Decimal fourPieceDamageBonus = Decimal.ofPercentage(20);
    private final Decimal fourPieceDamageBonusDuration = Decimal.ofSeconds(10);
    
    private final AttributeModifierArtifactSet.ModifierKey fourPieceModifierKey = AttributeModifierArtifactSet.ModifierKey.create(this, PieceCount.FOUR_PIECE);
    
    ArtifactSetProofOfPower(@NotNull Key key) {
        super(key, Component.text("Proof of Power"));
        
        setPieceDescription(PieceCount.TWO_PIECE, twoPieceBonus);
        
        setPieceDescription(
                PieceCount.FOUR_PIECE,
                Component.empty()
                         .append(Component.text("Triggering "))
                         .append(ElementalAnomalyType.FROZEN)
                         .append(Component.text(" anomaly increases "))
                         .append(Component.text("DMG", Colors.RED))
                         .append(Component.text(" dealt by "))
                         .append(fourPieceDamageBonus)
                         .append(Component.text(" for "))
                         .append(fourPieceDamageBonusDuration)
                         .append(Component.text("."))
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void handleHariantDamageComputeEvent(HariantDamageComputeEvent ev) {
        if (!(ev.getAttacker() instanceof HariantPlayer player)) {
            return;
        }
        
        if (!player.getAttributes().hasModifier(fourPieceModifierKey.key())) {
            return;
        }
        
        ev.mutateDamage(() -> "Proof of Power", DamageMutator.multiply(), 1 + fourPieceDamageBonus.doubleValue());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handleHariantElementalAnomalyEvent(HariantElementalAnomalyEvent ev) {
        if (!(ev.getSource() instanceof HariantPlayer player)) {
            return;
        }
        
        if (ev.getElementalAnomaly() != ElementalAnomalyType.FROZEN) {
            return;
        }
        
        if (!player.getHeroInstance().countArtifactSetPieces(this).isOrHigher(PieceCount.FOUR_PIECE)) {
            return;
        }
        
        player.getAttributes().addModifier(new ModifierFourPiece(player));
    }
    
    @Override
    public void applyEffect(@NotNull HariantPlayer player, @NotNull PieceCount pieceCount) {
        if (pieceCount.isOrHigher(PieceCount.TWO_PIECE)) {
            player.getAttributes().addModifier(new ModifierTwoPiece(player));
        }
    }
    
    public class ModifierTwoPiece extends AttributeModifierArtifactSet {
        
        ModifierTwoPiece(@NotNull HariantEntity applier) {
            super(ArtifactSetProofOfPower.this, PieceCount.TWO_PIECE, applier, twoPieceBonus);
        }
        
    }
    
    public class ModifierFourPiece extends AttributeModifierArtifactSet {
        
        ModifierFourPiece(@NotNull HariantEntity applier) {
            super(fourPieceModifierKey, applier, fourPieceDamageBonusDuration.intValue());
        }
        
        @Override
        public @NotNull EffectType getEffectType() {
            return EffectType.BUFF;
        }
        
    }
    
}