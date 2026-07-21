package me.hapyl.hariant.inventory.item.artifact.set;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifierArtifactSet;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantElementalAnomalyEvent;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.ArtifactSetModifier;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.CommonArtifactSetModifiers;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.term.EnumTerminology;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class ArtifactSetTomeOfTheEnlightened extends ArtifactSet implements Listener {
    
    private final ArtifactSetModifier twoPieceElementalMasteryIncrease = CommonArtifactSetModifiers.ELEMENTAL_MASTERY;
    
    private final Decimal fourPieceElementalMasteryIncrease = Decimal.ofPercentage(100);
    private final Decimal fourPieceElementalMasteryIncreaseDuration = Decimal.ofSeconds(16);
    
    ArtifactSetTomeOfTheEnlightened(@NotNull Key key) {
        super(key, Component.text("Tome of the Enlightened"));
        
        setArtifactTags(AttributeType.ELEMENTAL_MASTERY);
        
        setPieceDescription(PieceCount.TWO_PIECE, twoPieceElementalMasteryIncrease);
        
        setPieceDescription(
                PieceCount.FOUR_PIECE,
                Component.empty()
                         .append(Component.text("Triggering an "))
                         .append(EnumTerminology.ELEMENTAL_ANOMALY)
                         .append(Component.text(" increases your and your teammates "))
                         .appendNewline()
                         .append(AttributeType.ELEMENTAL_MASTERY)
                         .append(Component.text(" by "))
                         .append(fourPieceElementalMasteryIncrease)
                         .append(Component.text(" for "))
                         .append(fourPieceElementalMasteryIncreaseDuration)
                         .append(Component.text("."))
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
        if (!(ev.getSource() instanceof HariantPlayer source)) {
            return;
        }
        
        final PieceCount pieceCount = source.getHeroInstance().countArtifactSetPieces(this);
        
        if (pieceCount != PieceCount.FOUR_PIECE) {
            return;
        }
        
        // Apply elemental mastery to self and all teammates
        final EnumTeam playerTeam = source.getPlayerTeam();
        
        Hariant.getPlayers()
               .filter(player -> player.getPlayerTeam() == playerTeam)
               .forEach(player -> player.getAttributes().addModifier(new ModifierFourPiece(source)));
    }
    
    public class ModifierTwoPiece extends AttributeModifierArtifactSet {
        ModifierTwoPiece(@NotNull HariantEntity applier) {
            super(ArtifactSetTomeOfTheEnlightened.this, PieceCount.TWO_PIECE, applier, twoPieceElementalMasteryIncrease);
        }
    }
    
    public class ModifierFourPiece extends AttributeModifierArtifactSet {
        ModifierFourPiece(@NotNull HariantEntity applier) {
            super(ArtifactSetTomeOfTheEnlightened.this, PieceCount.FOUR_PIECE, applier, fourPieceElementalMasteryIncreaseDuration.intValue());
            
            of(AttributeType.ELEMENTAL_MASTERY, AttributeModifierType.MULTIPLICATIVE, fourPieceElementalMasteryIncrease.doubleValue());
        }
    }
}
