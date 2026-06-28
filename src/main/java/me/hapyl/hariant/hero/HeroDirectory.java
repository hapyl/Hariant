package me.hapyl.hariant.hero;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.PlayerDatabaseEntry;
import me.hapyl.hariant.database.problem.Problem;
import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.MongoSerializableConstructor;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class HeroDirectory extends PlayerDatabaseEntry {
    
    private final Map<Hero, HeroInstance> heroes;
    
    @NotNull private Hero selectedHero;
    
    @MongoSerializableConstructor
    private HeroDirectory(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull String parent) {
        super(database, document, parent);
        
        this.heroes = Maps.newHashMap();
        this.selectedHero = HeroRegistry.ARCHER;
    }
    
    @NotNull
    public Hero getSelectedHero() {
        return selectedHero;
    }
    
    @NotNull
    public HeroInstance getSelectedHeroInstance() {
        return heroes.get(selectedHero);
    }
    
    public void setSelectedHero(@NotNull HeroInstance heroInstance) {
        this.selectedHero = heroInstance.getOrigin();
    }
    
    public boolean isOwned(@NotNull Hero hero) {
        return this.heroes.containsKey(hero);
    }
    
    @NotNull
    public Optional<HeroInstance> getHero(@NotNull Hero hero) {
        return Optional.ofNullable(heroes.get(hero));
    }
    
    /**
     * Attempts to create a {@link HeroInstance} for the given {@link Hero}.
     *
     * <p>
     * This method <b>always</b> returns a valid {@link HeroInstance}, by either creating
     * a new instance, or retrieving an exiting instance.
     * </p>
     *
     * <p>
     * Callers are advised to check {@link #isOwned(Hero)} before calling this method.
     * </p>
     *
     * @param hero - The hero create the instance of.
     * @return the hero instance.
     */
    @NotNull
    public HeroInstance createHero(@NotNull Hero hero) {
        HeroInstance heroInstance = heroes.get(hero);
        
        if (heroInstance == null) {
            heroes.put(hero, heroInstance = new HeroInstance(database, hero));
        }
        
        return heroInstance;
    }
    
    @Override
    public void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        // Write heroes
        document.put(
                "instance",
                heroes.values()
                      .stream()
                      .collect(Collectors.toMap(
                              instance -> instance.getOrigin().getKey().toString(),
                              instance -> instance.writeToNewDocument(database, problemReporter)
                      ))
        );
        
        // Write selected hero
        document.put("selected_hero", selectedHero.getKey().toString());
    }
    
    @Override
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        // Read heroes
        document.get("instance", new Document()).forEach((entryKey, entryValue) -> {
            final Key key = Key.ofStringOrNull(entryKey);
            
            if (key == null) {
                problemReporter.report(Problem.severe(HeroDirectory.class, "Malformed key: %s".formatted(entryKey)));
                return;
            }
            
            if (!(entryValue instanceof Document doc)) {
                problemReporter.report(Problem.severe(HeroDirectory.class, "`entryValue` must be a Document, not `%s`!".formatted(entryValue.getClass().getSimpleName())));
                return;
            }
            
            final Hero hero = HeroRegistry.getRegistry().get(key).orElse(null);
            
            if (hero == null) {
                problemReporter.report(Problem.severe(HeroDirectory.class, "Hero `%s` doesn't exist!".formatted(key)));
                return;
            }
            
            final HeroInstance heroInstance = new HeroInstance(database, hero);
            heroInstance.read(database, doc, problemReporter);
            
            this.heroes.put(hero, heroInstance);
        });
        
        // Read selected hero
        this.selectedHero = HeroRegistry.getRegistry().getFromDocument(document, "selected_hero").orElse(HeroRegistry.ARCHER);
        
        // If heroes are empty, means we joined for the first time, create default heroes
        if (this.heroes.isEmpty()) {
            HeroRegistry.defaultHeroes().forEach(this::createHero);
        }
    }
    
}