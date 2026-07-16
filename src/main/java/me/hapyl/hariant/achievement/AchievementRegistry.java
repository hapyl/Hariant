package me.hapyl.hariant.achievement;

import me.hapyl.hariant.registry.StaticRegistry;
import me.hapyl.hariant.registry.StaticRegistryMap;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public final class AchievementRegistry extends StaticRegistry<Achievement> {
    
    public static final Achievement FIRST_GAME;
    public static final Achievement TROLL_LAUGHING_OUT_LOUD;
    public static final Achievement BEYOND_CLOUDS;
    
    private static final StaticRegistryMap<Achievement> REGISTRY;
    
    static {
        REGISTRY = requestRegistry(AchievementRegistry.class);
        
        FIRST_GAME = REGISTRY.register("first_game", AchievementFirstGame::new);
        TROLL_LAUGHING_OUT_LOUD = REGISTRY.register("troll_laughing_out_loud", AchievementTrollLaughingOutLoud::new);
        BEYOND_CLOUDS = REGISTRY.register("beyond_clouds", AchievementBeyondClouds::new);
    }
    
    public static @NotNull StaticRegistryMap<Achievement> getRegistry() {
        return REGISTRY;
    }
    
    public static Stream<? extends Achievement> getByCategory(@NotNull AchievementCategory category) {
        return REGISTRY.values().stream().filter(achievement -> achievement.getCategory() == category);
    }
    
}