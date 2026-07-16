package me.hapyl.hariant.game.battleground.feature;

import me.hapyl.hariant.annotate.AutoRegisteredListener;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

@AutoRegisteredListener
public abstract class BattlegroundFeatureImpl implements BattlegroundFeature {
    
    private final Component name;
    private final Component description;
    
    public BattlegroundFeatureImpl(@NotNull Component name, @NotNull Component description) {
        this.name = name;
        this.description = description;
        
        AutoRegisteredListener.Registry.register(this);
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public @NotNull Component getDescription() {
        return description;
    }
    
    @Override
    public abstract void tick();
    
}