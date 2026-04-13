package me.hapyl.hariant.entity.damage;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.hariant.term.Term;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public enum DamageType implements Named, Term {
    
    MELEE(
            Component.text("Melee DMG"),
            Component.text("Damage caused by a melee attack.")
    ),
    
    RANGED(
            Component.text("Ranged DMG"),
            Component.text("Damage caused by a ranged attack or a projectile.")
    ),
    
    TALENT(
            Component.text("Talent DMG"),
            Component.text("Damage caused by a non-ultimate talent.")
    ),
    
    ULTIMATE(
            Component.text("Ultimate DMG"),
            Component.text("Damage caused by a an ultimate talent.")
    ),
    
    ENVIRONMENT(
            Component.text("Environment DMG"),
            Component.text("Damage caused by environment.")
    ),
    
    ANOMALY(
            Component.text("Anomaly DMG"),
            Component.text("Damage dealt by elemental anomaly.")
    ),
    
    ;
    
    private final Component name;
    private final Component explanation;
    
    DamageType(@NotNull Component name, @NotNull Component explanation) {
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
