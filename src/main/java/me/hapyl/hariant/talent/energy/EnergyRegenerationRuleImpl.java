package me.hapyl.hariant.talent.energy;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.util.decimal.DecimalFormat;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class EnergyRegenerationRuleImpl implements EnergyRegenerationRule {
    
    private final Key key;
    private final double energy;
    private final Fulfilment fulfilment;
    private final Component component;
    
    EnergyRegenerationRuleImpl(@NotNull Key key, final double energy, @NotNull Fulfilment fulfilment) {
        this.key = key;
        this.fulfilment = fulfilment;
        this.energy = energy;
        this.component = Component.empty()
                                  .append(DecimalFormat.DECIMAL.format(energy))
                                  .appendSpace()
                                  .append(UltimateResourceType.ENERGY.getPrefix())
                                  .appendSpace()
                                  .append(Component.text("("))
                                  .append(fulfilment.cooldown())
                                  .append(Component.text("/"))
                                  .append(Component.text(fulfilment.threshold()))
                                  .append(Component.text(")"));
    }
    
    @Override
    public @NotNull Key getKey() {
        return key;
    }
    
    @Override
    public @NotNull Component format() {
        return component;
    }
    
    @Override
    public double energy() {
        return energy;
    }
    
    @Override
    public @NotNull Fulfilment fulfilment() {
        return fulfilment;
    }
    
    @Override
    public void fulfil(@NotNull HariantPlayer player) {
        if (!player.energyRegenerationRules().test(this)) {
            return;
        }
        
        player.incrementUltimateResource(energy);
    }
    
}