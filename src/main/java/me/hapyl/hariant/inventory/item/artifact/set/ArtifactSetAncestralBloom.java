package me.hapyl.hariant.inventory.item.artifact.set;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.instance.AttributesInstance;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierArtifactSet;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.entity.heal.HealingSource;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageComputeEvent;
import me.hapyl.hariant.event.HariantHealEvent;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.ArtifactSetModifier;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.CommonArtifactSetModifiers;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class ArtifactSetAncestralBloom extends ArtifactSet implements Listener {
    
    private static final String NAME = "Ancestral Bloom";
    private static final String BUFF_CHAR = "\uD83E\uDEA1";
    
    private static final Component COMPONENT_STACK = Component.text("Ancestral Favor", Colors.BLOOM);
    
    private static final Title TITLE_BUFF_ACTIVE = Title.title(Component.text(BUFF_CHAR, Colors.GREEN), Component.empty(), 5, 15, 5);
    private static final Title TITLE_BUFF_INACTIVE = Title.title(Component.text(BUFF_CHAR, Colors.DARK_GRAY), Component.empty(), 5, 15, 5);
    private static final Title TITLE_DAMAGE_BONUS_ACTIVE = Title.title(Component.text(BUFF_CHAR, Colors.GOLD), Component.empty(), 5, 20, 5);
    
    private final ArtifactSetModifier twoPieceBonus = CommonArtifactSetModifiers.MAX_HEALTH;
    
    private final Decimal fourPieceDamageBonus = Decimal.ofPercentage(16);
    private final Decimal fourPieceDamageBonusDuration = Decimal.ofSeconds(4);
    
    private final Decimal maximumBloomStacks = Decimal.ofValue(3);
    private final Decimal bloomDecay = Decimal.ofSeconds(3);
    
    private final Key blookingModifierKey = Key.ofString("ancestral_bloom_blooming");
    
    ArtifactSetAncestralBloom(@NotNull Key key) {
        super(key, Component.text(NAME));
        
        setPieceDescription(PieceCount.TWO_PIECE, twoPieceBonus);
        
        setPieceDescription(
                PieceCount.FOUR_PIECE,
                Component.empty()
                         .append(Component.text("When healing a teammate, gain one stack of "))
                         .append(COMPONENT_STACK)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("At "))
                         .append(Component.text(maximumBloomStacks.wordValue(Decimal.WordValue.LOWERCASE)))
                         .append(Component.text(" stacks, healing also increases the "))
                         .append(Component.text("DMG", Colors.RED))
                         .append(Component.text(" dealt by "))
                         .append(fourPieceDamageBonus)
                         .append(Component.text(" for "))
                         .append(fourPieceDamageBonusDuration)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Lose one stacks after not getting a stack for ", Colors.DARK_GRAY))
                         .append(bloomDecay.asComponent().color(Colors.DARK_GRAY))
                         .append(Component.text(".", Colors.DARK_GRAY))
        );
    }
    
    @EventHandler
    public void handleHariantDamageComputeEvent(HariantDamageComputeEvent ev) {
        final HariantEntity attacker = ev.getAttacker();
        
        if (attacker == null) {
            return;
        }
        
        if (!attacker.getAttributes().hasModifier(blookingModifierKey)) {
            return;
        }
        
        ev.mutateDamage(() -> NAME, DamageMutator.multiply(), 1 + fourPieceDamageBonus.doubleValue());
    }
    
    @EventHandler
    public void handleHariantHealEvent(HariantHealEvent ev) {
        final HealingSource healingSource = ev.getHealingSource();
        
        if (!(healingSource.getHealer() instanceof HariantPlayer player)) {
            return;
        }
        
        if (!player.getHeroInstance().countArtifactSetPieces(this).isOrHigher(PieceCount.FOUR_PIECE)) {
            return;
        }
        
        final AttributesInstance playerAttributes = player.getAttributes();
        final ModifierFourPiece existingModifier = playerAttributes.getModifier(ModifierFourPiece.class).orElse(null);
        
        if (existingModifier != null) {
            final HariantEntity entity = ev.getEntity();
            
            // If stacks already at max, add modifier to the healed entity
            if (existingModifier.stacks == maximumBloomStacks.intValue()) {
                final AttributesInstance entityAttributes = entity.getAttributes();
                final boolean newModifier = !entityAttributes.hasModifier(blookingModifierKey);
                
                entityAttributes.addModifier(new AttributeModifierBlooming(player));
                
                // Notify entity
                if (newModifier) {
                    entity.showTitle(TITLE_DAMAGE_BONUS_ACTIVE);
                }
            }
            // Otherwise increment stacks
            else {
                existingModifier.stacks = Math.min(existingModifier.stacks + 1, maximumBloomStacks.intValue());
                
                // If stacks are now at max value, notify the player
                if (existingModifier.stacks == maximumBloomStacks.intValue()) {
                    player.showTitle(TITLE_BUFF_ACTIVE);
                }
            }
            
            // Reset the duration either way
            existingModifier.resetTicksBeforeStackDecrement();
        }
        else {
            playerAttributes.addModifier(new ModifierFourPiece(player));
        }
    }
    
    @Override
    public void applyEffect(@NotNull HariantPlayer player, @NotNull PieceCount pieceCount) {
        if (pieceCount.isOrHigher(PieceCount.TWO_PIECE)) {
            player.getAttributes().addModifier(new ModifierTwoPiece(player));
        }
    }
    
    public class ModifierTwoPiece extends AttributeModifierArtifactSet {
        
        ModifierTwoPiece(@NotNull HariantEntity applier) {
            super(ArtifactSetAncestralBloom.this, PieceCount.TWO_PIECE, applier, twoPieceBonus);
        }
        
    }
    
    public class ModifierFourPiece extends AttributeModifierArtifactSet {
        
        private int stacks;
        private int ticksBeforeStackDecrement;
        
        ModifierFourPiece(@NotNull HariantEntity applier) {
            super(ArtifactSetAncestralBloom.this, PieceCount.FOUR_PIECE, applier, Tick.INDEFINITE);
            
            this.stacks = 1;
            this.ticksBeforeStackDecrement = bloomDecay.intValue();
        }
        
        @Override
        public void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick, int duration) {
            // Decrement stacks
            if (this.ticksBeforeStackDecrement-- <= 0) {
                // If stacks are at maximum, notify the player that they lost the buff
                if (this.stacks == maximumBloomStacks.intValue()) {
                    entity.showTitle(TITLE_BUFF_INACTIVE);
                }
                
                this.stacks--;
                this.resetTicksBeforeStackDecrement();
            }
        }
        
        @Override
        public boolean isOver() {
            return stacks <= 0;
        }
        
        private void resetTicksBeforeStackDecrement() {
            this.ticksBeforeStackDecrement = bloomDecay.intValue();
        }
        
    }
    
    public class AttributeModifierBlooming extends AttributeModifier {
        
        AttributeModifierBlooming(@NotNull HariantEntity applier) {
            super(blookingModifierKey, ArtifactSetAncestralBloom.this, applier, fourPieceDamageBonusDuration.intValue());
        }
        
    }
    
}
