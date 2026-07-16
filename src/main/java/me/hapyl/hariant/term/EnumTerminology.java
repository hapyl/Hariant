package me.hapyl.hariant.term;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public enum EnumTerminology implements Terminology {
    
    AREA_OF_EFFECT(
            Component.text("AoE"),
            Component.text("Area of Effect that affects targets within a fixed range.")
    ),
    
    TRUE_DAMAGE(
            Component.text("True DMG"),
            Component.text("This type of damage ignores DEF.")
    ),
    
    ALL_TYPE_RESISTANCE(
            Component.text("All-Type RES"),
            Component.text("Resistance to all element types.")
    ),
    
    ALL_TYPE_DAMAGE(
            Component.text("All-Type DMG"),
            Component.text("Bonus damage to all element types.")
    ),
    
    ELEMENTAL_ANOMALY(
            Component.text("Elemental Anomaly"),
            Component.text("Filling the elemental gauge fully triggers elemental anomaly of the corresponding element.")
    ),
    
    CRITICAL_DAMAGE(
            Component.text("Critical DMG"),
            Component.text("Multiplies the original damage by Crit Damage.")
    ),
    
    INTERRUPTION(
            Component.text("Interruption"),
            Component.text("Interrupts the current action and cancels any talent cast.")
    ),
    
    BASE_CHANCE(
            Component.text("Base Chance"),
            Component.text("A random chance that can be increased by Luck attribute.")
    ),
    
    ;
    
    private final Component name;
    private final Component explanation;
    
    EnumTerminology(@NotNull Component name, @NotNull Component explanation) {
        this.name = name;
        this.explanation = explanation;
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public Component explainTerm() {
        return explanation;
    }
    
}