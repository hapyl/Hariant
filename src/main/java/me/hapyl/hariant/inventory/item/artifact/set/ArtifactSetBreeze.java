package me.hapyl.hariant.inventory.item.artifact.set;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
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
import me.hapyl.hariant.inventory.item.artifact.set.modifier.ArtifactSetModifier;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.CommonArtifactSetModifiers;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class ArtifactSetBreeze extends ArtifactSet implements Listener {
    
    private final ArtifactSetModifier critChanceIncrease = CommonArtifactSetModifiers.CRIT_CHANCE;
    
    private final Decimal healthLostPercentage = Decimal.ofPercentage(5);
    private final Decimal healthLostLimit = Decimal.ofPercentage(20);
    
    private final Decimal fourPieceCritDamageIncrease = Decimal.ofAttribute(AttributeType.CRIT_DAMAGE, 10);
    private final Decimal fourPieceCritDamageIncreaseDuration = Decimal.ofSeconds(6f);
    
    ArtifactSetBreeze(@NotNull Key key) {
        super(key, Component.text("Breeze"));
        
        setArtifactTags(AttributeType.CRIT_CHANCE, AttributeType.CRIT_DAMAGE);
        
        setPieceDescription(PieceCount.TWO_PIECE, critChanceIncrease);
        
        setPieceDescription(
                PieceCount.FOUR_PIECE,
                Component.empty()
                         .append(Component.text("Whenever your health decreases, for every "))
                         .append(healthLostPercentage)
                         .append(Component.text(" of "))
                         .append(AttributeType.MAX_HEALTH)
                         .append(Component.text(" lost, increases your "))
                         .append(AttributeType.CRIT_DAMAGE)
                         .append(Component.text(" by "))
                         .append(fourPieceCritDamageIncrease)
                         .append(Component.text(" for "))
                         .append(fourPieceCritDamageIncreaseDuration)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("A maximum of %.0f ".formatted(healthLostLimit.divide(healthLostPercentage) * fourPieceCritDamageIncrease.doubleValue()), Colors.DARK_GRAY))
                         .append(AttributeType.CRIT_DAMAGE.asComponent().color(Colors.DARK_GRAY))
                         .append(Component.text(" can be gained this way.", Colors.DARK_GRAY))
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
        
        // Calculate the health difference of max health
        final double healthLostOfMaxHealth = Math.min(-ev.getHealthDifference() / player.getMaxHealth(), healthLostLimit.doubleValue());
        
        // Don't trigger for 0 health difference, which occurs when using Flower Breeze at minimum health threshold
        if (healthLostOfMaxHealth <= 0) {
            return;
        }
        
        final double critDamageIncreaseStacks = (int) (healthLostOfMaxHealth / healthLostPercentage.doubleValue());
        final double critDamageIncrease = fourPieceCritDamageIncrease.doubleValue() * critDamageIncreaseStacks;
        
        final AttributesInstance attributes = player.getAttributes();
        final ModifierFourPiece previousModifier = attributes.getModifier(ModifierFourPiece.class).orElse(null);
        
        // If the modifier already exists and the crit damage increase is higher than the new increase, we ignore it
        // to not override stronger modifier
        if (previousModifier != null && previousModifier.critDamageIncrease > critDamageIncrease) {
            return;
        }
        
        attributes.addModifier(new ModifierFourPiece(player, critDamageIncrease));
    }
    
    private class ModifierTwoPiece extends AttributeModifierArtifactSet {
        ModifierTwoPiece(@NotNull HariantEntity applier) {
            super(ArtifactSetBreeze.this, PieceCount.TWO_PIECE, applier, HariantConstants.INDEFINITE_DURATION, critChanceIncrease);
        }
    }
    
    private class ModifierFourPiece extends AttributeModifierArtifactSet {
        private final double critDamageIncrease;
        
        ModifierFourPiece(@NotNull HariantEntity applier, double critDamageIncrease) {
            super(ArtifactSetBreeze.this, PieceCount.FOUR_PIECE, applier, fourPieceCritDamageIncreaseDuration.intValue());
            
            of(AttributeType.CRIT_DAMAGE, AttributeModifierType.FLAT, this.critDamageIncrease = critDamageIncrease);
        }
    }
    
}
