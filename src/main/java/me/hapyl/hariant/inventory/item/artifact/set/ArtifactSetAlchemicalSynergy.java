package me.hapyl.hariant.inventory.item.artifact.set;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifierArtifactSet;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.ArtifactSetModifier;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.CommonArtifactSetModifiers;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class ArtifactSetAlchemicalSynergy extends ArtifactSet implements Listener {
    
    private final ArtifactSetModifier toxicDamageBonus = CommonArtifactSetModifiers.TOXIC_DAMAGE_BONUS;
    
    private final Decimal damageBoostPerDebuff = Decimal.ofPercentage(6);
    private final Decimal maxNumbersOfDebuffs = Decimal.ofValue(3);
    
    private final String identity;
    
    ArtifactSetAlchemicalSynergy(@NotNull Key key) {
        super(key, Component.text(identity = "Alchemical Synergy"));
        
        setArtifactTags(AttributeType.TOXIC_DAMAGE_BONUS);
        
        setPieceDescription(PieceCount.TWO_PIECE, toxicDamageBonus);
        
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
    
    @Override
    public @NotNull ElementType getEffectiveElementType() {
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
            super(ArtifactSetAlchemicalSynergy.this, PieceCount.TWO_PIECE, applier, HariantConstants.INDEFINITE_DURATION, toxicDamageBonus);
        }
    }
    
}
