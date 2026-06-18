package me.hapyl.hariant.entity.damage.report;

import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.util.Identified;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public interface Step extends Identified, ComponentLike {
    
    @NotNull
    @Override
    String identify();
    
    @NotNull
    DamageMutator damageMutator();
    
    @NotNull
    @Override
    Component asComponent();
}
