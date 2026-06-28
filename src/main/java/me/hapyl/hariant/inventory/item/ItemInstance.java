package me.hapyl.hariant.inventory.item;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.database.InstanceImpl;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.MongoSerializable;
import me.hapyl.hariant.database.serialize.codec.MongoCodecs;
import me.hapyl.hariant.inventory.drop.Drop;
import me.hapyl.hariant.util.Hoverable;
import me.hapyl.hariant.util.Timestamp;
import me.hapyl.hariant.util.UniquelyIdentified;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Objects;
import java.util.UUID;

public class ItemInstance
        extends
        InstanceImpl<Item>
        implements
        UniquelyIdentified, MongoSerializable, ItemCreator, Hoverable,
        Drop {
    
    private final UUID uuid;
    
    @NotNull
    private Timestamp timestamp;
    
    public ItemInstance(@NotNull PlayerDatabase playerDatabase, @NotNull Item origin, @NotNull UUID uuid) {
        super(playerDatabase, origin);
        
        this.uuid = uuid;
        this.timestamp = Timestamp.ofNow();
    }
    
    @NotNull
    public Timestamp getTimestamp() {
        return timestamp;
    }
    
    @NotNull
    @Override
    public UUID getUuid() {
        return uuid;
    }
    
    @Override
    @OverridingMethodsMustInvokeSuper
    public void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        MongoCodecs.ofKey().write(document, "key", origin.getKey());
        MongoCodecs.ofTimestamp().write(document, "timestamp", timestamp);
    }
    
    @Override
    @OverridingMethodsMustInvokeSuper
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        // We skip `key` because it's set on init stage via `origin`
        timestamp = MongoCodecs.ofTimestamp().read(document, "timestamp").orElseGet(Timestamp::ofNow);
    }
    
    @Override
    public @NotNull ItemBuilder createBuilder() {
        return origin.getIcon().createBuilder();
    }
    
    @Override
    public @NotNull Key getKey() {
        return origin.getKey();
    }
    
    @Override
    public @NotNull Component getNameStyled() {
        return origin.getNameStyled();
    }
    
    @NotNull
    @Override
    public HoverEvent<?> createHoverEvent() {
        return createItem().asHoverEvent();
    }
    
    @Override
    public final int hashCode() {
        return Objects.hashCode(this.uuid);
    }
    
    @Override
    public final boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final ItemInstance that = (ItemInstance) object;
        return Objects.equals(this.uuid, that.uuid);
    }
    
    @Override
    public String toString() {
        return "%s(%s, %s)".formatted(this.getClass().getSimpleName(), origin.getClass().getSimpleName(), uuid.toString());
    }
    
}
