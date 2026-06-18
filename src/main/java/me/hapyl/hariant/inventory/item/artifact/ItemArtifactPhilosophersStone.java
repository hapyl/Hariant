package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifierArtifactSet;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemArtifactPhilosophersStone extends ItemArtifact {
    public ItemArtifactPhilosophersStone(@NotNull Key key) {
        super(key, Component.text("Philosopher's Stone"), Icon.ofTexture("1cf948805dcb43a2cf0406aab37a412ceeff82a6ba1541a11d6c8307555d1aa6"), new ArtifactSetAlchemicalSynergy(key));
        
        setDescription(Component.text("An alchemical substance resembling a stone, said to be capable of turning lead to gold."));
    }
    
    public static class ArtifactSetAlchemicalSynergy extends ArtifactSet implements Listener {
        
        private final Decimal toxicDamageBonus = Decimal.ofAttribute(AttributeType.TOXIC_DAMAGE_BONUS, 20);
        
        private final Decimal damageBoostPerDebuff = Decimal.ofPercentage(6);
        private final Decimal maxNumbersOfDebuffs = Decimal.ofValue(3);
        
        private final String identity;
        
        ArtifactSetAlchemicalSynergy(@NotNull Key key) {
            super(key, Component.text(identity = "Alchemical Synergy"));
            
            setPieceDescription(
                    PieceCount.TWO_PIECE,
                    Component.empty()
                             .append(Component.text("Increases "))
                             .append(ElementType.TOXIC.asComponentDamage())
                             .append(Component.text(" dealt by "))
                             .append(toxicDamageBonus)
                             .append(Component.text("."))
            );
            
            setPieceDescription(
                    PieceCount.FOUR_PIECE,
                    Component.empty()
                             .append(Component.text("Increases the damage dealt by "))
                             .append(damageBoostPerDebuff)
                             .append(Component.text(" for each unique "))
                             .append(Component.text("de-buff", Colors.ERROR))
                             .append(Component.text(" active, up to "))
                             .append(Component.text("%.0f%%".formatted(damageBoostPerDebuff.value() * maxNumbersOfDebuffs.value()), Colors.NUMBER))
                             .append(Component.text("."))
            );
        }
        
        @Nullable
        @Override
        public ElementType getEffectiveElementType() {
            return ElementType.TOXIC;
        }
        
        @Override
        public void applyEffect(@NotNull HariantPlayer player, @NotNull PieceCount pieceCount) {
            if (pieceCount.isOrHigher(PieceCount.TWO_PIECE)) {
                player.getAttributes().addModifier(new ModifierTwoPiece(player));
            }
        }
        
        @EventHandler
        public void handleHariantDamageEvent(HariantDamageEvent ev) {
            final HariantEntity attacker = ev.getAttacker();
            
            if (!(attacker instanceof HariantPlayer playerAttacker)) {
                return;
            }
            
            final PieceCount pieceCount = playerAttacker.getHeroInstance().countArtifactSetPieces(this);
            
            if (pieceCount != PieceCount.FOUR_PIECE) {
                return;
            }
            
            final long debuffCount = playerAttacker.countEffects(EffectType.DEBUFF);
            
            if (debuffCount == 0) {
                return;
            }
            
            final double damageBoost = 1 + damageBoostPerDebuff.doubleValue() * Math.min(debuffCount, maxNumbersOfDebuffs.intValue());
            
            ev.mutateDamage(() -> identity, DamageMutator.multiply(), damageBoost);
        }
        
        private class ModifierTwoPiece extends AttributeModifierArtifactSet {
            ModifierTwoPiece(@NotNull HariantEntity applier) {
                super(ArtifactSetAlchemicalSynergy.this, PieceCount.TWO_PIECE, applier, HariantConstants.INDEFINITE_DURATION);
                
                of(AttributeType.TOXIC_DAMAGE_BONUS, AttributeModifierType.FLAT, toxicDamageBonus.doubleValue());
            }
        }
        
    }
    
}