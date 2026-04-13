package me.hapyl.hariant.game.battleground;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.Hariant;
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
    public List<ImmutableLocation> getSpawnLocations() {
        return battleground.getSpawnLocations();
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
    public void select() {
        Hariant.setSelectedBattleground(this);
    }
    
    @Override
    public boolean isSelected() {
        return Hariant.getSelectedBattleground() == this;
    }
    
    
}
