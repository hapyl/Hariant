package me.hapyl.hariant.inventory.item.artifact.set;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstance;
import me.hapyl.hariant.attribute.modifier.AttributeModifierArtifactSet;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantHealthChangeEvent;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class ArtifactSetBreeze extends ArtifactSet implements Listener {
    
    private final Decimal critChanceIncrease = Decimal.ofAttribute(AttributeType.CRIT_CHANCE, 20);
    
    private final Decimal critDamageIncreasePerHealthDecreased = Decimal.ofAttribute(AttributeType.CRIT_DAMAGE, 20);
    private final Decimal critDamageIncreasePerHealthMaximum = Decimal.ofValue(40);
    private final Decimal critDamageIncreaseDuration = Decimal.ofSeconds(6f);
    
    private final Decimal healthLost = Decimal.ofValue(50);
    
    ArtifactSetBreeze(@NotNull Key key) {
        super(key, Component.text("Breeze"));
        
        setArtifactTags(AttributeType.CRIT_CHANCE, AttributeType.CRIT_DAMAGE);
        
        setPieceDescription(
                PieceCount.TWO_PIECE,
                Component.empty()
                         .append(AttributeType.CRIT_CHANCE)
                         .append(Component.text(" increased by "))
                         .append(critChanceIncrease)
                         .append(Component.text("."))
        );
        
        setPieceDescription(
                PieceCount.FOUR_PIECE,
                Component.empty()
                         .append(Component.text("Whenever your health decreases, for each "))
                         .append(healthLost)
                         .append(Component.text(" health lost, increases your "))
                         .appendNewline() // Manual newline because it cuts the CD char
                         .append(AttributeType.CRIT_DAMAGE)
                         .append(Component.text(" by "))
                         .append(critDamageIncreasePerHealthDecreased)
                         .append(Component.text(" for "))
                         .append(critDamageIncreaseDuration)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("A maximum of "))
                         .append(critDamageIncreasePerHealthMaximum)
                         .appendSpace()
                         .append(AttributeType.CRIT_DAMAGE)
                         .append(Component.text(" can be gained this way."))
        );
    }
    
    @Override
    public @NotNull ElementType getEffectiveElementType() {
        return ElementType.PHYSICAL;
    }
    
    @Override
    public void applyEffect(@NotNull HariantPlayer player, @NotNull PieceCount pieceCount) {
        if (pieceCount.isOrHigher(PieceCount.TWO_PIECE)) {
            player.getAttributes().addModifier(new ModifierTwoPiece(player));
        }
    }
    
    @EventHandler
    public void handleHealthDecreaseEvent(HariantHealthChangeEvent ev) {
        final HariantEntity entity = ev.getEntity();
        
        if (ev.isHealing() || !(entity instanceof HariantPlayer player)) {
            return;
        }
        
        if (!player.getHeroInstance().isArtifactSetPieceBonusActive(this, PieceCount.FOUR_PIECE)) {
            return;
        }
        
        final double healthDifference = ev.getHealthDifference();
        final int critDamageIncrease = calculateCritDamageIncrease(healthDifference);
        
        // Don't trigger for 0 health difference, which occurs when using Flower Breeze at minimum health threshold
        if (healthDifference == 0 || critDamageIncrease <= 0) {
            return;
        }
        
        final AttributesInstance attributes = player.getAttributes();
        final ModifierFourPiece previousModifier = attributes.getModifier(ModifierFourPiece.class).orElse(null);
        
        // If the modifier already exists and the crit damage increase is higher than the new increase, we ignore it
        // to not override stronger modifier
        if (previousModifier != null && previousModifier.critDamageIncrease > critDamageIncrease) {
            return;
        }
        
        attributes.addModifier(new ModifierFourPiece(player, critDamageIncrease));
    }
    
    public int calculateCritDamageIncrease(final double healthDifference) {
        final int lostHealthIncrease = (int) (healthDifference / healthLost.doubleValue());
        final int critDamageBonus = lostHealthIncrease * critDamageIncreasePerHealthDecreased.intValue();
        
        return Math.min(critDamageIncreasePerHealthMaximum.intValue(), critDamageBonus);
    }
    
    private class ModifierTwoPiece extends AttributeModifierArtifactSet {
        ModifierTwoPiece(@NotNull HariantEntity applier) {
            super(ArtifactSetBreeze.this, PieceCount.TWO_PIECE, applier, HariantConstants.INDEFINITE_DURATION);
            
            entries.add(entry(AttributeType.CRIT_CHANCE, AttributeModifierType.FLAT, critChanceIncrease.doubleValue()));
        }
    }
    
    private class ModifierFourPiece extends AttributeModifierArtifactSet {
        private final int critDamageIncrease;
        
        ModifierFourPiece(@NotNull HariantEntity applier, int critDamageIncrease) {
            super(ArtifactSetBreeze.this, PieceCount.FOUR_PIECE, applier, critDamageIncreaseDuration.intValue());
            
            entries.add(entry(AttributeType.CRIT_DAMAGE, AttributeModifierType.FLAT, critDamageIncrease));
            
            this.critDamageIncrease = critDamageIncrease;
        }
    }
    
}
