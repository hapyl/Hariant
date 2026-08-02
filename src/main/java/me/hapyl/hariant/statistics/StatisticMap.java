package me.hapyl.hariant.statistics;

import com.google.common.collect.Maps;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.problem.Problem;
import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.MongoSerializable;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentRegistry;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Collectors;

public class StatisticMap implements Statistics, MongoSerializable {
    
    private final Map<Statistic, Double> statistics;
    private final Map<Talent, Integer> talentUsage;
    
    public StatisticMap() {
        this.statistics = Maps.newEnumMap(Statistic.class);
        this.talentUsage = Maps.newHashMap();
    }
    
    public void merge(@NotNull StatisticMap other) {
        other.statistics.forEach((key, value) -> statistics.merge(key, value, Double::sum));
        other.talentUsage.forEach((key, value) -> talentUsage.merge(key, value, Integer::sum));
    }
    
    @Override
    public void incrementStatistic(@NotNull Statistic statistic, double value) {
        statistics.merge(statistic, value, Double::sum);
    }
    
    @Override
    public void incrementTalentUsage(@NotNull Talent talent) {
        talentUsage.merge(talent, 1, Integer::sum);
    }
    
    @Override
    public double getStatistic(@NotNull Statistic statistic) {
        return statistics.getOrDefault(statistic, 0.0);
    }
    
    @Override
    public int getTalentUsage(@NotNull Talent talent) {
        return talentUsage.getOrDefault(talent, 0);
    }
    
    @Override
    public void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        // Write statistics
        statistics.forEach(((statistic, value) -> document.put(StatisticEntry.CODEC.serialize(statistic), value)));
        
        // Write talent usages
        document.put(
                "talent_usage",
                talentUsage.entrySet()
                           .stream()
                           .collect(Collectors.toMap(
                                   entry -> entry.getKey().getKeyAsString(),
                                   Map.Entry::getValue
                           ))
        );
    }
    
    @Override
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        // Read statistics
        for (Statistic statistic : Statistic.values()) {
            if (document.get(StatisticEntry.CODEC.serialize(statistic)) instanceof Double value) {
                statistics.put(statistic, value);
            }
        }
        
        // Read talent usage
        if (document.get("talent_usage") instanceof Document talentUsageDocument) {
            talentUsageDocument.keySet().forEach(talentKey -> {
                final Talent talent = TalentRegistry.getRegistry().get(talentKey).orElse(null);
                
                if (talent == null) {
                    problemReporter.report(Problem.severe(StatisticMap.class, "Invalid talent: " + talentKey));
                    return;
                }
                
                final int value = talentUsageDocument.get(talentKey, 0);
                
                if (value > 0) {
                    talentUsage.put(talent, value);
                }
            });
        }
    }
    
    public @NotNull Map<Talent, Integer> getTalentUsages() {
        return Map.copyOf(talentUsage);
    }
    
    public boolean isEmpty() {
        return statistics.isEmpty() && talentUsage.isEmpty();
    }
    
    public static @NotNull StatisticMap readAsNew(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        final StatisticMap statisticMap = new StatisticMap();
        statisticMap.read(database, document, problemReporter);
        
        return statisticMap;
    }
    
}