package me.hapyl.hariant.inventory.item.artifact.affix;

import me.hapyl.hariant.attribute.AttributeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ArtifactAffixDistribution {
    
    @NotNull ArtifactAffix randomAffix();
    
    @NotNull List<? extends ArtifactAffix> listAffixes();
    
    default @NotNull List<? extends AttributeType> listAttributes() {
        return this.listAffixes().stream().map(ArtifactAffix::getAttributeType).toList();
    }
    
    static @NotNull ArtifactAffixDistribution create(@NotNull List<? extends ArtifactAffix> affixes) {
        return new ArtifactAffixDistributionImpl(affixes);
    }
    
}
