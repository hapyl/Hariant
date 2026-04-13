package me.hapyl.hariant.hero;

import me.hapyl.hariant.element.ElementType;
import org.jetbrains.annotations.NotNull;

public final class HeroProfile {
    
    private final Hero hero;
    
    @NotNull private Archetype archetype;
    @NotNull private ElementType elementType;
    @NotNull private Affiliation affiliation;
    @NotNull private Gender gender;
    
    HeroProfile(@NotNull Hero hero) {
        this.hero = hero;
        this.archetype = Archetype.DAMAGE;
        this.elementType = ElementType.PHYSICAL;
        this.affiliation = Affiliation.NONE;
        this.gender = Gender.OTHER;
    }
    
    @NotNull
    public Hero getHero() {
        return hero;
    }
    
    @NotNull
    public Archetype getArchetype() {
        return archetype;
    }
    
    public void setArchetype(@NotNull Archetype archetype) {
        this.archetype = archetype;
    }
    
    @NotNull
    public ElementType getElementType() {
        return elementType;
    }
    
    public void setElementType(@NotNull ElementType elementType) {
        this.elementType = elementType;
    }
    
    @NotNull
    public Affiliation getAffiliation() {
        return affiliation;
    }
    
    public void setAffiliation(@NotNull Affiliation affiliation) {
        this.affiliation = affiliation;
    }
    
    @NotNull
    public Gender getGender() {
        return gender;
    }
    
    public void setGender(@NotNull Gender gender) {
        this.gender = gender;
    }
}
