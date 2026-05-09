package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.inventory.item.Item;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ItemArtifact extends Item {
    
    private final ArtifactSet artifactSet;
    
    public ItemArtifact(@NotNull Key key, @NotNull Component name, @NotNull Icon icon, @NotNull ArtifactSet artifactSet) {
        super(key, name, icon);
        
        this.artifactSet = artifactSet;
    }
    
    @NotNull
    public ArtifactSet getArtifactSet() {
        return artifactSet;
    }
    
    @NotNull
    @Override
    public ItemBuilder createBuilder() {
        final ItemBuilder builder = super.createBuilder();
        
        builder.addLore();
        builder.addLore(artifactSet.getName().color(NamedTextColor.GREEN));
        
        int index = 0;
        for (PieceCount pieceCount : PieceCount.values()) {
            final Component pieceDescription = artifactSet.getPieceDescription(pieceCount);
            
            if (pieceDescription == null) {
                continue;
            }
            
            if (index++ != 0) {
                builder.addLore();
            }
            
            builder.addLore(
                    Component.empty()
                             .append(Component.text(" "))
                             .append(pieceCount.getName().color(NamedTextColor.DARK_GRAY))
                             .append(Component.text("  "))
            );
            
            builder.addWrappedLore(
                    pieceDescription,
                    _component -> Component.empty()
                                           .append(Component.text("  "))
                                           .append(_component)
                                           .style(Style.style(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
            );
        }
        
        return builder;
    }
    
    @NotNull
    @Override
    public ItemArtifactInstance newInstance(@NotNull PlayerDatabase database, @NotNull UUID uuid) {
        return new ItemArtifactInstance(database, this, uuid);
    }
}
