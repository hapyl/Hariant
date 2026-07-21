package me.hapyl.hariant.inventory.item.artifact.set;

import me.hapyl.eterna.module.player.sequencer.Sequencer;
import me.hapyl.eterna.module.player.sequencer.Track;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifierArtifactSet;
import me.hapyl.hariant.entity.cooldown.HariantCooldown;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantTalentEvent;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.ArtifactSetModifier;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.CommonArtifactSetModifiers;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class ArtifactSetRewind extends ArtifactSet implements Listener {
    
    private final ArtifactSetModifier cooldownReductionIncrease = CommonArtifactSetModifiers.COOLDOWN_REDUCTION;
    
    private final Decimal rewindChance = Decimal.ofPercentage(25);
    private final HariantCooldown rewindCooldown = HariantCooldown.ofSeconds(Key.ofString("rewind_cooldown"), 12);
    
    private final Sequencer sequencer = Sequencer.singleTrack(
            Hariant.getPlugin(),
            Track.builder("a-----b-----c")
                 .where('a', Sound.BLOCK_DISPENSER_DISPENSE, 0.7f)
                 .where('b', Sound.BLOCK_DISPENSER_DISPENSE, 0.8f)
                 .where('c', Sound.BLOCK_DISPENSER_DISPENSE, 0.9f)
    );
    
    ArtifactSetRewind(@NotNull Key key) {
        super(key, Component.text("Rewind"));
        
        setArtifactTags(AttributeType.COOLDOWN_REDUCTION);
        
        setPieceDescription(PieceCount.TWO_PIECE, cooldownReductionIncrease);
        
        setPieceDescription(
                PieceCount.FOUR_PIECE,
                Component.empty()
                         .append(Component.text("Using a talent has "))
                         .append(rewindChance)
                         .append(Component.text(" chance to "))
                         .append(Component.text("reset", Colors.SUCCESS))
                         .append(Component.text(" that talent's cooldown."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Can only occur once every "))
                         .append(rewindCooldown)
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
    public void handleHariantTalentEvent(HariantTalentEvent ev) {
        final HariantPlayer player = ev.getPlayer();
        final Talent talent = ev.getTalent();
        final PieceCount pieceCount = player.getHeroInstance().countArtifactSetPieces(this);
        
        if (talent instanceof TalentUltimate || pieceCount.isLower(PieceCount.FOUR_PIECE) || !ev.getResponse().isOk() || player.hasCooldown(rewindCooldown)) {
            return;
        }
        
        // Check for chance
        if (!player.getRandom().chance(rewindChance)) {
            return;
        }
        
        player.setCooldown(talent, 0);
        player.setCooldown(rewindCooldown);
        
        // Fx
        player.sendSubtitle(Component.text("⌚", Colors.ATTRIBUTE_COOLDOWN_REDUCTION), 0, 10, 5);
        sequencer.play(player.getHandle());
    }
    
    private final class ModifierTwoPiece extends AttributeModifierArtifactSet {
        ModifierTwoPiece(@NotNull HariantPlayer applier) {
            super(ArtifactSetRewind.this, PieceCount.TWO_PIECE, applier, cooldownReductionIncrease);
        }
    }
    
}