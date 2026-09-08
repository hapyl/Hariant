package me.hapyl.hariant.game.battleground.feature;

import me.hapyl.hariant.annotate.AutoRegisteredListener;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.game.GameInstance;
import me.hapyl.hariant.game.WinResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
    
    @Override
    public void onCreate(@NotNull Iterable<? extends HariantPlayer> players) {
    }
    
    @Override
    public void onDestroy(@NotNull Iterable<? extends HariantPlayer> players, @NotNull WinResult result) {
    }
    
    @Override
    public void onFinalize(@NotNull List<? extends HariantPlayer> players, @NotNull WinResult result) {
    }
    
    @Override
    public void onKill(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player, @NotNull HariantPlayer victim) {
    }
    
    @Override
    public void onDeath(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player, @Nullable HariantEntity killer) {
    }
    
}