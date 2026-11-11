package me.hapyl.hariant.talent.energy;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.util.ComponentFormatter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface EnergyRegenerationRule extends Keyed, ComponentFormatter {
    
    @Override
    @NotNull Key getKey();
    
    @Override
    @NotNull Component format();
    
    double energy();
    
    @NotNull Fulfilment fulfilment();
    
    void fulfil(@NotNull HariantPlayer player);
    
    static @NotNull EnergyRegenerationRule create(@NotNull Key key, double energy, @NotNull Fulfilment fulfilment) {
        return new EnergyRegenerationRuleImpl(key, energy, fulfilment);
    }
    
    static @NotNull EnergyRegenerationRule createOfKeyed(@NotNull Keyed keyed, double energy, @NotNull Fulfilment fulfilment) {
        return create(keyed.getKey(), energy, fulfilment);
    }
    
}