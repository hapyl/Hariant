package me.hapyl.hariant.inventory.drop;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.annotate.SelfReturn;
import me.hapyl.hariant.Colors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DropSummary {
    
    private static final Component PREFIX_BULLET = Component.text(" + ", Colors.GREEN);
    private static final DropTier RARE_DROP_THRESHOLD = DropTier.VERY_RARE;
    private static final net.kyori.adventure.key.Key SOUND_KEY = net.kyori.adventure.key.Key.key("block.chest.close");
    
    private static final Comparator<Entry> COMPARATOR = Comparator.comparingDouble(Entry::getChance).reversed();
    
    private final List<DropResult> drops;
    
    private DropSummary() {
        this.drops = Lists.newArrayList();
    }
    
    @SelfReturn
    public DropSummary append(@NotNull DropResult dropResult) {
        this.drops.add(dropResult);
        return this;
    }
    
    @SelfReturn
    public DropSummary append(@NotNull DropSummary summary) {
        this.drops.addAll(summary.drops);
        return this;
    }
    
    public void showSummary(@NotNull Audience audience) {
        final List<? extends Entry> summary = createSummary();
        
        audience.sendMessage(Component.text("LOOT!", Colors.ORANGE, TextDecoration.BOLD));
        
        summary.forEach(entry -> {
            audience.sendMessage(Component.empty().append(PREFIX_BULLET).append(entry.asComponent()));
        });
        
        // TODO (xanyjl @ Tuesday, June 16) -> Add sfx for rare items?
        
        // Sfx
        audience.playSound(Sound.sound(SOUND_KEY, Sound.Source.UI, 3, 0.50f));
        audience.playSound(Sound.sound(SOUND_KEY, Sound.Source.UI, 3, 0.75f));
    }
    
    @Override
    public String toString() {
        return drops.stream()
                    .map(drop -> "%s:%s".formatted(drop.getDrop().getKeyAsString(), drop.getAmount()))
                    .collect(Collectors.joining(", ", "[", "]"));
    }
    
    private @NotNull List<? extends Entry> createSummary() {
        return Stream.concat(
                // Sum up multi-drops to a single entry
                drops.stream()
                     .filter(drop -> drop.getAmount() > 1)
                     .collect(Collectors.groupingBy(
                             Function.identity(),
                             Collectors.summingInt(DropResult::getAmount)
                     ))
                     .entrySet()
                     .stream()
                     .map(entry -> new Entry(entry.getKey(), entry.getValue())),
                // Append single drops as-is
                drops.stream()
                     .filter(drop -> drop.getAmount() == 1)
                     .map(drop -> new Entry(drop, 1))
        ).sorted(COMPARATOR).toList();
    }
    
    public static @NotNull DropSummary create() {
        return new DropSummary();
    }
    
    private record Entry(@NotNull DropResult dropResult, int totalAmount) implements ComponentLike {
        
        public double getChance() {
            return dropResult.getChance();
        }
        
        @Override
        public @NotNull Component asComponent() {
            final TextComponent.Builder builder = Component.text();
            final Drop drop = dropResult.getDrop();
            
            // If total amount greater tha none, append it
            if (totalAmount > 1) {
                builder.append(Component.text("%,d".formatted(totalAmount), Colors.GOLD));
                builder.append(Component.text(" x ", Colors.DARK_GRAY));
            }
            
            builder.append(drop.getNameStyled());
            builder.hoverEvent(drop.createHoverEvent());
            
            // If it's a rare drop, append the rarity at the end
            final DropTier dropTier = dropResult.getDropTier();
            
            if (dropTier.isOrHigher(RARE_DROP_THRESHOLD)) {
                builder.append(Component.text(" (", Colors.DARK_GRAY));
                builder.append(dropTier.getName().style(dropTier.getStyle()));
                builder.append(Component.text(")", Colors.DARK_GRAY));
            }
            
            return builder.build();
        }
        
    }
    
}