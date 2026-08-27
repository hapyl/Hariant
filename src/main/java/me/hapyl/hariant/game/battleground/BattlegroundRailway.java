package me.hapyl.hariant.game.battleground;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.hariant.inventory.drop.CommonDroppable;
import me.hapyl.hariant.inventory.drop.DropTable;
import me.hapyl.hariant.inventory.drop.Droppable;
import me.hapyl.hariant.inventory.item.ItemRegistry;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.ImmutableLocation;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.List;

public final class BattlegroundRailway extends BattlegroundImpl {
    BattlegroundRailway() {
        super(
                Component.text("Railway"),
                Component.text("A railway station untouched by arriving or departing trains."),
                new DropTableRailway(),
                Icon.ofMaterial(Material.RAIL)
        );
        
        this.setTimeBeforePlayersReveal(Tick.fromSeconds(8));
        
        this.setSpawnLocations(
                ImmutableLocation.create(3000, 64, 0, -90, 0),
                ImmutableLocation.create(2982, 76, 0, -90, 0),
                ImmutableLocation.create(3037, 63, 0, 90, 0),
                ImmutableLocation.create(3010, 72, 17, 180, 0),
                ImmutableLocation.create(3010, 72, 17, 180, 0),
                ImmutableLocation.create(3010, 72, -17, 0, 0)
        );
        
        setSize(Size.LARGE);
    }
    
    private static class DropTableRailway extends DropTable {
        DropTableRailway() {
            super(
                    List.of(
                            CommonDroppable.CAT_COINS,
                            CommonDroppable.ARTIFACT_ARTIFICER,
                            CommonDroppable.HERO_RECRUIT_VOUCHER,
                            Droppable.ofItem(ItemRegistry.ARTIFACT_ANTIQUE_POCKET_WATCH, 50),
                            Droppable.ofItem(ItemRegistry.ARTIFACT_LIGHTNING_IN_A_BOTTLE, 50),
                            Droppable.ofItem(ItemRegistry.ARTIFACT_CUIRASS, 50)
                    ),
                    DEFAULT_DROP_TABLE_AMOUNT
            );
        }
    }
    
}
