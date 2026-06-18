package me.hapyl.hariant.inventory.item.artifact;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.annotate.AutoRegisteredListener;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

@AutoRegisteredListener
public abstract class ArtifactSet implements Keyed, Named {
    
    private final Key key;
    private final Component name;
    
    private final Map<PieceCount, Component> effectDescription;
    
    public ArtifactSet(@NotNull Key key, @NotNull Component name) {
        this.key = key;
        this.name = name;
        this.effectDescription = Maps.newEnumMap(PieceCount.class);
        
        AutoRegisteredListener.Registry.register(this);
    }
    
    @Nullable
    public ElementType getEffectiveElementType() {
        return null;
    }
    
    public int lastPieceCount() {
        final PieceCount pieceCount = effectDescription.keySet().stream().max(Comparator.comparingInt(PieceCount::ordinal)).orElse(null);
        
        return pieceCount != null ? pieceCount.ordinal() : 0;
    }
    
    @ApiStatus.OverrideOnly
    public abstract void applyEffect(@NotNull HariantPlayer player, @NotNull PieceCount pieceCount);
    
    @NotNull
    @Override
    public final Key getKey() {
        return key;
    }
    
    @Override
    public final int hashCode() {
        return Objects.hashCode(this.key);
    }
    
    @Override
    public final boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final ArtifactSet that = (ArtifactSet) object;
        return Objects.equals(this.key, that.key);
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @Nullable
    public Component getPieceDescription(@NotNull PieceCount pieceCount) {
        return effectDescription.get(pieceCount);
    }
    
    protected void setPieceDescription(@NotNull PieceCount pieceCount, @NotNull Component description) {
        this.effectDescription.put(pieceCount, description);
    }
}
