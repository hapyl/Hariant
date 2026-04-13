package me.hapyl.hariant.talent;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.text.SmallCaps;
import me.hapyl.hariant.entity.SmallCapsComponent;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public enum TalentType implements Named, Described, SmallCapsComponent {
    
    DAMAGE("Damage", "Deals damage to enemies."),
    ENHANCE("Enhance", "Enhances oneselves for the battle."),
    SUPPORT("Support", "Supports a teammate by buffing or healing them."),
    DEFENSE("Defense", "Provides a shield for self or a teammate."),
    IMPAIR("Impair", "Weakens enemies by hindering them."),
    MOVEMENT("Movement", "Provides a way to flee or enter a fight or just to have fun.");
    
    private final Component name;
    private final Component description;
    private final Component smallCaps;
    
    TalentType(@NotNull String name, @NotNull String description) {
        this.name = Component.text(name);
        this.description = Component.text(description);
        this.smallCaps = Component.text(SmallCaps.format(name));
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public Component getDescription() {
        return description;
    }
    
    @NotNull
    @Override
    public Component asSmallCaps() {
        return smallCaps;
    }
}
