package me.hapyl.hariant.statistics;

import com.google.common.collect.Maps;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.database.DatabaseCollection;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.PlayerDatabaseEntry;
import me.hapyl.hariant.database.problem.Problem;
import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.MongoSerializableConstructor;
import me.hapyl.hariant.database.serialize.codec.MongoCodec;
import me.hapyl.hariant.database.serialize.codec.MongoCodecs;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.task.InternalTasks;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class StatisticEntry extends PlayerDatabaseEntry {
    
    static final MongoCodec<? super Statistic, String> CODEC = MongoCodecs.ofEnum(Statistic.class);
    
    private final Map<Hero, StatisticMap> statisticMap;
    
    private @MongoSerializableConstructor StatisticEntry(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull String parent) {
        super(database, document, parent);
        
        this.statisticMap = Maps.newHashMap();
    }
    
    public @NotNull StatisticMap getStatisticMap(@NotNull Hero hero) {
        // We compute here to avoid statistic map allocated and filter empty maps on `write`
        return statisticMap.computeIfAbsent(hero, _ -> new StatisticMap());
    }
    
    @Override
    public void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        statisticMap.forEach((hero, statistics) -> {
            if (statistics.isEmpty()) {
                return;
            }
            
            document.put(hero.getKeyAsString(), statistics.writeToNewDocument(database, problemReporter));
        });
    }
    
    @Override
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        document.keySet().forEach(key -> {
            final Hero hero = HeroRegistry.getRegistry().get(key).orElse(null);
            
            if (hero == null) {
                problemReporter.report(Problem.severe(StatisticEntry.class, "Unknow hero: " + key));
                return;
            }
            
            if (!(document.get(key) instanceof Document statisticsDocument)) {
                problemReporter.report(Problem.severe(StatisticEntry.class, "Statistic must be in a document: " + key));
                return;
            }
            
            statisticMap.put(hero, StatisticMap.readAsNew(database, statisticsDocument, problemReporter));
        });
    }
    
    public void fromPlayerStatistics(@NotNull Hero hero, @NotNull StatisticMap playerStatistics) {
        statisticMap.computeIfAbsent(hero, _ -> new StatisticMap()).merge(playerStatistics);
    }
    
    public static @NotNull CompletableFuture<@Nullable Double> aggregatesStatistic(@NotNull Hero hero, @NotNull Statistic statistic) {
        final CompletableFuture<Double> future = new CompletableFuture<>();
        
        InternalTasks.asynchronously(() -> {
            final Document result = Hariant.getDatabase().getCollection(DatabaseCollection.PLAYERS).aggregate(List.of(Aggregates.group(
                    null,
                    Accumulators.sum("total", "$statistics.%s.%s".formatted(hero.getKeyAsString(), CODEC.serialize(statistic)))
            ))).first();
            
            if (result != null && result.get("total") instanceof Double value) {
                future.complete(value);
            }
            else {
                future.complete(null);
            }
        });
        
        return future;
    }
    
}