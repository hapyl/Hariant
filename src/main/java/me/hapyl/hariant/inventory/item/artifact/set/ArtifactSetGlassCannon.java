package me.hapyl.hariant.inventory.item.artifact.set;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifierArtifactSet;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.event.HariantHealthChangeEvent;
import me.hapyl.hariant.event.HariantPlayerCreateEvent;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.ArtifactSetModifier;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.CommonArtifactSetModifiers;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class ArtifactSetGlassCannon extends ArtifactSet implements Listener {
    
    private final ArtifactSetModifier attackIncrease = CommonArtifactSetModifiers.ATTACK;
    
    private final Decimal fourPieceHealthLimitOfMaxHealth = Decimal.ofPercentage(50);
    private final Decimal fourPieceDamageBonus = Decimal.ofPercentage(25);
    
    ArtifactSetGlassCannon(@NotNull Key key) {
        super(key, Component.text("Glass Cannon"));
        
        setPieceDescription(PieceCount.TWO_PIECE, attackIncrease);
        
        setPieceDescription(
                PieceCount.FOUR_PIECE,
                Component.empty()
                         .append(Component.text("Your health "))
                         .append(Component.text("cannot", Colors.ERROR))
                         .append(Component.text(" exceed "))
                         .append(fourPieceHealthLimitOfMaxHealth)
                         .append(Component.text(" of your "))
                         .append(AttributeType.MAX_HEALTH)
                         .append(Component.text(", but all "))
                         .append(Component.text("DMG", Colors.RED))
                         .append(Component.text(" dealt is increased by "))
                         .append(fourPieceDamageBonus)
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
    public void handleHariantPlayerCreateEvent(HariantPlayerCreateEvent ev) {
        final HariantPlayer player = ev.getPlayer();
        
        if (hasFourPieceEffectActive(player)) {
            player.setHealth(player.getMaxHealth() * fourPieceHealthLimitOfMaxHealth.doubleValue());
        }
    }
    
    @EventHandler
    public void handleHariantHealthChangeEvent(HariantHealthChangeEvent ev) {
        final HariantEntity entity = ev.getEntity();
        
        if (hasFourPieceEffectActive(entity)) {
            ev.setNewHealth(Math.min(ev.getNewHealth(), entity.getMaxHealth() * fourPieceHealthLimitOfMaxHealth.doubleValue()));
        }
    }
    
    @EventHandler
    public void handleHariantDamageEvent(HariantDamageEvent ev) {
        if (!(ev.getAttacker() instanceof HariantPlayer player)) {
            return;
        }
        
        if (hasFourPieceEffectActive(player)) {
            ev.mutateDamage(() -> "Glass Canon", DamageMutator.multiply(), 1 + fourPieceDamageBonus.doubleValue());
        }
    }
    
    private boolean hasFourPieceEffectActive(@NotNull HariantEntity entity) {
        if (!(entity instanceof HariantPlayer player)) {
            return false;
        }
        
        return player.getHeroInstance().countArtifactSetPieces(this).isOrHigher(PieceCount.FOUR_PIECE);
    }
    
    private class ModifierTwoPiece extends AttributeModifierArtifactSet {
        
        ModifierTwoPiece(@NotNull HariantEntity applier) {
            super(ArtifactSetGlassCannon.this, PieceCount.TWO_PIECE, applier, attackIncrease);
        }
    }
    
}
