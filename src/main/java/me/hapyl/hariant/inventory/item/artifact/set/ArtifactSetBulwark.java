package me.hapyl.hariant.inventory.item.artifact.set;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstance;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantHealthChangeEvent;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class ArtifactSetBulwark extends ArtifactSet implements Listener {
    
    private final Decimal defenseIncrease = Decimal.ofPercentage(20);
    
    private final Decimal fourPieceHealthThreshold = Decimal.ofPercentage(50);
    private final Decimal fourPieceEffectIncrease = Decimal.ofPercentage(100);
    
    ArtifactSetBulwark(@NotNull Key key) {
        super(key, Component.text("Bulwark"));
        
        setPieceDescription(
                PieceCount.TWO_PIECE,
                Component.empty()
                         .append(Component.text("Increases "))
                         .append(AttributeType.DEFENSE)
                         .append(Component.text(" by "))
                         .append(defenseIncrease)
                         .append(Component.text("."))
        );
        
        setPieceDescription(
                PieceCount.FOUR_PIECE,
                Component.empty()
                         .append(Component.text("When "))
                         .append(Component.text("health", Colors.GREEN))
                         .append(Component.text(" drops below "))
                         .append(fourPieceHealthThreshold)
                         .append(Component.text(", increases the two-piece bonus by "))
                         .append(fourPieceEffectIncrease)
                         .append(Component.text("."))
        );
    }
    
    @Override
    public void applyEffect(@NotNull HariantPlayer player, @NotNull PieceCount pieceCount) {
        this.applyModifier(player, false);
    }
    
    @EventHandler
    public void handleHealthUpdateEvent(HariantHealthChangeEvent ev) {
        if (!(ev.getEntity() instanceof HariantPlayer player)) {
            return;
        }
        
        if (!player.getHeroInstance().countArtifactSetPieces(this).isOrHigher(PieceCount.FOUR_PIECE)) {
            return;
        }
        
        // Max health is an attribute, so it can easily be 0, even though it should never be ¯\_(ツ)_/¯
        final double healthPercentageOfMaxHealth = ev.getNewHealth() / Math.max(1, player.getMaxHealth());
        
        this.applyModifier(player, healthPercentageOfMaxHealth <= fourPieceHealthThreshold.doubleValue());
    }
    
    private void applyModifier(@NotNull HariantPlayer player, boolean isFourPieceBonus) {
        final AttributesInstance attributes = player.getAttributes();
        
        // If player already has modifier and the effect is the same, just skip it
        if (attributes.getModifier(this.getKey()).orElse(null) instanceof Modifier modifier && modifier.isFourPieceBonus == isFourPieceBonus) {
            return;
        }
        
        // Otherwise add the modifier
        attributes.addModifier(new Modifier(player, isFourPieceBonus));
    }
    
    private class Modifier extends AttributeModifier {
        
        private final boolean isFourPieceBonus;
        
        Modifier(@NotNull HariantEntity applier, boolean isFourPieceBonus) {
            super(ArtifactSetBulwark.this.getKey(), ArtifactSetBulwark.this.getName(), applier, HariantConstants.INDEFINITE_DURATION);
            this.isFourPieceBonus = isFourPieceBonus;
            
            of(AttributeType.DEFENSE, AttributeModifierType.MULTIPLICATIVE, defenseIncrease.doubleValue() * (isFourPieceBonus ? 1 + fourPieceEffectIncrease.doubleValue() : 1));
        }
    }
    
}