package me.hapyl.hariant.inventory.item;

import me.hapyl.hariant.inventory.item.artifact.*;
import me.hapyl.hariant.registry.StaticRegistry;
import me.hapyl.hariant.registry.StaticRegistryMap;
import org.jetbrains.annotations.NotNull;

public final class ItemRegistry extends StaticRegistry<Item> {
    
    public static final ItemArtifact ARTIFACT_UNSTABLE_LIGHTNING_GEM;
    public static final ItemArtifact ARTIFACT_BLOODY_ROSE;
    public static final ItemArtifact ARTIFACT_PHILOSOPHERS_STONE;
    public static final ItemArtifact ARTIFACT_MAGIC_CODEX;
    
    private static final StaticRegistryMap<Item> REGISTRY;
    
    static {
        REGISTRY = StaticRegistry.requestRegistry(ItemRegistry.class);
        
        ARTIFACT_UNSTABLE_LIGHTNING_GEM = REGISTRY.register("artifact_unstable_lightning_gem", ItemArtifactUnstableLightningGem::new);
        ARTIFACT_BLOODY_ROSE = REGISTRY.register("artifact_bloody_rose", ItemArtifactBloodyRose::new);
        ARTIFACT_PHILOSOPHERS_STONE = REGISTRY.register("artifact_philosophers_stone", ItemArtifactPhilosophersStone::new);
        ARTIFACT_MAGIC_CODEX = REGISTRY.register("artifact_magic_codex", ItemArtifactMagicCodex::new);
    }
    
    @NotNull
    public static StaticRegistryMap<Item> getRegistry() {
        return REGISTRY;
    }
}
