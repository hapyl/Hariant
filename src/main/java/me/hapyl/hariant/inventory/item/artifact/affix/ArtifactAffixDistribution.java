package me.hapyl.hariant.inventory.item.artifact.affix;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ArtifactAffixDistribution {
    
    @NotNull ArtifactAffix randomAffix();
    
    @NotNull List<? extends ArtifactAffix> listAffixes();
    
    static @NotNull ArtifactAffixDistribution create(@NotNull List<? extends ArtifactAffix> affixes) {
        return new ArtifactAffixDistributionImpl(affixes);
    }
    
}
