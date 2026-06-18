package me.hapyl.hariant.inventory.item;

import me.hapyl.hariant.inventory.item.artifact.*;
import me.hapyl.hariant.registry.StaticRegistry;
import me.hapyl.hariant.registry.StaticRegistryMap;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public final class ItemRegistry extends StaticRegistry<Item> {
    
    public static final ItemArtifact ARTIFACT_UNSTABLE_LIGHTNING_GEM;
    public static final ItemArtifact ARTIFACT_BLOODY_ROSE;
    public static final ItemArtifact ARTIFACT_PHILOSOPHERS_STONE;
    public static final ItemArtifact ARTIFACT_MAGIC_CODEX;
    public static final ItemArtifact ARTIFACT_SHATTERED_SOUL;
    public static final ItemArtifact ARTIFACT_WHOOPEE_CUSHION;
    public static final ItemArtifact ARTIFACT_ANTIQUE_POCKET_WATCH;
    public static final ItemArtifact ARTIFACT_INFERNAL_CRUCIBLE;
    public static final ItemArtifact ARTIFACT_LIGHTNING_IN_A_BOTTLE;
    
    private static final StaticRegistryMap<Item> REGISTRY;
    
    static {
        REGISTRY = StaticRegistry.requestRegistry(ItemRegistry.class);
        
        ARTIFACT_UNSTABLE_LIGHTNING_GEM = REGISTRY.register("artifact_unstable_lightning_gem", ItemArtifactUnstableLightningGem::new);
        ARTIFACT_BLOODY_ROSE = REGISTRY.register("artifact_bloody_rose", ItemArtifactBloodyRose::new);
        ARTIFACT_PHILOSOPHERS_STONE = REGISTRY.register("artifact_philosophers_stone", ItemArtifactPhilosophersStone::new);
        ARTIFACT_MAGIC_CODEX = REGISTRY.register("artifact_magic_codex", ItemArtifactMagicCodex::new);
        ARTIFACT_SHATTERED_SOUL = REGISTRY.register("artifact_shattered_soul", ItemArtifactShatteredSoul::new);
        ARTIFACT_WHOOPEE_CUSHION = REGISTRY.register("artifact_whoopee_cushion", ItemArtifactWhoopeeCushion::new);
        ARTIFACT_ANTIQUE_POCKET_WATCH = REGISTRY.register("artifact_antique_pocket_watch", ItemArtifactAntiquePocketWatch::new);
        ARTIFACT_INFERNAL_CRUCIBLE = REGISTRY.register("artifact_infernal_crucible", ItemArtifactInfernalCrucible::new);
        ARTIFACT_LIGHTNING_IN_A_BOTTLE = REGISTRY.register("artifact_lightning_in_a_bottle", ItemArtifactLightningInABottle::new);
    }
    
    @NotNull
    public static StaticRegistryMap<Item> getRegistry() {
        return REGISTRY;
    }
    
    @NotNull
    public static <I extends Item> Stream<I> streamOfType(@NotNull Class<I> itemClass) {
        return REGISTRY.values()
                       .stream()
                       .filter(itemClass::isInstance)
                       .map(itemClass::cast);
    }
    
}