package me.hapyl.hariant.game.battleground;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.game.GameInstance;
import me.hapyl.hariant.game.GameInstanceListener;
import me.hapyl.hariant.game.WinResult;
import me.hapyl.hariant.game.battleground.feature.BattlegroundFeature;
import me.hapyl.hariant.inventory.drop.Amount;
import me.hapyl.hariant.inventory.drop.DropTable;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.ImmutableLocation;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Battleground extends Icon, Named, Described, Ticking, GameInstanceListener {
    
    @NotNull Amount DEFAULT_DROP_TABLE_AMOUNT = Amount.range(1, 3);
    
    @Override
    @NotNull Component getName();
    
    @Override
    @NotNull Component getDescription();
    
    @NotNull Size getSize();
    
    @NotNull List<? extends ImmutableLocation> getSpawnLocations();
    
    @NotNull List<? extends BattlegroundFeature> getFeatures();
    
    @NotNull
    default Location getRandomSpawnLocation() {
        return CollectionUtils.randomElementOrFirst(this.getSpawnLocations()).getCenteredLocation();
    }
    
    @Override
    @NotNull ItemBuilder createBuilder();
    
    @NotNull DropTable getDropTable();
    
    int getTimeBeforePlayerReveal();
    
    @Override
    void tick();
    
    @Override
    void onCreate(@NotNull Iterable<? extends HariantPlayer> players);
    
    @Override
    void onDestroy(@NotNull Iterable<? extends HariantPlayer> players, @NotNull WinResult result);
    
    @Override
    void onFinalize(@NotNull List<? extends HariantPlayer> players, @NotNull WinResult result);
    
    @Override
    
    void onKill(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player, @NotNull HariantPlayer victim);
    
    @Override
    void onDeath(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player, @Nullable HariantEntity source);
    
    @NotNull BattlegroundWeather getWeather();
    
    @NotNull BattlegroundTime getTime();
    
}