package me.hapyl.hariant.inventory.drop;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.event.HariantLootGenerationEvent;
import me.hapyl.hariant.inventory.item.ResourceRegistry;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

public class DropTable implements LootGenerator {
    
    private final int totalWeight;
    
    private final List<? extends Content> contents;
    private final Amount rolls;
    
    protected DropTable(@NotNull List<? extends Droppable> contents, final Amount rolls) {
        // We have to compute total weight before Content, since drop tiers and chances are constants that require total weight
        this.totalWeight = contents.stream().mapToInt(Droppable::getWeight).sum();
        this.contents = contents.stream().map(Content::new).toList();
        this.rolls = rolls;
    }
    
    public int getTotalWeight() {
        return totalWeight;
    }
    
    @NotNull
    @Unmodifiable
    public List<? extends Content> getContents() {
        return contents;
    }
    
    public @NotNull Amount getRolls() {
        return rolls;
    }
    
    @NotNull
    public Map<DropTier, List<Content>> getContentsTiered() {
        return contents.stream()
                       .collect(Collectors.groupingBy(
                               Content::getDropTier,
                               () -> new TreeMap<>(Comparator.comparingInt(Enum::ordinal)),
                               Collectors.toList()
                       ));
    }
    
    @Override
    public @NotNull DropSummary generateLoot(@NotNull PlayerProfile profile) {
        final List<Content> generatedContents = Lists.newArrayList();
        
        // Roll guaranteed loot
        this.contents.stream().filter(Content::isGuaranteedDrop).forEach(generatedContents::add);
        
        // Only roll if `totalWeight` is higher than 0, which can happen if the drop table
        // only contains guaranteed items
        if (this.totalWeight > 0) {
            int rollAmount = rolls.amount();
            
            final HariantLootGenerationEvent event = new HariantLootGenerationEvent(this, rollAmount);
            event.callEvent();
            
            // Update roll amount from the event
            rollAmount = event.getRollAmount();
            
            for (int i = 0; i < rollAmount; i++) {
                generatedContents.add(this.randomEntry());
            }
        }
        
        // Generate drops
        final DropSummary dropSummary = DropSummary.create();
        
        for (Content content : generatedContents) {
            dropSummary.append(content.drop(profile));
        }
        
        return dropSummary;
    }
    
    @NotNull
    private DropTable.Content randomEntry() {
        final Random random = Hariant.getRandom();
        
        final double randomWeight = random.nextDouble() * totalWeight;
        double cumulativeWeight = 0;
        
        for (Content content : contents) {
            // Skip guaranteed drop because it's faster
            if (content.isGuaranteedDrop()) {
                continue;
            }
            
            final int weight = content.getWeight();
            cumulativeWeight += weight;
            
            if (randomWeight <= cumulativeWeight) {
                return content;
            }
        }
        
        throw new IllegalArgumentException("Cannot generate loot on empty DropTable!");
    }
    
    @NotNull
    public static DropTable create(@NotNull List<Droppable> contents, @NotNull Amount rolls) {
        return new DropTable(contents, rolls);
    }
    
    // Note that this is should only be used as a placeholder and not an actual DropTable
    @NotNull
    public static DropTable empty() {
        return new DropTable(List.of(Droppable.ofResource(ResourceRegistry.CAT_COINS, HariantConstants.GUARANTEED_DROP_CHANCE, Amount.fixed(1))), Amount.fixed(1));
    }
    
    public final class Content implements Droppable {
        
        private final Droppable droppable;
        private final double dropChance;
        private final DropTier dropTier;
        
        Content(@NotNull Droppable droppable) {
            this.droppable = droppable;
            this.dropChance = droppable.isGuaranteedDrop() ? 1.0 : (double) droppable.getWeight() / totalWeight;
            this.dropTier = DropTier.fromDropChance(dropChance);
        }
        
        public double getDropChance() {
            return dropChance;
        }
        
        @NotNull
        public DropTier getDropTier() {
            return dropTier;
        }
        
        @Override
        public @NotNull Key getKey() {
            return droppable.getKey();
        }
        
        @Override
        public @NotNull Component getName() {
            return droppable.getName();
        }
        
        @Override
        public @NotNull Amount getAmount() {
            return droppable.getAmount();
        }
        
        @Override
        public @Range(from = 0, to = Integer.MAX_VALUE) int getWeight() {
            return droppable.getWeight();
        }
        
        @Override
        public @NotNull Drop drop(@NotNull PlayerProfile profile, int amount) {
            return droppable.drop(profile, amount);
        }
        
        private @NotNull DropResult drop(@NotNull PlayerProfile profile) {
            final int amount = droppable.getAmount().amount();
            final Drop drop = droppable.drop(profile, amount);
            
            return new DropResult(drop, amount, dropChance, dropTier);
        }
        
    }
}
