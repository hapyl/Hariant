package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstance;
import me.hapyl.hariant.attribute.modifier.AttributeModifierArtifactSet;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.cooldown.Cooldown;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.talent.ultimate.TalentUltimateResource;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemArtifactUnstableLightningGem extends ItemArtifact {
    
    public ItemArtifactUnstableLightningGem(@NotNull Key key) {
        super(key, Component.text("Unstable Lightning Gem"), Icon.ofTexture("2136e8a543621bb511e386507aeae4613928682649bd147d0f7939411cb49081"), new ArtifactSetElectrifying(key));
    }
    
    public static class ArtifactSetElectrifying extends ArtifactSet implements Listener {
        
        private final Decimal electricDamageBonus = Decimal.ofAttributeBonus(AttributeType.ELECTRIC_DAMAGE_BONUS, 20);
        private final Decimal energyRegeneration = Decimal.ofValue(8);
        
        private final Cooldown energyRegenerationCooldown = Cooldown.ofSeconds(Key.ofString("electrifying"), 6f);
        
        public ArtifactSetElectrifying(@NotNull Key key) {
            super(key, Component.text("Electrifying"));
            
            setPieceDescription(
                    PieceCount.TWO_PIECE,
                    Component.empty()
                             .append(Component.text("Increases "))
                             .append(ElementType.ELECTRIC.asComponentDamage())
                             .append(Component.text(" dealt by "))
                             .append(electricDamageBonus)
                             .append(Component.text("."))
            );
            
            setPieceDescription(
                    PieceCount.FOUR_PIECE,
                    Component.empty()
                             .append(Component.text("Dealing "))
                             .append(ElementType.ELECTRIC.asComponentDamage())
                             .append(Component.text(" generates "))
                             .append(energyRegeneration)
                             .appendSpace()
                             .append(TalentUltimateResource.ENERGY)
                             .append(Component.text("."))
                             .appendNewline()
                             .append(Component.text("This effect can only occur once every "))
                             .append(energyRegenerationCooldown.getCooldownFormatted())
                             .append(Component.text("."))
            );
        }
        
        @Nullable
        @Override
        public ElementType getEffectiveElementType() {
            return ElementType.ELECTRIC;
        }
        
        @Override
        public void applyEffect(@NotNull HariantPlayer player, @NotNull PieceCount pieceCount) {
            final AttributesInstance attributes = player.getAttributes();
            
            if (pieceCount.isOrHigher(PieceCount.TWO_PIECE)) {
                attributes.addModifier(new ModifierTwoPiece());
            }
        }
        
        @EventHandler
        public void handleHariantDamageEvent(HariantDamageEvent ev) {
            if (ev.getElementType() != ElementType.ELECTRIC) {
                return;
            }
            
            if (!(ev.getAttacker() instanceof HariantPlayer player)) {
                return;
            }
            
            final PieceCount pieceCount = player.getHeroInstance().countArtifactSetPieces(this);
            
            if (!pieceCount.isOrHigher(PieceCount.FOUR_PIECE)) {
                return;
            }
            
            final TalentUltimateResource ultimateResource = player.getHeroInstance().getOrigin().getUltimateTalent().getResource();
            
            if (ultimateResource != TalentUltimateResource.ENERGY) {
                return;
            }
            
            if (player.isOnCooldown(energyRegenerationCooldown)) {
                return;
            }
            
            player.setCooldown(energyRegenerationCooldown);
            player.incrementUltimateResource(energyRegeneration.doubleValue());
        }
        
        private class ModifierTwoPiece extends AttributeModifierArtifactSet {
            
            ModifierTwoPiece() {
                super(ArtifactSetElectrifying.this, PieceCount.TWO_PIECE, null, HariantConstants.INDEFINITE_DURATION);
                
                entries.add(entry(AttributeType.ELECTRIC_DAMAGE_BONUS, AttributeModifierType.FLAT, electricDamageBonus.doubleValue()));
            }
            
        }
        
    }
}
