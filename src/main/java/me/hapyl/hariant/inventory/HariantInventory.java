package me.hapyl.hariant.inventory;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.math.Numbers;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.PlayerDatabaseEntry;
import me.hapyl.hariant.database.problem.Problem;
import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.MongoSerializableConstructor;
import me.hapyl.hariant.database.serialize.codec.MongoCodecs;
import me.hapyl.hariant.inventory.item.*;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class HariantInventory extends PlayerDatabaseEntry {
    
    private final Map<UUID, ItemInstance> items;
    private final Map<Resource, Integer> materials;
    
    @MongoSerializableConstructor
    private HariantInventory(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull String parent) {
        super(database, document, parent);
        
        this.items = Maps.newHashMap();
        this.materials = Maps.newHashMap();
    }
    
    @Override
    public void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        final Document documentItems = new Document();
        final Document documentMaterials = new Document();
        
        // Write items
        this.items.forEach((uuid, item) -> {
            documentItems.put(uuid.toString(), item.writeToNewDocument(database, problemReporter));
        });
        
        // Write materials
        this.materials.forEach((material, amount) -> {
            documentMaterials.put(material.getKey().toString(), amount);
        });
        
        document.put("items", documentItems);
        document.put("materials", documentMaterials);
    }
    
    @Override
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        // Read items
        document.get("items", new Document()).forEach((entryKey, entryValue) -> {
            final UUID uuid = BukkitUtils.getUuidFromString(entryKey);
            
            if (uuid == null) {
                problemReporter.report(Problem.severe(HariantInventory.class, "Entry key is not UUID!"));
                return;
            }
            
            if (!(entryValue instanceof Document doc)) {
                problemReporter.report(Problem.severe(HariantInventory.class, "`entryValue` must be a Document, not `%s`!".formatted(entryValue.getClass().getSimpleName())));
                return;
            }
            
            final Key key = MongoCodecs.ofKey().read(doc, "key").orElse(null);
            
            if (key == null) {
                problemReporter.report(Problem.severe(HariantInventory.class, "Missing key `key` in `%s`!".formatted(uuid)));
                return;
            }
            
            final Item item = ItemRegistry.getRegistry().get(key).orElse(null);
            
            if (item == null) {
                problemReporter.report(Problem.severe(HariantInventory.class, "Item with key `%s` doesn't exist!".formatted(key)));
                return;
            }
            
            final ItemInstance itemInstance = item.newInstance(database, uuid);
            itemInstance.read(database, doc, problemReporter);
            
            items.put(uuid, itemInstance);
        });
        
        // Read materials
        document.get("materials", new Document()).forEach((entryKey, entryValue) -> {
            final Key key = Key.ofStringOrNull(entryKey);
            final int amount = Numbers.toInt(entryValue);
            
            if (key == null) {
                problemReporter.report(Problem.severe(HariantInventory.class, "Entry key is not a Key!"));
                return;
            }
            
            final Resource resource = ResourceRegistry.getRegistry().get(key).orElse(null);
            
            if (resource == null) {
                problemReporter.report(Problem.severe(HariantInventory.class, "Resources with key `%s` doesn't exist!".formatted(key)));
                return;
            }
            
            materials.put(resource, amount);
        });
    }
    
    // *-* Resources *-* //
    
    public boolean hasResource(@NotNull Resource resource, int amount) {
        return materials.getOrDefault(resource, 0) >= amount;
    }
    
    public int getResource(@NotNull Resource resource) {
        return materials.getOrDefault(resource, 0);
    }
    
    public boolean canAddResource(@NotNull Resource resource, int amount) {
        return getResource(resource) + amount < resource.maxStackSize();
    }
    
    public void addResource(@NotNull Resource resource, int amount) {
        materials.compute(resource, (_resource, _amount) -> {
            // Increment the value and clamp between `0` - `maxStackSize()`
            return Math.clamp((_amount != null ? _amount : 0) + amount, 0, resource.maxStackSize());
        });
    }
    
    public void removeResource(@NotNull Resource resource, int amount) {
        this.addResource(resource, -amount);
    }
    
    // *-* Items *-* //
    
    public @NotNull ItemInstance createItem(@NotNull Item item) {
        // TODO (xanyjl @ Tuesday, June 23) -> Add a check whether the item can be added
        
        final ItemInstance itemInstance = item.newInstance(database, UUID.randomUUID());
        itemInstance.onInstanceCreated();
        
        items.put(itemInstance.getUuid(), itemInstance);
        
        return itemInstance;
    }
    
    public boolean destroyItem(@NotNull UUID uuid) {
        final ItemInstance destroyedItem = items.remove(uuid);
        
        if (destroyedItem != null) {
            destroyedItem.onInstanceDestroyed();
            return true;
        }
        
        return false;
    }
    
    @NotNull
    public <I extends ItemInstance> Optional<I> getItemByUuid(@NotNull UUID uuid, @NotNull Class<I> instanceClass) {
        final ItemInstance item = items.get(uuid);
        
        return instanceClass.isInstance(item) ? Optional.of(instanceClass.cast(item)) : Optional.empty();
    }
    
    @NotNull
    public <I extends ItemInstance> Stream<I> getItemsByClass(@NotNull Class<I> instanceClass) {
        return items.values().stream()
                    .filter(instanceClass::isInstance)
                    .map(instanceClass::cast);
    }
    
}