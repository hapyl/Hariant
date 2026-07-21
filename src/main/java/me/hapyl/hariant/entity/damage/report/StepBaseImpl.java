package me.hapyl.hariant.entity.damage.report;

import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class StepBaseImpl implements Step {
    
    private static final DamageMutator EMPTY_MUTATOR = new DamageMutator() {
        @NotNull
        @Override
        public String identify() {
            return "";
        }
        
        @Override
        public double mutate(double damage, double value) {
            return damage;
        }
    };
    
    private final Component component;
    
    StepBaseImpl(double damage) {
        this.component = Component.text("%,.1f".formatted(damage), Colors.GRAY);
    }
    
    @NotNull
    @Override
    public String identify() {
        return "base";
    }
    
    @Override
    public @NotNull DamageMutator damageMutator() {
        return EMPTY_MUTATOR;
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return component;
    }
    
}