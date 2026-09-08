package me.hapyl.hariant.entity.damage;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.hariant.term.Terminology;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public enum DamageType implements Named, Terminology {
    
    MELEE(
            Component.text("Melee DMG"),
            Component.text("Damage caused by a melee attack.")
    ) {
        @Override
        public boolean canTriggerFerocity() {
            return true;
        }
    },
    
    RANGED(
            Component.text("Ranged DMG"),
            Component.text("Damage caused by a ranged attack or a projectile.")
    ) {
        @Override
        public boolean canTriggerFerocity() {
            return true;
        }
    },
    
    TALENT(
            Component.text("Talent DMG"),
            Component.text("Damage caused by a non-ultimate talent.")
    ),
    
    ULTIMATE(
            Component.text("Ultimate DMG"),
            Component.text("Damage caused by an ultimate talent.")
    ),
    
    ENVIRONMENT(
            Component.text("Environment DMG"),
            Component.text("Damage caused by environment.")
    ),
    
    ANOMALY(
            Component.text("Anomaly DMG"),
            Component.text("Damage dealt by elemental anomaly.")
    ),
    
    FEROCITY(
            Component.text("Ferocity DMG"),
            Component.text("Damage dealt by a repeated attack.")
    ),
    
    ;
    
    private final Component name;
    private final Component explanation;
    
    DamageType(@NotNull Component name, @NotNull Component explanation) {
        this.name = name;
        this.explanation = explanation;
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public @NotNull Component explainTerm() {
        return explanation;
    }
    
    public boolean canTriggerFerocity() {
        return false;
    }
    
}
