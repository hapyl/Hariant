package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.player.sequencer.Sequencer;
import me.hapyl.eterna.module.player.sequencer.Track;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifierArtifactSet;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.entity.cooldown.Cooldown;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantTalentEvent;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.task.executor.ExecutorService;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class ItemArtifactAntiquePocketWatch extends ItemArtifact {
    
    public ItemArtifactAntiquePocketWatch(@NotNull Key key) {
        super(key, Component.text("Antique Pocket Watch"), Icon.ofTexture("b60eca715b31a8a7b1eb35cb7ade089ac56944ec7e0217ddadec6fe9bc4a766d"), new ArtifactSetRewind());
        
        setDescription(
                Component.empty()
                         .append(Component.text("An old antique pocket watch whose hands haven't moved in a long time."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("But when the moment comes, it will strike its hour."))
        );
    }
    
    private static class ArtifactSetRewind extends ArtifactSet implements Listener {
        
        @DisplayField private final Decimal cooldownReductionIncrease = Decimal.ofAttribute(AttributeType.COOLDOWN_REDUCTION, 20);
        
        @DisplayField private final Decimal rewindChance = Decimal.ofPercentage(25);
        @DisplayField private final Cooldown rewindCooldown = Cooldown.ofSeconds(Key.ofString("rewind_cooldown"), 12);
        
        private final Sequencer sequencer = Sequencer.singleTrack(
                Hariant.getPlugin(),
                Track.builder("a-----b-----c")
                     .where('a', Sound.BLOCK_DISPENSER_DISPENSE, 0.7f)
                     .where('b', Sound.BLOCK_DISPENSER_DISPENSE, 0.8f)
                     .where('c', Sound.BLOCK_DISPENSER_DISPENSE, 0.9f)
        );
        
        ArtifactSetRewind() {
            super(Key.ofString("rewind"), Component.text("Rewind"));
            
            setPieceDescription(
                    PieceCount.TWO_PIECE,
                    Component.empty()
                             .append(Component.text("Increases "))
                             .append(AttributeType.COOLDOWN_REDUCTION)
                             .append(Component.text(" by "))
                             .append(cooldownReductionIncrease)
                             .append(Component.text("."))
            );
            
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
                super(ArtifactSetRewind.this, PieceCount.TWO_PIECE, applier, HariantConstants.INDEFINITE_DURATION);
                
                of(AttributeType.COOLDOWN_REDUCTION, AttributeModifierType.FLAT, cooldownReductionIncrease.doubleValue());
            }
        }
    }
    
}