package me.hapyl.hariant.inventory.item.artifact.affix;

import me.hapyl.hariant.Hariant;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public final class ArtifactAffixDistributionImpl implements ArtifactAffixDistribution {
    
    private final List<? extends ArtifactAffix> affixes;
    
    private final int totalWeight;
    
    ArtifactAffixDistributionImpl(@NotNull List<? extends ArtifactAffix> affixes) {
        this.affixes = affixes;
        this.totalWeight = affixes.stream().mapToInt(ArtifactAffix::getWeight).sum();
    }
    
    @Override
    public @NotNull ArtifactAffix randomAffix() {
        final Random random = Hariant.getRandom();
        
        final double targetWeight = random.nextDouble() * totalWeight;
        double cumulativeWeight = 0;
        
        for (ArtifactAffix affix : affixes) {
            cumulativeWeight += affix.getWeight();
            
            if (targetWeight <= cumulativeWeight) {
                return affix;
            }
        }
        
        throw new IllegalStateException("Cannot generate affix because distribution is empty!");
    }
    
    @Override
    public @NotNull List<? extends ArtifactAffix> listAffixes() {
        return affixes;
    }
    
}