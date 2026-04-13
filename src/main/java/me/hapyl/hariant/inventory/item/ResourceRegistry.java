package me.hapyl.hariant.inventory.item;

import me.hapyl.hariant.inventory.item.resource.ResourceCatCoins;
import me.hapyl.hariant.inventory.item.resource.ResourceRuby;
import me.hapyl.hariant.registry.StaticRegistry;
import me.hapyl.hariant.registry.StaticRegistryMap;
import org.jetbrains.annotations.NotNull;

public final class ResourceRegistry extends StaticRegistry<Resource> {
    
    public static final ResourceCatCoins CAT_COINS;
    public static final ResourceRuby RUBY;
    
    private static final StaticRegistryMap<Resource> REGISTRY;
    
    static {
        REGISTRY = StaticRegistry.requestRegistry(ResourceRegistry.class);
        
        CAT_COINS = REGISTRY.register("cat_coins", ResourceCatCoins::new);
        RUBY = REGISTRY.register("ruby", ResourceRuby::new);
    }
    
    @NotNull
    public static StaticRegistryMap<Resource> getRegistry() {
        return REGISTRY;
    }
    
}
