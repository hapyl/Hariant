package me.hapyl.hariant.inventory.drop;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.annotate.Immutable;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.event.HariantLootGenerationEvent;
import me.hapyl.hariant.inventory.item.ResourceRegistry;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.List;
import java.util.Random;

public class DropTable implements LootGenerator {
    
    private final List<Content> contents;
    private final Amount rolls;
    private final int totalWeight;
    
    protected DropTable(@NotNull List<Droppable> contents, final Amount rolls) {
        this.contents = contents.stream().map(Content::new).toList();
        this.rolls = rolls;
        this.totalWeight = contents.stream().mapToInt(Droppable::getWeight).sum();
    }
    
    public int getTotalWeight() {
        return totalWeight;
    }
    
    @NotNull
    @Immutable
    public List<Content> getContents() {
        return contents;
    }
    
    @NotNull
    @Override
    public List<Droppable> generateLoot() {
        final List<Droppable> loot = Lists.newArrayList();
        
        // Generate guaranteed loot
        this.contents.stream().filter(Droppable::isGuaranteedDrop).forEach(loot::add);
        
        // totalWeight can easily be 0 if we only have guaranteed drops, which is kinda weird to use DropTable for
        // only guaranteed drops, but that can happen
        if (this.totalWeight > 0) {
            int rollAmount = rolls.amount();
            
            final HariantLootGenerationEvent event = new HariantLootGenerationEvent(rollAmount);
            event.callEvent();
            
            // Update roll amount from the event
            rollAmount = event.getRollAmount();
            
            for (int i = 0; i < rollAmount; i++) {
                loot.add(this.randomDrop());
            }
        }
        
        return loot;
    }
    
    public void generateLootAnDrop(@NotNull PlayerProfile profile) {
        final List<Droppable> loot = this.generateLoot();
        
        // Notify
        profile.sendMessage(Component.text("LOOT!", NamedTextColor.GOLD, TextDecoration.BOLD));
        
        loot.forEach(droppable -> {
            final Drop drop = droppable.drop(profile);
            
            profile.sendMessage(
                    Component.empty()
                             .append(Component.text(" + ", NamedTextColor.GREEN))
                             .append(drop)
            );
        });
    }
    
    @NotNull
    private Droppable randomDrop() {
        final Random random = Hariant.getRandom();
        
        final double randomWeight = random.nextDouble() * totalWeight;
        double cumulativeWeight = 0;
        
        for (Droppable droppable : contents) {
            // Skip guaranteed drop because it's faster
            if (droppable.isGuaranteedDrop()) {
                continue;
            }
            
            final int weight = droppable.getWeight();
            cumulativeWeight += weight;
            
            if (randomWeight <= cumulativeWeight) {
                return droppable;
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
        
        Content(@NotNull Droppable droppable) {
            this.droppable = droppable;
        }
        
        @Range(from = 0, to = Integer.MAX_VALUE)
        @Override
        public int getWeight() {
            return droppable.getWeight();
        }
        
        @NotNull
        @Override
        public Component getName() {
            return droppable.getName();
        }
        
        @NotNull
        @Override
        public Amount getAmount() {
            return droppable.getAmount();
        }
        
        @NotNull
        @Override
        public Drop drop(@NotNull PlayerProfile profile) {
            return droppable.drop(profile);
        }
        
        public double getDropChance() {
            return droppable.isGuaranteedDrop()
                   ? 1.0
                   : (double) droppable.getWeight() / totalWeight;
        }
        
        @NotNull
        public Component getDropChanceFormatted() {
            return Component.text("%.1f%%".formatted(this.getDropChance() * 100));
        }
    }
    
}
