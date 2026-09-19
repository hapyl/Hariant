package me.hapyl.hariant.hero;

import me.hapyl.hariant.element.ElementType;
import org.jetbrains.annotations.NotNull;

public final class HeroProfile {
    
    private final Hero hero;
    
    private @NotNull Archetype archetype;
    private @NotNull ElementType elementType;
    private @NotNull Affiliation affiliation;
    private @NotNull Gender gender;
    private @NotNull Race race;
    
    HeroProfile(@NotNull Hero hero) {
        this.hero = hero;
        this.archetype = Archetype.DAMAGE;
        this.elementType = ElementType.PHYSICAL;
        this.affiliation = Affiliation.NONE;
        this.gender = Gender.OTHER;
        this.race = Race.HUMAN;
    }
    
    public @NotNull Hero getHero() {
        return hero;
    }
    
    public @NotNull Archetype getArchetype() {
        return archetype;
    }
    
    public void setArchetype(@NotNull Archetype archetype) {
        this.archetype = archetype;
    }
    
    public @NotNull ElementType getElementType() {
        return elementType;
    }
    
    public void setElementType(@NotNull ElementType elementType) {
        this.elementType = elementType;
    }
    
    public @NotNull Affiliation getAffiliation() {
        return affiliation;
    }
    
    public void setAffiliation(@NotNull Affiliation affiliation) {
        this.affiliation = affiliation;
    }
    
    public @NotNull Gender getGender() {
        return gender;
    }
    
    public void setGender(@NotNull Gender gender) {
        this.gender = gender;
    }
    
    public @NotNull Race getRace() {
        return race;
    }
    
    public void setRace(@NotNull Race race) {
        this.race = race;
    }
}
