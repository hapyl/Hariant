package me.hapyl.hariant.statistics;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.util.Hoverable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class PlayerStatistics implements Statistics, Hoverable {
    
    private static final List<? extends Statistic> STATISTICS_TO_DISPLAY_IN_HOVER_EVENT = List.of(
            Statistic.KILLS,
            Statistic.DEATH,
            Statistic.ASSISTS,
            Statistic.ANOMALY_TRIGGERED,
            Statistic.DAMAGE_DEALT,
            Statistic.DAMAGE_TAKEN,
            Statistic.TALENT_USAGE,
            Statistic.ULTIMATE_USAGE
    );
    
    private final HariantPlayer player;
    private final Hero hero;
    private final StatisticMap statisticMap;
    
    public PlayerStatistics(@NotNull HariantPlayer player) {
        this.player = player;
        this.hero = player.getHero();
        this.statisticMap = new StatisticMap();
    }
    
    public @NotNull HariantPlayer getPlayer() {
        return player;
    }
    
    public void syncToDatabase() {
        player.getProfile().getDatabase().statistics.fromPlayerStatistics(hero, statisticMap);
    }
    
    @Override
    public void incrementStatistic(@NotNull Statistic statistic, double value) {
        statisticMap.incrementStatistic(statistic, value);
    }
    
    @Override
    public void incrementTalentUsage(@NotNull Talent talent) {
        statisticMap.incrementTalentUsage(talent);
    }
    
    @Override
    public double getStatistic(@NotNull Statistic statistic) {
        return statisticMap.getStatistic(statistic);
    }
    
    @Override
    public int getTalentUsage(@NotNull Talent talent) {
        return statisticMap.getTalentUsage(talent);
    }
    
    @Override
    public @NotNull HoverEvent<?> createHoverEvent() {
        final TextComponent.Builder builder = Component.text();
        
        builder.append(Component.text("Statistics", Colors.WHITE, TextDecoration.BOLD));
        builder.appendNewline();
        
        // Append non-empty statistics
        STATISTICS_TO_DISPLAY_IN_HOVER_EVENT.forEach(statistic -> {
            final double value = statisticMap.getStatistic(statistic);
            
            builder.append(format(statistic, value));
            builder.appendNewline();
        });
        
        // Append talent usages
        builder.appendNewline();
        builder.append(Component.text("Talent Usage", Colors.WHITE, TextDecoration.BOLD));
        builder.appendNewline();
        
        final Map<Talent, Integer> talentUsage = statisticMap.getTalentUsages();
        
        if (talentUsage.isEmpty()) {
            builder.append(Component.text("You haven't used any talents", Colors.DARK_GRAY)).appendNewline();
            builder.append(Component.text("this game, why? \uD83E\uDD28", Colors.DARK_GRAY));
        }
        else {
            int index = 0;
            
            for (Talent talent : talentUsage.keySet()) {
                if (index++ != 0) {
                    builder.appendNewline();
                }
                
                builder.append(format(talent, talentUsage.getOrDefault(talent, 0)));
            }
        }
        
        return HoverEvent.showText(builder);
    }
    
    
    private static @NotNull Component format(@NotNull Named named, double value) {
        return Component.empty()
                        .append(named.getName().color(Colors.GRAY))
                        .appendSpace()
                        .append(Component.text("%,.0f".formatted(value), value > 0 ? Colors.GREEN : Colors.RED));
    }
}