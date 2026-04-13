package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.inventory.item.ItemInstance;
import me.hapyl.hariant.util.Owned;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ItemArtifactInstance extends ItemInstance implements Owned<HeroInstance> {
    
    private static final Style STYLE_EQUIPPED_BY = Style.style(NamedTextColor.WHITE, TextDecoration.UNDERLINED);
    
    /**
     * Defines the {@link HeroInstance} this {@link ItemArtifactInstance} is currently equipped by, or {@code null} if not equipped.
     */
    @Nullable
    private HeroInstance owner;
    
    public ItemArtifactInstance(@NotNull PlayerDatabase playerDatabase, @NotNull ItemArtifact origin, @NotNull UUID uuid) {
        super(playerDatabase, origin, uuid);
    }
    
    @NotNull
    public ArtifactSet getArtifactSet() {
        return getOrigin().getArtifactSet();
    }
    
    @Nullable
    @Override
    public HeroInstance getOwner() {
        return owner;
    }
    
    @Override
    public void setOwner(@Nullable HeroInstance owner) {
        this.owner = owner;
    }
    
    @NotNull
    @Override
    public ItemArtifact getOrigin() {
        return (ItemArtifact) super.getOrigin();
    }
    
    @Override
    @NotNull
    public ItemBuilder createBuilder() {
        final ItemBuilder builder = super.createBuilder();
        final ItemArtifact origin = getOrigin();
        
        builder.setName(origin.getName());
        
        builder.addWrappedLore(origin.getDescription());
        builder.addLore();
        
        final ArtifactSet artifactSet = origin.getArtifactSet();
        final int artifactSetCount = owner != null ? owner.countArtifactSetPieces(artifactSet).ordinal() : 0;
        
        builder.addLore(
                Component.empty()
                         .append(artifactSet.getName().color(NamedTextColor.GREEN))
                         .append(artifactSetCount == 0 ? Component.empty() : Component.text(" (%s/%s)".formatted(artifactSetCount, artifactSet.lastPieceCount()), NamedTextColor.GRAY))
        );
        
        int index = 0;
        for (PieceCount pieceCount : PieceCount.values()) {
            final Component pieceDescription = artifactSet.getPieceDescription(pieceCount);
            
            if (pieceDescription == null) {
                continue;
            }
            
            final boolean isPieceBonusActive = owner != null && owner.isArtifactSetPieceBonusActive(artifactSet, pieceCount);
            
            if (index++ != 0) {
                builder.addLore();
            }
            
            builder.addLore(
                    Component.empty()
                             .append(Component.text(" "))
                             .append(pieceCount.getName().color(NamedTextColor.DARK_GRAY))
                             .append(Component.text("  "))
                             .append(
                                     Components.checkmark(isPieceBonusActive)
                                               .appendSpace()
                                               .append(isPieceBonusActive ? Component.text("ᴀᴄᴛɪᴠᴇ") : Component.text("ɪɴᴀᴄᴛɪᴠᴇ"))
                             )
                             .append()
            );
            
            builder.addWrappedLore(
                    pieceDescription,
                    _component -> Component.empty()
                                           .append(Component.text("  "))
                                           .append(_component)
                                           .style(Style.style(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
            );
        }
        
        if (owner != null) {
            builder.addLore();
            builder.addLore(
                    Component.empty()
                             .append(Component.text("Equipped by ", STYLE_EQUIPPED_BY))
                             .append(owner.getOrigin().getName().style(STYLE_EQUIPPED_BY))
            );
        }
        
        return builder;
    }
    
}
