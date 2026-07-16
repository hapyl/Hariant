package me.hapyl.hariant.inventory.item.artifact.set;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifierArtifactSet;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.cooldown.Cooldown;
import me.hapyl.hariant.entity.heal.HealingSource;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantElementalAnomalyEvent;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.ArtifactSetModifier;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.CommonArtifactSetModifiers;
import me.hapyl.hariant.term.EnumTerminology;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class ArtifactSetBloodscent extends ArtifactSet implements Listener {
    
    private final ArtifactSetModifier attackIncrease = CommonArtifactSetModifiers.ATTACK;
    
    private final AttributeScaling healing = AttributeScaling.create(
            Map.of(AttributeType.ATTACK, 50.0, AttributeType.ELEMENTAL_MASTERY, 25.0),
            25
    );
    
    private final Cooldown cooldown = Cooldown.ofSeconds(Key.ofString("bloodscent_healing"), 0.1f);
    
    ArtifactSetBloodscent(@NotNull Key key) {
        super(key, Component.text("Bloodscent"));
        
        setArtifactTags(AttributeType.ATTACK);
        
        setPieceDescription(PieceCount.TWO_PIECE, attackIncrease);
        
        setPieceDescription(
                PieceCount.FOUR_PIECE,
                Component.empty()
                         .append(Component.text("Triggering "))
                         .append(EnumTerminology.ELEMENTAL_ANOMALY)
                         .append(Component.text(" heals you based on your "))
                         .append(AttributeType.ATTACK)
                         .append(Component.text(" and "))
                         .append(AttributeType.ELEMENTAL_MASTERY)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("This effect can only occur once every ", Colors.DARK_GRAY))
                         .append(cooldown.asComponent().color(Colors.DARK_GRAY))
                         .append(Component.text(".", Colors.DARK_GRAY))
        );
    }
    
    @Override
    public void applyEffect(@NotNull HariantPlayer player, @NotNull PieceCount pieceCount) {
        if (pieceCount.isOrHigher(PieceCount.TWO_PIECE)) {
            player.getAttributes().addModifier(new ModifierTwoPiece(player));
        }
    }
    
    @EventHandler
    public void handleHariantElementalAnomalyEvent(HariantElementalAnomalyEvent ev) {
        if (!(ev.getSource() instanceof HariantPlayer player)) {
            return;
        }
        
        if (!player.getHeroInstance().countArtifactSetPieces(this).isOrHigher(PieceCount.FOUR_PIECE)) {
            return;
        }
        
        if (player.hasCooldown(cooldown)) {
            return;
        }
        
        player.heal(HealingSource.create(healing.getScaledValue(player), this));
        player.setCooldown(cooldown);
    }
    
    private class ModifierTwoPiece extends AttributeModifierArtifactSet {
        ModifierTwoPiece(@NotNull HariantEntity applier) {
            super(ArtifactSetBloodscent.this, PieceCount.TWO_PIECE, applier, HariantConstants.INDEFINITE_DURATION, attackIncrease);
        }
    }
    
}