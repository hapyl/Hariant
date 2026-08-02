package me.hapyl.hariant.reward;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.PlayerDatabaseEntry;
import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.MongoSerializableConstructor;
import me.hapyl.hariant.database.serialize.codec.MongoCodecs;
import me.hapyl.hariant.util.Timestamp;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public class RewardsEntry extends PlayerDatabaseEntry {
    
    private final Map<Key, Timestamp> claimedAt;
    
    private @MongoSerializableConstructor RewardsEntry(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull String parent) {
        super(database, document, parent);
        
        this.claimedAt = Maps.newHashMap();
    }
    
    public boolean hasClaimed(@NotNull Reward reward) {
        return claimedAt.containsKey(reward.getKey());
    }
    
    public @NotNull Optional<Timestamp> getClaimedAt(@NotNull Reward reward) {
        return Optional.ofNullable(claimedAt.get(reward.getKey()));
    }
    
    public void setClaimed(@NotNull Reward reward) {
        claimedAt.put(reward.getKey(), Timestamp.ofNow());
    }
    
    @Override
    public void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        claimedAt.forEach((key, timestamp) -> {
            document.put(MongoCodecs.ofKey().serialize(key), MongoCodecs.ofTimestamp().serialize(timestamp));
        });
    }
    
    @Override
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        document.forEach((stringKey, value) -> {
            if (!(MongoCodecs.ofKey().deserialize(stringKey) instanceof Key key) || !(MongoCodecs.ofTimestamp().deserializeObject(value) instanceof Timestamp timestamp)) {
                return;
            }
            
            claimedAt.put(key, timestamp);
        });
    }
    
}