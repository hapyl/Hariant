package me.hapyl.hariant.daily;

import me.hapyl.hariant.Colors;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.hero.Archetype;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class DailyArchetypeImpl extends DailyImpl {
    
    private final Archetype archetype;
    
    DailyArchetypeImpl(@NotNull Archetype archetype, @NotNull Component name) {
        super(
                name,
                goal -> Component.empty()
                              .append(Component.text("Play "))
                              .append(Component.text(goal, Colors.GREEN))
                              .append(Component.text(" games as "))
                              .append(archetype)
                              .append(Component.text(".")),
                DailyTier.TIER_2,
                2,
                4
        );
        
        this.archetype = archetype;
    }
    
    @Override
    public boolean canGenerate(@NotNull PlayerDatabase database) {
        // Make the player actually owns a hero with the given archetype
        return database.heroDirectory.stream().anyMatch(heroInstance -> heroInstance.getOrigin().getProfile().getArchetype() == archetype);
    }
    
    static @NotNull DailyArchetypeImpl create(@NotNull Archetype archetype, @NotNull Component name) {
        return new DailyArchetypeImpl(archetype, name);
    }
    
}