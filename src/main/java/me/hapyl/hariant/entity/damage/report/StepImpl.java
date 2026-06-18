package me.hapyl.hariant.entity.damage.report;

import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.util.Identified;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class StepImpl implements Step {
    
    private final Identified identified;
    private final DamageMutator damageMutator;
    private final Component component;
    
    StepImpl(@NotNull Identified identified, @NotNull DamageMutator damageMutator, double value, double damageBeforeMutation, double damageAfterMutation) {
        this.identified = identified;
        this.damageMutator = damageMutator;
        this.component = Component.text(
                "%,.1f %s %,.3f = %,.1f".formatted(damageBeforeMutation, damageMutator.identify(), value, damageAfterMutation),
                Colors.GRAY
        );
    }
    
    @NotNull
    @Override
    public String identify() {
        return identified.identify();
    }
    
    @NotNull
    @Override
    public DamageMutator damageMutator() {
        return damageMutator;
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return component;
    }
}
