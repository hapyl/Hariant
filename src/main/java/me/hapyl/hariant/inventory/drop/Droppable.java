package me.hapyl.hariant.inventory.drop;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.inventory.item.Resource;
import me.hapyl.hariant.inventory.item.ResourceRegistry;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifact;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.function.BiConsumer;

public interface Droppable extends Named {
    
    @Range(from = 0, to = Integer.MAX_VALUE)
    int getWeight();
    
    @NotNull
    @Override
    Component getName();
    
    @NotNull
    Amount getAmount();
    
    @NotNull
    Drop drop(@NotNull PlayerProfile profile);
    
    default boolean isGuaranteedDrop() {
        return this.getWeight() == HariantConstants.GUARANTEED_DROP_CHANCE;
    }
    
    @NotNull
    static Droppable ofArtifact(@NotNull ItemArtifact artifact, final int weight) {
        return new DroppableArtifactImpl(artifact, weight);
    }
    
    @NotNull
    static Droppable ofResource(@NotNull Resource resource, final int weight, @NotNull Amount amount) {
        return new DroppableResourceImpl(resource, weight, amount);
    }
    
    @NotNull
    static Droppable ofCatCoins() {
        return ofResource(ResourceRegistry.CAT_COINS, HariantConstants.GUARANTEED_DROP_CHANCE, Amount.range(100, 200));
    }
    
}
