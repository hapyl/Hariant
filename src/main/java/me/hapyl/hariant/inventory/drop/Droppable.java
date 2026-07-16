package me.hapyl.hariant.inventory.drop;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.inventory.item.Item;
import me.hapyl.hariant.inventory.item.Resource;
import me.hapyl.hariant.inventory.item.ResourceRegistry;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public interface Droppable extends Keyed, Named {
    
    @Override
    @NotNull Key getKey();
    
    @Override
    @NotNull Component getName();
    
    @NotNull Amount getAmount();
    
    @Range(from = 0, to = Integer.MAX_VALUE)
    int getWeight();
    
    default boolean isGuaranteedDrop() {
        return this.getWeight() == HariantConstants.GUARANTEED_DROP_CHANCE;
    }
    
    @NotNull Drop drop(@NotNull PlayerProfile profile, int amount);
    
    static @NotNull Droppable ofItem(@NotNull Item item, final int weight) {
        return new DroppableItemImpl(item, weight);
    }
    
    static @NotNull Droppable ofResource(@NotNull Resource resource, final int weight, @NotNull Amount amount) {
        return new DroppableResourceImpl(resource, weight, amount);
    }
    
    static @NotNull Droppable ofCatCoins() {
        return ofResource(ResourceRegistry.CAT_COINS, HariantConstants.GUARANTEED_DROP_CHANCE, Amount.range(100, 200));
    }
    
    static @NotNull Droppable ofArtifactArtificer() {
        return ofResource(ResourceRegistry.ARTIFACT_ARTIFICER, 10, Amount.range(1, 2));
    }
    
    static @NotNull Droppable ofHeroRecruitVoucher() {
        return ofResource(ResourceRegistry.HERO_RECRUIT_VOUCHER, 1, Amount.fixed(1));
    }
    
}