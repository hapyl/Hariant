package me.hapyl.hariant.game.battleground;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.game.battleground.clouds.BattlegroundClouds;
import me.hapyl.hariant.game.battleground.feature.BattlegroundFeature;
import me.hapyl.hariant.game.battleground.japan.BattlegroundJapan;
import me.hapyl.hariant.inventory.drop.DropTable;
import me.hapyl.hariant.util.ImmutableLocation;
import me.hapyl.hariant.util.Selectable;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

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
    
    ;
    
    private final Battleground battleground;
    
    EnumBattleground(@NotNull Battleground battleground) {
        this.battleground = battleground;
    }
    
    
    @Override
    @NotNull
    public Component getName() {
        return battleground.getName();
    }
    
    @NotNull
    @Override
    public Component getDescription() {
        return battleground.getDescription();
    }
    
    @NotNull
    @Override
    public List<? extends ImmutableLocation> getSpawnLocations() {
        return battleground.getSpawnLocations();
    }
    
    @Override
    public @NotNull List<? extends BattlegroundFeature> getFeatures() {
        return battleground.getFeatures();
    }
    
    @NotNull
    @Override
    public ItemBuilder createBuilder() {
        return battleground.createBuilder();
    }
    
    @NotNull
    @Override
    public DropTable getDropTable() {
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
    public void select() {
        Hariant.setSelectedBattleground(this);
    }
    
    @Override
    public boolean isSelected() {
        return Hariant.getSelectedBattleground() == this;
    }
    
}