package me.hapyl.hariant.inventory.cosmetic;

import me.hapyl.hariant.registry.StaticRegistry;
import me.hapyl.hariant.registry.StaticRegistryMap;
import org.jetbrains.annotations.NotNull;

public final class CosmeticRegistry extends StaticRegistry<Cosmetic> {
    
    private static final StaticRegistryMap<Cosmetic> REGISTRY;
    
    static {
        REGISTRY = requestRegistry(CosmeticRegistry.class);
    }
    
    public static @NotNull StaticRegistryMap<Cosmetic> getRegistry() {
        return REGISTRY;
    }
    
}