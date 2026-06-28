package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.annotate.StrictEnumOrdinal;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.hariant.util.ComparableOrdinal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

@StrictEnumOrdinal
public enum PieceCount implements Named, ComparableOrdinal<PieceCount>, ComponentLike {
    
    NONE(""),
    ONE_PIECE("①"),
    TWO_PIECE("②"),
    THREE_PIECE("③"),
    FOUR_PIECE("④");
    
    private final Component name;
    
    PieceCount(@NotNull String ch) {
        this.name = Component.text(ch + "-ᴘɪᴇᴄᴇ ʙᴏɴᴜꜱ");
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return name;
    }
    
    @NotNull
    public static PieceCount valueOf(final int count) {
        return switch (count) {
            case 0 -> NONE;
            case 1 -> ONE_PIECE;
            case 2 -> TWO_PIECE;
            case 3 -> THREE_PIECE;
            case 4 -> FOUR_PIECE;
            default -> throw new IllegalArgumentException("Illegal artifact piece count: %s".formatted(count));
        };
    }
    
}