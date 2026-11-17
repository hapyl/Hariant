package me.hapyl.hariant.inventory.item.artifact.set;

import me.hapyl.eterna.module.math.Precision;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.modifier.AttributeModifierArtifactSet;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantTalentEvent;
import me.hapyl.hariant.event.HariantUltimateFinishEvent;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.ArtifactSetModifier;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.CommonArtifactSetModifiers;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class ArtifactSetTimeSlip extends ArtifactSet implements Listener {
    
    private static final String HOURGLASS_FULL = "⏳";
    private static final String HOURGLASS_EMPTY = "⌛";
    
    private final ArtifactSetModifier twoPieceBonus = CommonArtifactSetModifiers.ENERGY_RECHARGE;
    
    private final Decimal noCooldownDuration = Decimal.ofSeconds(6);
    private final Decimal noCooldownInternalCooldown = Decimal.ofSeconds(0.25f);
    private final Decimal numberOfNoCooldownTalentUsages = Decimal.ofValue(3);
    
    ArtifactSetTimeSlip(@NotNull Key key) {
        super(key, Component.text("Time Slip"));
        
        setPieceDescription(PieceCount.TWO_PIECE, twoPieceBonus);
        
        setPieceDescription(
                PieceCount.FOUR_PIECE,
                Component.empty()
                         .append(Component.text("For "))
                         .append(noCooldownDuration)
                         .append(Component.text(" after using an "))
                         .append(Component.text("ultimate", Colors.ULTIMATE_RESOURCE_ENERGY))
                         .append(Component.text(", the next "))
                         .append(Component.text(numberOfNoCooldownTalentUsages.wordValue(Decimal.WordValue.LOWERCASE), Colors.NUMBER))
                         .append(Component.text(" talent usages have their cooldown reduced to "))
                         .append(noCooldownInternalCooldown)
                         .append(Component.text("."))
        );
    }
    
    @EventHandler
    public void handleHariantTalentEvent(HariantTalentEvent ev) {
        // Only reset `OK` responses
        if (!ev.getResponse().isOk()) {
            return;
        }
        
        // Ignore ultimates
        if (ev.getTalent() instanceof TalentUltimate) {
            return;
        }
        
        final HariantPlayer player = ev.getPlayer();
        
        if (!player.getHeroInstance().countArtifactSetPieces(this).isOrHigher(PieceCount.FOUR_PIECE)) {
            return;
        }
        
        final ModifierFourPiece modifier = player.getAttributes().getModifier(ModifierFourPiece.class).orElse(null);
        
        if (modifier == null) {
            return;
        }
        
        modifier.stacks--;
        
        // Set the cooldown to a small value so the player sees the changes but cannot accidentally use it again
        player.startCooldown(ev.getTalent(), noCooldownInternalCooldown.intValue());
    }
    
    @EventHandler
    public void handleHariantHariantUltimateFinishEvent(HariantUltimateFinishEvent ev) {
        final HariantPlayer player = ev.getPlayer();
        
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
    
    public class ModifierTwoPiece extends AttributeModifierArtifactSet {
        
        ModifierTwoPiece(@NotNull HariantEntity applier) {
            super(ArtifactSetTimeSlip.this, PieceCount.TWO_PIECE, applier, twoPieceBonus);
        }
        
    }
    
    public class ModifierFourPiece extends AttributeModifierArtifactSet {
        
        private int stacks;
        
        ModifierFourPiece(@NotNull HariantEntity applier) {
            super(ArtifactSetTimeSlip.this, PieceCount.FOUR_PIECE, applier, noCooldownDuration.intValue());
            
            this.stacks = numberOfNoCooldownTalentUsages.intValue();
        }
        
        @Override
        public void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick, int duration) {
            entity.showTitle(Title.title(
                    Component.empty(),
                    Component.text(HOURGLASS_FULL.repeat(stacks), Colors.GREEN).append(Component.text(HOURGLASS_EMPTY.repeat(numberOfNoCooldownTalentUsages.intValue() - stacks), Colors.DARK_GRAY)).appendSpace().append(Tick.format(tick, Precision.FIXED_1).color(Colors.GRAY)),
                    0, 5, 2
            ));
        }
        
        @Override
        public boolean isOver() {
            return stacks <= 0 || currentTick() < 0;
        }
        
    }
    
}