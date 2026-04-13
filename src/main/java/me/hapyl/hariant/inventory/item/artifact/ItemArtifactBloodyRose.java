package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.attribute.instance.AttributesInstance;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantHealthChangeEvent;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemArtifactBloodyRose extends ItemArtifact {
    
    public ItemArtifactBloodyRose(@NotNull Key key) {
        super(key, Component.text("Bloody Rose"), Icon.ofTexture("676f0a8f03a79e4b19bef283b8915e1ffb0c42b311530b01550b31893d4b0742"), new ArtifactSetBreeze(key));
    }
    
    public static class ArtifactSetBreeze extends ArtifactSet implements Listener {
        
        private final Decimal critChanceIncrease = Decimal.ofAttributeBonus(AttributeType.CRIT_CHANCE, 20);
        
        private final Decimal critDamageIncreasePerHealthDecreased = Decimal.ofAttributeBonus(AttributeType.CRIT_DAMAGE, 20);
        private final Decimal critDamageIncreasePerHealthMaximum = Decimal.ofValue(60);
        private final Decimal critDamageIncreaseDuration = Decimal.ofSeconds(6f);
        
        private final Decimal healthLost = Decimal.ofValue(50);
        
        public ArtifactSetBreeze(@NotNull Key key) {
            super(key, Component.text("Breeze"));
            
            this.setPieceDescription(
                    PieceCount.TWO_PIECE,
                    Component.empty()
                             .append(AttributeType.CRIT_CHANCE)
                             .append(Component.text(" increased by "))
                             .append(critChanceIncrease)
                             .append(Component.text("."))
            );
            
            this.setPieceDescription(
                    PieceCount.FOUR_PIECE,
                    Component.empty()
                             .append(Component.text("Whenever your health decreases, for each "))
                             .append(healthLost)
                             .append(Component.text(" health lost, increases your "))
                             .append(AttributeType.CRIT_DAMAGE)
                             .append(Component.text(" by "))
                             .append(critDamageIncreasePerHealthDecreased)
                             .append(Component.text(" for "))
                             .append(critDamageIncreaseDuration)
                             .append(Component.text("."))
                             .appendNewline()
                             .append(Component.text("A maximum of "))
                             .append(critDamageIncreasePerHealthMaximum)
                             .appendSpace()
                             .append(AttributeType.CRIT_DAMAGE)
                             .append(Component.text(" can be gained this way."))
            );
        }
        
        @Override
        @Nullable
        public ElementType getEffectiveElementType() {
            return ElementType.PHYSICAL;
        }
        
        @Override
        public void applyEffect(@NotNull HariantPlayer player, @NotNull PieceCount pieceCount) {
            if (pieceCount.isOrHigher(PieceCount.TWO_PIECE)) {
                player.getAttributes().addModifier(new ModifierTwoPiece());
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
            
            attributes.addModifier(new ModifierFourPiece(critDamageIncrease));
        }
        
        public int calculateCritDamageIncrease(final double healthDifference) {
            final int lostHealthIncrease = (int) (healthDifference / healthLost.doubleValue());
            final int critDamageBonus = lostHealthIncrease * critDamageIncreasePerHealthDecreased.intValue();
            
            return Math.min(critDamageIncreasePerHealthMaximum.intValue(), critDamageBonus);
        }
        
        private class ModifierTwoPiece extends AttributeModifier {
            ModifierTwoPiece() {
                super(createModifierKey(PieceCount.TWO_PIECE), null, HariantConstants.INDEFINITE_DURATION);
                
                entries.add(entry(AttributeType.CRIT_CHANCE, AttributeModifierType.FLAT, critChanceIncrease.doubleValue()));
            }
        }
        
        private class ModifierFourPiece extends AttributeModifier {
            private final int critDamageIncrease;
            
            ModifierFourPiece(int critDamageIncrease) {
                super(createModifierKey(PieceCount.FOUR_PIECE), null, critDamageIncreaseDuration.intValue());
                
                entries.add(entry(AttributeType.CRIT_DAMAGE, AttributeModifierType.FLAT, critDamageIncrease));
                
                this.critDamageIncrease = critDamageIncrease;
            }
        }
        
    }
    
}
