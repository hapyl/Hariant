package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstance;
import me.hapyl.hariant.attribute.modifier.AttributeModifierArtifactSet;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyBurn;
import me.hapyl.hariant.element.anomaly.EnumAnomaly;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemArtifactInfernalCrucible extends ItemArtifact {
    
    public ItemArtifactInfernalCrucible(@NotNull Key key) {
        super(key, Component.text("Infernal Crucible"), Icon.ofTemporaryTexture(), new ArtifactSetSearingInferno());
        
        setDescription(Component.text("A piece of forever burning infernal crucible that should burn hot, yet it doesn't."));
    }
    
    public static class ArtifactSetSearingInferno extends ArtifactSet implements Listener {
        
        @DisplayField private final Decimal elementalMasteryIncrease = Decimal.ofAttribute(AttributeType.ELEMENTAL_MASTERY, 120);
        @DisplayField private final Decimal burningDamageIncreaseOfElementalMastery = Decimal.ofPercentage(25);
        
        ArtifactSetSearingInferno() {
            super(Key.ofString("searing_inferno"), Component.text("Searing Inferno"));
            
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
                             .append(EnumAnomaly.BURN.asComponent())
                             .append(Component.text(" anomaly "))
                             .append(Component.text("DMG", Colors.ELEMENT_FIRE))
                             .append(Component.text(" by "))
                             .append(burningDamageIncreaseOfElementalMastery)
                             .append(Component.text(" of "))
                             .append(AttributeType.ELEMENTAL_MASTERY)
                             .append(Component.text("."))
            );
        }
        
        @Nullable
        @Override
        public ElementType getEffectiveElementType() {
            return ElementType.FIRE;
        }
        
        @Override
        public void applyEffect(@NotNull HariantPlayer player, @NotNull PieceCount pieceCount) {
            if (pieceCount.isOrHigher(PieceCount.TWO_PIECE)) {
                player.getAttributes().addModifier(new ModifierTwoPiece(player));
            }
        }
        
        @EventHandler
        public void handleHariantDamageEvent(HariantDamageEvent ev) {
            final DamageSource damageSource = ev.getDamageSource();
            
            if (!(ev.getAttacker() instanceof HariantPlayer player)) {
                return;
            }
            
            if (!player.getHeroInstance().countArtifactSetPieces(this).isOrHigher(PieceCount.FOUR_PIECE)) {
                return;
            }
            
            if (!(damageSource instanceof ElementalAnomalyBurn.ElementalAnomalyBurnDamageSource)) {
                return;
            }
            
            final double additiveDamage = calculateAdditiveDamage(player);
            
            ev.mutateDamage(() -> "Searing Inferno", DamageMutator.add(), additiveDamage);
        }
        
        public double calculateAdditiveDamage(@NotNull HariantPlayer player) {
            final AttributesInstance attributes = player.getAttributes();
            final double elementalMastery = attributes.get(AttributeType.ELEMENTAL_MASTERY);
            
            return elementalMastery * burningDamageIncreaseOfElementalMastery.doubleValue();
        }
        
        public class ModifierTwoPiece extends AttributeModifierArtifactSet {
            ModifierTwoPiece(@NotNull HariantPlayer applier) {
                super(ArtifactSetSearingInferno.this, PieceCount.TWO_PIECE, applier, HariantConstants.INDEFINITE_DURATION);
                
                of(AttributeType.ELEMENTAL_MASTERY, AttributeModifierType.FLAT, elementalMasteryIncrease);
            }
        }
    }
    
}