package me.hapyl.hariant.game.battleground;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.game.GameInstance;
import me.hapyl.hariant.game.WinResult;
import me.hapyl.hariant.game.battleground.clouds.BattlegroundClouds;
import me.hapyl.hariant.game.battleground.feature.BattlegroundFeature;
import me.hapyl.hariant.game.battleground.japan.BattlegroundJapan;
import me.hapyl.hariant.inventory.drop.DropTable;
import me.hapyl.hariant.util.ImmutableLocation;
import me.hapyl.hariant.util.Selectable;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public enum EnumBattleground implements Battleground, Selectable {
    
    SPAWN(new BattlegroundSpawn()) {
        @Override
        public boolean isSelectable() {
            return false;
        }
    },
    
    ARENA(new BattlegroundArena()),
    JAPAN(new BattlegroundJapan()),
    RAILWAY(new BattlegroundRailway()),
    WINERY(new BattlegroundWinery()),
    CLOUDS(new BattlegroundClouds()),
    LIBRARY(new BattlegroundLibrary()),
    THE_VAULT(new BattlegroundTheVault()),
    
    ;
    
    public final Battleground battleground;
    
    EnumBattleground(@NotNull Battleground battleground) {
        this.battleground = battleground;
    }
    
    @Override
    public @NotNull Component getName() {
        return battleground.getName();
    }
    
    @Override
    public @NotNull Component getDescription() {
        return battleground.getDescription();
    }
    
    @Override
    public @NotNull Size getSize() {
        return battleground.getSize();
    }
    
    @Override
    public @NotNull List<? extends ImmutableLocation> getSpawnLocations() {
        return battleground.getSpawnLocations();
    }
    
    @Override
    public @NotNull List<? extends BattlegroundFeature> getFeatures() {
        return battleground.getFeatures();
    }
    
    @Override
    public @NotNull ItemBuilder createBuilder() {
        return battleground.createBuilder();
    }
    
    @Override
    public @NotNull DropTable getDropTable() {
        return battleground.getDropTable();
    }
    
    @Override
    public int getTimeBeforePlayerReveal() {
        return battleground.getTimeBeforePlayerReveal();
    }
    
    @Override
    public void tick() {
        battleground.tick();
    }
    
    @Override
    public void onCreate(@NotNull Iterable<? extends HariantPlayer> players) {
        battleground.onCreate(players);
    }
    
    @Override
    public void onDestroy(@NotNull Iterable<? extends HariantPlayer> players, @NotNull WinResult result) {
        battleground.onDestroy(players, result);
    }
    
    @Override
    public void onFinalize(@NotNull List<? extends HariantPlayer> players, @NotNull WinResult result) {
        battleground.onFinalize(players, result);
    }
    
    @Override
    public void onKill(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player, @NotNull HariantPlayer victim) {
        battleground.onKill(gameInstance, player, victim);
    }
    
    @Override
    public void onDeath(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player, @Nullable HariantEntity source) {
        battleground.onDeath(gameInstance, player, source);
    }
    
    @Override
    public void select() {
        Hariant.setSelectedBattleground(this);
    }
    
    @Override
    public boolean isSelected() {
        return Hariant.getSelectedBattleground() == this;
    }
    
}