package me.hapyl.hariant.inventory.drop;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.eterna.module.annotate.SelfReturn;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public final class DropSummary {
    
    private static final Component PREFIX_BULLET = Component.text(" + ", Colors.GREEN);
    private static final DropTier RARE_DROP_THRESHOLD = DropTier.VERY_RARE;
    private static final net.kyori.adventure.key.Key SOUND_KEY = net.kyori.adventure.key.Key.key("block.chest.close");
    
    private static final Comparator<Entry> COMPARATOR = Comparator.comparingDouble(Entry::getChance).reversed();
    
    private final List<Drop> drops;
    
    private DropSummary() {
        this.drops = Lists.newArrayList();
    }
    
    @SelfReturn
    public DropSummary append(@NotNull List<? extends Drop> drop) {
        this.drops.addAll(drop);
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
    
    private @NotNull List<? extends Entry> createSummary() {
        final Map<Key, Entry> summary = Maps.newHashMap();
        
        for (Drop drop : drops) {
            summary.compute(drop.getDroppable().getKey(), (key, _entry) -> {
                final Entry entry = _entry != null ? _entry : new Entry(drop);
                
                entry.amount += drop.getAmount();
                
                return entry;
            });
        }
        
        return summary.values().stream().sorted(COMPARATOR).toList();
    }
    
    public static @NotNull DropSummary create() {
        return new DropSummary();
    }
    
    public static @NotNull DropSummary create(@NotNull List<? extends Drop> drop) {
        return new DropSummary().append(drop);
    }
    
    @Override
    public String toString() {
        return drops.stream()
                    .map(drop -> "%s:%s".formatted(drop.getDroppable().getKeyAsString(), drop.getAmount()))
                    .collect(Collectors.joining(", ", "[", "]"));
    }
    
    private static class Entry implements ComponentLike {
        
        private final Drop drop;
        private final double chance;
        
        private int amount;
        
        private Entry(@NotNull Drop drop) {
            this.drop = drop;
            this.chance = drop.getDropChance();
        }
        
        public double getChance() {
            return chance;
        }
        
        public int getAmount() {
            return amount;
        }
        
        @Override
        public @NotNull Component asComponent() {
            final TextComponent.Builder builder = Component.text();
            
            builder.append(Component.text("%,d".formatted(amount), Colors.GOLD));
            builder.append(Component.text(" x ", Colors.DARK_GRAY));
            builder.append(drop.getDroppable().getName());
            builder.hoverEvent(drop.getDroppable().createHoverEvent());
            
            // If it's a rare drop, append the rarity at the end
            final DropTier dropTier = drop.getDropTier();
            
            if (dropTier.isOrHigher(RARE_DROP_THRESHOLD)) {
                builder.append(Component.text(" (", Colors.DARK_GRAY));
                builder.append(dropTier.getName().style(dropTier.getStyle()));
                builder.append(Component.text(")", Colors.DARK_GRAY));
            }
            
            return builder.build();
        }
        
    }
    
}