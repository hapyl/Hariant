package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ItemArtifactWhoopeeCushion extends ItemArtifact {
    public ItemArtifactWhoopeeCushion(@NotNull Key key) {
        super(key, Component.text("Whoopee Cushion"), Icon.ofTexture("f2f8859a07fdbec3072dd8c6af492d3e6176c4890d64e01b630aa7a26c8ba536"), new ArtifactSetTest(key));
    }
    
    public static class ArtifactSetTest extends ArtifactSet {
        
        @DisplayField private final Decimal physicalDamageBoost = Decimal.ofValue(20);
        
        public ArtifactSetTest(@NotNull Key key) {
            super(key, Component.text("Test"));
            
            setPieceDescription(
                    PieceCount.TWO_PIECE,
                    Component.empty()
                             .append(Component.text("Increases "))
                             .append(AttributeType.PHYSICAL_DAMAGE_BONUS)
                             .append(Component.text(" by "))
                             .append(physicalDamageBoost)
                             .append(Component.text("."))
            );
            
            setPieceDescription(
                    PieceCount.FOUR_PIECE,
                    Component.empty()
            );
        }
        
        @Override
        public void applyEffect(@NotNull HariantPlayer player, @NotNull PieceCount pieceCount) {
        
        }
    }
}
