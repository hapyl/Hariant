package me.hapyl.hariant.game.battleground.feature;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.util.Ticking;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface BattlegroundFeature extends Named, Described, Ticking {
    
    @Override
    @NotNull Component getName();
    
    @Override
    @NotNull Component getDescription();
    
    @Override
    void tick();
    
}