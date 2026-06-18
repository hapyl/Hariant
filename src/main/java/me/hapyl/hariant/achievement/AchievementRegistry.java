package me.hapyl.hariant.achievement;

import me.hapyl.hariant.registry.StaticRegistry;
import me.hapyl.hariant.registry.StaticRegistryMap;
import org.jetbrains.annotations.NotNull;

public final class AchievementRegistry extends StaticRegistry<Achievement> {
    
    public static final Achievement FIRST_GAME;
    
    private static final StaticRegistryMap<Achievement> REGISTRY;
    
    static {
        REGISTRY = requestRegistry(AchievementRegistry.class);
        
        FIRST_GAME = REGISTRY.register("first_game", AchievementFirstGame::new);
    }
    
    public static @NotNull StaticRegistryMap<Achievement> getRegistry() {
        return REGISTRY;
    }
    
}