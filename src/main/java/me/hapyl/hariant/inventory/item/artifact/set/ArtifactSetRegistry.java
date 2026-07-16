package me.hapyl.hariant.inventory.item.artifact.set;

import me.hapyl.hariant.registry.StaticRegistry;
import me.hapyl.hariant.registry.StaticRegistryMap;
import org.jetbrains.annotations.NotNull;

public final class ArtifactSetRegistry extends StaticRegistry<ArtifactSet> {
    
    public static final ArtifactSet ELECTRIFYING;
    public static final ArtifactSet BREEZE;
    public static final ArtifactSet ALCHEMICAL_SYNERGY;
    public static final ArtifactSet TOME_OF_THE_ENLIGHTENED;
    public static final ArtifactSet SOUL_FRACTURE;
    public static final ArtifactSet FUNNY_PRANK;
    public static final ArtifactSet REWIND;
    public static final ArtifactSet SEARING_INFERNO;
    public static final ArtifactSet RECHARGE;
    public static final ArtifactSet ECLIPSE;
    public static final ArtifactSet BULWARK;
    
    private static final StaticRegistryMap<ArtifactSet> REGISTRY;
    
    static {
        REGISTRY = requestRegistry(ArtifactSetRegistry.class);
        
        ELECTRIFYING = REGISTRY.register("artifact_set_electrifying", ArtifactSetElectrifying::new);
        BREEZE = REGISTRY.register("artifact_set_breeze", ArtifactSetBreeze::new);
        ALCHEMICAL_SYNERGY = REGISTRY.register("artifact_set_alchemical_synergy", ArtifactSetAlchemicalSynergy::new);
        TOME_OF_THE_ENLIGHTENED = REGISTRY.register("artifact_set_tome_of_the_enlightened", ArtifactSetTomeOfTheEnlightened::new);
        SOUL_FRACTURE = REGISTRY.register("artifact_set_soul_fracture", ArtifactSetSoulFracture::new);
        FUNNY_PRANK = REGISTRY.register("artifact_set_funny_prank", ArtifactSetFunnyPrank::new);
        REWIND = REGISTRY.register("artifact_set_rewind", ArtifactSetRewind::new);
        SEARING_INFERNO = REGISTRY.register("artifact_searing_inferno", ArtifactSetSearingInferno::new);
        RECHARGE = REGISTRY.register("artifact_searing_recharge", ArtifactSetRecharge::new);
        ECLIPSE = REGISTRY.register("artifact_searing_eclipse", ArtifactSetEclipse::new);
        BULWARK = REGISTRY.register("artifact_set_bulwark", ArtifactSetBulwark::new);
    }
    
    private ArtifactSetRegistry() {
    }
    
    public static @NotNull StaticRegistryMap<ArtifactSet> getRegistry() {
        return REGISTRY;
    }
    
}