package me.hapyl.hariant.inventory.item.artifact;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Enums;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.problem.Problem;
import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.MongoSerializable;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSet;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSetRegistry;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ArtifactFilter implements MongoSerializable {
    
    private final @NotNull Set<ArtifactSet> filteringSets;
    private final @NotNull Set<AttributeType> filteringAttributes;
    
    public ArtifactFilter() {
        this.filteringSets = Sets.newHashSet();
        this.filteringAttributes = Sets.newHashSet();
    }
    
    public boolean isEmpty() {
        return filteringSets.isEmpty() && filteringAttributes.isEmpty();
    }
    
    public @NotNull Set<ArtifactSet> getFilteringSets() {
        return filteringSets;
    }
    
    public @NotNull Set<AttributeType> getFilteringAttributes() {
        return filteringAttributes;
    }
    
    public @NotNull @Unmodifiable Set<? extends AttributeType> getFilteringAttributes(@NotNull Collection<? extends AttributeType> attributes) {
        return filteringAttributes.stream()
                                  .filter(attributes::contains)
                                  .collect(Collectors.toSet());
    }
    
    public boolean test(@NotNull ItemArtifactInstance itemArtifactInstance, @NotNull Collection<? extends AttributeType> possibleAttributes) {
        if (!filteringSets.isEmpty() && !filteringSets.contains(itemArtifactInstance.getArtifactSet())) {
            return false;
        }
        
        if (!filteringAttributes.isEmpty()) {
            // If none of the possible attributes are filtered, we keep the artifact
            if (possibleAttributes.stream().noneMatch(filteringAttributes::contains)) {
                return true;
            }
            
            // Otherwise check whether we're filtering the attribute
            return filteringAttributes.contains(itemArtifactInstance.getArtifactAffix().getAttributeType());
        }
        
        return true;
    }
    
    @Override
    public void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        // Write filtering sets
        if (!filteringSets.isEmpty()) {
            document.put("filtering_sets", filteringSets.stream().map(ArtifactSet::getKey).map(Key::toString).toList());
        }
        
        // Write filtering attributes
        if (!filteringAttributes.isEmpty()) {
            document.put("filtering_attributes", filteringAttributes.stream().map(AttributeType::name).map(String::toLowerCase).toList());
        }
    }
    
    @Override
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        // Read filtering sets
        readTo(filteringSets, document, "filtering_sets", string -> ArtifactSetRegistry.getRegistry().get(string).orElse(null), problemReporter, "Invalid artifact set: %s!"::formatted);
        
        // Read filtering attributes
        readTo(filteringAttributes, document, "filtering_attributes", string -> Enums.byName(AttributeType.class, string), problemReporter, "Invalid attribute type: %s!"::formatted);
    }
    
    public void reset() {
        filteringSets.clear();
        filteringAttributes.clear();
    }
    
    // #norender
    private static <T> void readTo(@NotNull Set<? super T> destination, @NotNull Document document, @NotNull String key, @NotNull Function<String, @Nullable T> function, @NotNull ProblemReporter problemReporter, @NotNull Function<String, String> errorMessage) {
        document.get(key, Lists.<String>newArrayList()).forEach(string -> {
            final T object = function.apply(string);
            
            if (object == null) {
                problemReporter.report(Problem.severe(ArtifactFilter.class, errorMessage.apply(string)));
                return;
            }
            
            destination.add(object);
        });
    }
    
}