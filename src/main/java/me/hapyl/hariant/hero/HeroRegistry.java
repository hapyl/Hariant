package me.hapyl.hariant.hero;

import me.hapyl.hariant.hero.alchemist.HeroAlchemist;
import me.hapyl.hariant.hero.archer.HeroArcher;
import me.hapyl.hariant.hero.blast_knight.HeroBlastKnight;
import me.hapyl.hariant.hero.inferno.HeroInferno;
import me.hapyl.hariant.hero.mage.HeroMage;
import me.hapyl.hariant.hero.nyx.HeroNyx;
import me.hapyl.hariant.hero.pytaria.HeroPytaria;
import me.hapyl.hariant.hero.troll.HeroTroll;
import me.hapyl.hariant.registry.StaticRegistry;
import me.hapyl.hariant.registry.StaticRegistryMap;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class HeroRegistry extends StaticRegistry<Hero> {
    
    public static final HeroArcher ARCHER;
    public static final HeroPytaria PYTARIA;
    public static final HeroAlchemist ALCHEMIST;
    public static final HeroMage MAGE;
    public static final HeroTroll TROLL;
    public static final HeroInferno INFERNO;
    public static final HeroNyx NYX;
    public static final HeroBlastKnight BLAST_KNIGHT;
    
    private static final StaticRegistryMap<Hero> REGISTRY;
    private static final List<Hero> DEFAULT_HEROES;
    
    static {
        REGISTRY = StaticRegistry.requestRegistry(HeroRegistry.class);
        
        ARCHER = REGISTRY.register("archer", HeroArcher::new);
        PYTARIA = REGISTRY.register("pytaria", HeroPytaria::new);
        ALCHEMIST = REGISTRY.register("alchemist", HeroAlchemist::new);
        MAGE = REGISTRY.register("mage", HeroMage::new);
        TROLL = REGISTRY.register("troll", HeroTroll::new);
        INFERNO = REGISTRY.register("inferno", HeroInferno::new);
        NYX = REGISTRY.register("nyx", HeroNyx::new);
        BLAST_KNIGHT = REGISTRY.register("blast_knight", HeroBlastKnight::new);
        
        // Assign default heroes, which are: [ ARCHER, PYTARIA, ALCHEMIST, MAGE ]
        DEFAULT_HEROES = List.of(ARCHER, PYTARIA, ALCHEMIST, MAGE);
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
