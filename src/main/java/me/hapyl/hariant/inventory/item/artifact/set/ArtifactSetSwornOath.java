package me.hapyl.hariant.inventory.item.artifact.set;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifierArtifactSet;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.ArtifactSetModifier;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.CommonArtifactSetModifiers;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class ArtifactSetSwornOath extends ArtifactSet implements Listener {
    
    private final ArtifactSetModifier ferocityIncrease = CommonArtifactSetModifiers.FEROCITY;
    
    private final Decimal fourPieceAetherDamageBonus = Decimal.ofAttribute(AttributeType.AETHER_DAMAGE_BONUS, 20);
    private final Decimal fourPieceAetherDamageBonusDuration = Decimal.ofSeconds(6);
    
    ArtifactSetSwornOath(@NotNull Key key) {
        super(key, Component.text("Sworn Oath"));
        
        setArtifactTags(AttributeType.FEROCITY, AttributeType.AETHER_DAMAGE_BONUS);
        
        setPieceDescription(PieceCount.TWO_PIECE, ferocityIncrease);
        setPieceDescription(
                PieceCount.FOUR_PIECE,
                Component.empty()
                         .append(Component.text("Dealing "))
                         .append(DamageType.FEROCITY)
                         .append(Component.text(" increases "))
                         .append(ElementType.AETHER.asComponentDamage())
                         .append(Component.text(" dealt by "))
                         .append(fourPieceAetherDamageBonus)
                         .append(Component.text(" for "))
                         .append(fourPieceAetherDamageBonusDuration)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Repeated triggers reset the durations.", Colors.DARK_GRAY))
        );
    }
    
    @Override
    public @NotNull ElementType getEffectiveElementType() {
        return ElementType.AETHER;
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handleHariantDamageEvent(HariantDamageEvent ev) {
        if (!(ev.getAttacker() instanceof HariantPlayer player)) {
            return;
        }
        
        if (ev.getDamageSource().getDamageType() != DamageType.FEROCITY) {
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
    
    private class ModifierTwoPiece extends AttributeModifierArtifactSet {
        
        ModifierTwoPiece(@NotNull HariantEntity applier) {
            super(ArtifactSetSwornOath.this, PieceCount.TWO_PIECE, applier, ferocityIncrease);
        }
        
    }
    
    private class ModifierFourPiece extends AttributeModifierArtifactSet {
        
        ModifierFourPiece(@NotNull HariantEntity applier) {
            super(ArtifactSetSwornOath.this, PieceCount.FOUR_PIECE, applier, fourPieceAetherDamageBonusDuration.intValue());
         
            of(AttributeType.AETHER_DAMAGE_BONUS, AttributeModifierType.FLAT, fourPieceAetherDamageBonus.doubleValue());
        }
        
        @Override
        public void display(@NotNull Location location) {
        }
        
    }
    
}