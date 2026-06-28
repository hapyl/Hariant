package me.hapyl.hariant.inventory.item.artifact.set;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstance;
import me.hapyl.hariant.attribute.modifier.AttributeModifierArtifactSet;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.cooldown.Cooldown;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ArtifactSetElectrifying extends ArtifactSet implements Listener {
    
    private final Decimal electricDamageBonus = Decimal.ofAttribute(AttributeType.ELECTRIC_DAMAGE_BONUS, 20);
    private final Decimal energyRegeneration = Decimal.ofValue(8);
    
    private final Cooldown energyRegenerationCooldown = Cooldown.ofSeconds(Key.ofString("electrifying"), 6f);
    
    ArtifactSetElectrifying(@NotNull Key key) {
        super(key, Component.text("Electrifying"));
        
        setArtifactTags(AttributeType.ELECTRIC_DAMAGE_BONUS);
        
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
                         .append(UltimateResourceType.ENERGY)
                         .append(Component.text("."))
                         .appendNewline()
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
            attributes.addModifier(new ModifierTwoPiece(player));
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
        
        final UltimateResourceType ultimateResourceType = player.getHeroInstance().getOrigin().getUltimateTalent().getUltimateResourceType();
        
        if (ultimateResourceType != UltimateResourceType.ENERGY) {
            return;
        }
        
        if (player.hasCooldown(energyRegenerationCooldown)) {
            return;
        }
        
        player.setCooldown(energyRegenerationCooldown);
        player.incrementUltimateResource(energyRegeneration.doubleValue());
    }
    
    private class ModifierTwoPiece extends AttributeModifierArtifactSet {
        ModifierTwoPiece(@NotNull HariantEntity applier) {
            super(ArtifactSetElectrifying.this, PieceCount.TWO_PIECE, applier, HariantConstants.INDEFINITE_DURATION);
            
            entries.add(entry(AttributeType.ELECTRIC_DAMAGE_BONUS, AttributeModifierType.FLAT, electricDamageBonus.doubleValue()));
        }
        
    }
    
}
