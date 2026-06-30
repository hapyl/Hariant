package me.hapyl.hariant.entity.damage.tracker;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.Streamable;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.AssistSource;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.util.Resettable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public final class CombatTracker implements Resettable, Streamable<CombatData> {
    
    private static final int NUMBER_OF_DISPLAY_BARS = 20;
    
    private static final Component BAR_COMPONENT_FILLED = Component.text("|");
    private static final Component BAR_COMPONENT_EMPTY = Component.text("|", Colors.DARK_GRAY);
    
    private static final Style BAR_STYLE_NORMAL = Style.style(Colors.GREEN);
    private static final Style BAR_STYLE_LETHAL = Style.style(Colors.RED);
    
    private final HariantEntity entity;
    private final Map<HariantEntity, CombatData> combatDataMap;
    
    public CombatTracker(@NotNull HariantEntity entity) {
        this.entity = entity;
        this.combatDataMap = Maps.newHashMap();
    }
    
    public void incrementDamage(@NotNull CombatData.Type type, @NotNull HariantEntity entity, @NotNull DamageSourceIdentity identity, final double damage, final boolean isLethal) {
        final CombatData combatData = this.getOrComputeData(entity);
        final Map<? super DamageSourceIdentity, Damage> damageMap = combatData.getDamageMap(type);
        
        damageMap.compute(identity, (_key, _damage) -> {
            // Compute damage dealt
            _damage = Objects.requireNonNullElseGet(_damage, () -> new Damage(identity));
            _damage.damage += damage;
            _damage.totalHits++;
            
            // If the damage was lethal, mark it
            if (isLethal) {
                _damage.isLethal = true;
            }
            
            return _damage;
        });
    }
    
    public void assist(@NotNull AssistSource assistSource) {
        this.getOrComputeData(assistSource.source()).assist(assistSource);
    }
    
    @NotNull
    public Stream<? extends HariantEntity> assistingEntities() {
        final double maxHealth = entity.getMaxHealth();
        final double damageThreshold = maxHealth * HariantConstants.ASSIST_DAMAGE_THRESHOLD_PERCENTAGE;
        
        return combatDataMap.values().stream()
                            .filter(data -> {
                                // Assist Rules:
                                //  1. If damage taken is higher than n% of entity's max health
                                //  2. If assisted in the last nL
                                
                                if (data.totalDamage(CombatData.Type.INCOMING) >= damageThreshold) {
                                    return true;
                                }
                                else {
                                    final Assist assist = data.getAssist();
                                    
                                    return assist != null && assist.millisSinceLastAssist() < HariantConstants.ASSIST_DURATION_MILLIS;
                                }
                            })
                            .map(CombatData::getEntity);
    }
    
    @Override
    public void reset() {
        this.combatDataMap.clear();
    }
    
    @NotNull
    @Override
    public Stream<CombatData> stream() {
        return combatDataMap.values().stream();
    }
    
    public @NotNull HoverEvent<? extends Component> createHoverEvent(@NotNull CombatData.Type type) {
        // Group combat data
        final List<Entry> entries = combatDataMap.entrySet()
                                                 .stream()
                                                 .flatMap(entry -> {
                                                     return entry.getValue()
                                                                 .getDamageMap(type)
                                                                 .values()
                                                                 .stream()
                                                                 .map(damage -> new Entry(entry.getKey(), damage));
                                                 })
                                                 .sorted(Entry::compareTo)
                                                 .toList();
        
        // Calculate the total sum of all damage
        final double totalDamage = combatDataMap.values()
                                                .stream()
                                                .flatMap(data -> data.getDamageMap(type).values().stream())
                                                .mapToDouble(Damage::getDamage)
                                                .sum();
        
        final TextComponent.Builder builder = Component.text();
        builder.append(type);
        builder.append(Component.newline());
        builder.append(Component.newline());
        
        for (int i = 0; i < entries.size(); i++) {
            final Entry entry = entries.get(i);
            final HariantEntity entity = entry.entity;
            final Damage damage = entry.damage;
            
            if (i != 0) {
                builder.append(Component.newline());
            }
            
            final double percentageOfTotalDamageDealt = damage.getDamage() / totalDamage;
            
            builder.append(
                    Component.empty()
                             .append(entity.asHeadComponent())
                             .appendSpace()
                             .append(createProgress(percentageOfTotalDamageDealt, damage.isLethal ? BAR_STYLE_LETHAL : BAR_STYLE_NORMAL))
                             .appendSpace()
                             .append(Component.text("%,.0f".formatted(damage.damage), Colors.RED))
                             .appendSpace()
                             .append(Component.text("x%s".formatted(damage.totalHits), Colors.DARK_GRAY))
                             .appendSpace()
                             .append(damage.identity.getName().color(Colors.GRAY))
            );
        }
        
        builder.append(Component.newline());
        builder.append(Component.newline());
        
        builder.append(
                Component.empty()
                         .append(Component.text("Total DMG ", Colors.GRAY))
                         .append(Component.text("%,.0f".formatted(totalDamage), Colors.RED))
        );
        
        return HoverEvent.showText(builder.build());
    }
    
    @NotNull
    private CombatData getOrComputeData(@NotNull HariantEntity entity) {
        return combatDataMap.computeIfAbsent(entity, CombatData::new);
    }
    
    private static @NotNull Component createProgress(double percent, @NotNull Style style) {
        final TextComponent.Builder builder = Component.text();
        
        for (int i = 0; i < NUMBER_OF_DISPLAY_BARS; i++) {
            builder.append((double) i / NUMBER_OF_DISPLAY_BARS <= percent ? BAR_COMPONENT_FILLED.style(style) : BAR_COMPONENT_EMPTY);
        }
        
        return builder.build();
    }
    
    public record Entry(@NotNull HariantEntity entity, @NotNull Damage damage) implements Comparable<Entry> {
        
        @Override
        public int compareTo(@NotNull CombatTracker.Entry that) {
            return Double.compare(that.damage.getDamage(), this.damage.getDamage());
        }
        
    }
    
}
