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

public final class BattlegroundWinery extends BattlegroundImpl {
    BattlegroundWinery() {
        super(
                Component.text("Winery `Drunk Cat`"),
                Component.text("A winery that belongs to a certain drunk cat... literally!"),
                new DropTableWinery(),
                Icon.ofMaterial(Material.SWEET_BERRIES)
        );
        
        this.setTimeBeforePlayersReveal(Tick.fromSeconds(10));
        
        setSize(Size.LARGE);
        
        this.setSpawnLocations(
                ImmutableLocation.create(4976, 68, -1, -45, 0),
                ImmutableLocation.create(4979, 65, 31, -145, 0),
                ImmutableLocation.create(5008, 71, 23, -90, 0),
                ImmutableLocation.create(4997, 66, -27, 0, 0),
                ImmutableLocation.create(5025, 81, 23, 0, 0),
                ImmutableLocation.create(5024, 61, 16, 90, 0)
        );
    }
    
    private static class DropTableWinery extends DropTable {
        DropTableWinery() {
            super(
                    List.of(
                            CommonDroppable.CAT_COINS,
                            CommonDroppable.HERO_RECRUIT_VOUCHER,
                            CommonDroppable.ARTIFACT_ARTIFICER,
                            Droppable.ofItem(ItemRegistry.ARTIFACT_CANNONBALL, 50),
                            Droppable.ofItem(ItemRegistry.ARTIFACT_GREAT_WHITE_SHARK_TOOTH, 50),
                            Droppable.ofItem(ItemRegistry.ARTIFACT_INFERNAL_CRUCIBLE, 50)
                    ),
                    DEFAULT_DROP_TABLE_AMOUNT
            );
        }
    }
    
}