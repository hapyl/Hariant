package me.hapyl.hariant.reward;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.inventory.item.Resource;
import me.hapyl.hariant.util.LoreSupplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class RewardSummary implements LoreSupplier {
    
    private static final RewardSummary EMPTY = new RewardSummary(Map.of(), List.of(), 0);
    
    private static final Comparator<ResourceSum> COMPARATOR_RESOURCE = Comparator.comparing(RewardSummary.ResourceSum::getSum).reversed();
    private static final Comparator<Reward> COMPARATOR_PRIORITY = Comparator.comparing(Reward::priority).reversed();
    
    private final Map<? extends Resource, ? extends ResourceSum> resourceSums;
    private final List<? extends Reward> otherRewards;
    
    private final int count;
    
    private RewardSummary(@NotNull Map<? extends Resource, ? extends ResourceSum> resourceSums, @NotNull List<? extends Reward> otherRewards, int count) {
        this.resourceSums = resourceSums;
        this.otherRewards = otherRewards;
        this.count = count;
    }
    
    public int sizeResource() {
        return resourceSums.size();
    }
    
    public int sizeRewards() {
        return otherRewards.size();
    }
    
    public boolean isEmpty() {
        return count == 0;
    }
    
    public @NotNull Stream<? extends ResourceSum> streamResourceSumsSorted() {
        return resourceSums.values().stream().sorted(COMPARATOR_RESOURCE);
    }
    
    public @NotNull Stream<? extends Reward> streamRewardsSorted() {
        return otherRewards.stream().sorted(COMPARATOR_PRIORITY);
    }
    
    @Override
    public void supplyLore(@NotNull ItemBuilder builder) {
        if (count == 0) {
            return;
        }
        
        // Resource rewards go first, sorted by amount
        streamResourceSumsSorted().forEach(resourceSum -> builder.addLore(resourceSum.asComponent()));
        
        // Append other rewards sorted by priority
        streamRewardsSorted().forEach(reward -> builder.addLore(reward.getName()));
    }
    
    public static @NotNull RewardSummary create(@NotNull List<? extends Reward> rewards) {
        if (rewards.isEmpty()) {
            return EMPTY;
        }
        
        final Map<Resource, ResourceSum> resourceSums = Maps.newHashMap();
        final List<Reward> otherRewards = Lists.newArrayList();
        
        for (Reward reward : rewards) {
            if (reward instanceof RewardResource rewardResource) {
                final Resource resource = rewardResource.getResource();
                final int amount = rewardResource.getAmount();
                
                resourceSums.compute(resource, (_, sum) -> {
                    sum = sum != null ? sum : new ResourceSum(resource);
                    
                    sum.sum += amount;
                    sum.total++;
                    
                    return sum;
                });
            }
            else {
                otherRewards.add(reward);
            }
        }
        
        return new RewardSummary(resourceSums, otherRewards, rewards.size());
    }
    
    public static class ResourceSum implements ComponentLike {
        
        private final Resource resource;
        
        private int sum;
        private int total;
        
        private ResourceSum(@NotNull Resource resource) {
            this.resource = resource;
        }
        
        public @NotNull Resource getResource() {
            return resource;
        }
        
        public int getSum() {
            return sum;
        }
        
        public int getTotal() {
            return total;
        }
        
        @Override
        public @NotNull Component asComponent() {
            return Component.empty()
                            .append(Component.text("%,d".formatted(sum), Colors.WHITE))
                            .append(Reward.SEPARATOR)
                            .append(resource.getNameStyledWithRarity())
                            .append(total > 1 ? Component.text(" (sum of %s rewards)".formatted(total), Colors.DARK_GRAY) : Component.empty());
        }
        
    }
    
}