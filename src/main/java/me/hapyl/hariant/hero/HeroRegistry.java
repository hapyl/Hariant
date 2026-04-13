package me.hapyl.hariant.hero;

import me.hapyl.hariant.hero.alchemist.HeroAlchemist;
import me.hapyl.hariant.hero.archer.HeroArcher;
import me.hapyl.hariant.hero.pytaria.HeroPytaria;
import me.hapyl.hariant.registry.StaticRegistry;
import me.hapyl.hariant.registry.StaticRegistryMap;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class HeroRegistry extends StaticRegistry<Hero> {
    
    public static final HeroArcher ARCHER;
    public static final HeroPytaria PYTARIA;
    public static final HeroAlchemist ALCHEMIST;
    
    private static final StaticRegistryMap<Hero> REGISTRY;
    private static final List<Hero> DEFAULT_HEROES;
    
    static {
        REGISTRY = StaticRegistry.requestRegistry(HeroRegistry.class);
        
        ARCHER = REGISTRY.register("archer", HeroArcher::new);
        PYTARIA = REGISTRY.register("pytaria", HeroPytaria::new);
        ALCHEMIST = REGISTRY.register("alchemist", HeroAlchemist::new);
        
        // Assign default heroes
        DEFAULT_HEROES = List.of(ARCHER, PYTARIA, ALCHEMIST);
    }
    
    private HeroRegistry() {
    }
    
    @NotNull
    public static StaticRegistryMap<Hero> getRegistry() {
        return REGISTRY;
    }
    
    @NotNull
    public static List<Hero> defaultHeroes() {
        return DEFAULT_HEROES;
    }
    
}
