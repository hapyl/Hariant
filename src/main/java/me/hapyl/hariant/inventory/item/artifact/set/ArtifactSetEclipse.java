package me.hapyl.hariant.inventory.item.artifact.set;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifierArtifactSet;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.cooldown.Cooldown;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantShieldCreateEvent;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.ArtifactSetModifier;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.CommonArtifactSetModifiers;
import me.hapyl.hariant.term.EnumTerminology;
import me.hapyl.hariant.ui.ComponentDisplay;
import me.hapyl.hariant.ui.ComponentDisplayAnimation;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class ArtifactSetEclipse extends ArtifactSet implements Listener {
    
    private final ArtifactSetModifier twoPieceBonus = CommonArtifactSetModifiers.MAX_HEALTH;
    
    private final Decimal fourPieceDamageIncrease = Decimal.ofAttribute(AttributeType.AETHER_DAMAGE_BONUS, 10);
    private final Decimal fourPieceDamageIncreaseDuration = Decimal.ofSeconds(6);
    
    private final Cooldown fourPieceCooldown = Cooldown.ofSeconds(Key.ofString("eclipse_cooldown"), 2f);
    
    ArtifactSetEclipse(@NotNull Key key) {
        super(key, Component.text("Eclipse"));
        
        setArtifactTags(AttributeType.AETHER_DAMAGE_BONUS);
        
        setPieceDescription(PieceCount.TWO_PIECE, twoPieceBonus);
        
        setPieceDescription(
                PieceCount.FOUR_PIECE,
                Component.empty()
                         .append(Component.text("Shielding a "))
                         .append(Component.text("teammate", Colors.GREEN))
                         .append(Component.text(" increases their"))
                         .append(EnumTerminology.ALL_TYPE_DAMAGE)
                         .append(Component.text(" by "))
                         .append(fourPieceDamageIncrease)
                         .append(Component.text(" for "))
                         .append(fourPieceDamageIncreaseDuration)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("This effect can only trigger once every "))
                         .append(fourPieceCooldown)
                         .append(Component.text("."))
        );
    }
    
    @Override
    public @NotNull ElementType getEffectiveElementType() {
        return ElementType.AETHER;
    }
    
    @Override
    public void applyEffect(@NotNull HariantPlayer player, @NotNull PieceCount pieceCount) {
        if (pieceCount.isOrHigher(PieceCount.TWO_PIECE)) {
            player.getAttributes().addModifier(new ModifierTwoPiece(player));
        }
    }
    
    @EventHandler
    public void handleHariantShieldEvent(HariantShieldCreateEvent ev) {
        if (!(ev.getApplier() instanceof HariantPlayer player)) {
            return;
        }
        
        if (!player.getHeroInstance().countArtifactSetPieces(this).isOrHigher(PieceCount.FOUR_PIECE)) {
            return;
        }
        
        if (player.hasCooldown(fourPieceCooldown)) {
            return;
        }
        
        ev.getEntity().getAttributes().addModifier(new ModifierFourPiece(player));
        player.setCooldown(fourPieceCooldown);
    }
    
    public class ModifierTwoPiece extends AttributeModifierArtifactSet {
        
        ModifierTwoPiece(@NotNull HariantEntity applier) {
            super(ArtifactSetEclipse.this, PieceCount.TWO_PIECE, applier, HariantConstants.INDEFINITE_DURATION, twoPieceBonus);
        }
        
    }
    
    public class ModifierFourPiece extends AttributeModifierArtifactSet {
        
        private static final ComponentDisplay COMPONENT_DISPLAY = new ComponentDisplay(Component.text("❍ Eclipse", Colors.ELEMENT_AETHER), ComponentDisplayAnimation.ofSineAscend(), 20, 1.5f);
        
        ModifierFourPiece(@NotNull HariantEntity applier) {
            super(ArtifactSetEclipse.this, PieceCount.FOUR_PIECE, applier, fourPieceDamageIncreaseDuration.intValue());
            
            ofElementalDamageBonus(AttributeModifierType.FLAT, fourPieceDamageIncrease.doubleValue());
        }
        
        @Override
        public void display(@NotNull Location location) {
            COMPONENT_DISPLAY.display(location);
        }
    }
    
}
