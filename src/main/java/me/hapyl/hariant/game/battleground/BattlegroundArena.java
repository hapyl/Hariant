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

public final class BattlegroundArena extends BattlegroundImpl {
    
    BattlegroundArena() {
        super(
                Component.text("Arena"),
                Component.text("A grand arena built as a memorial to the warriors who fell in the Great War."),
                new DropTableArena(),
                Icon.ofMaterial(Material.COARSE_DIRT)
        );
        
        setTimeBeforePlayersReveal(Tick.fromSeconds(5));
        
        setSpawnLocations(
                ImmutableLocation.create(500, 64, 0),
                ImmutableLocation.create(479.0, 78.0, 24.0, 180, 0),
                ImmutableLocation.create(500.0, 66.0, -24.0),
                ImmutableLocation.create(526.0, 66.0, 16.0, 135, 0)
        );
        
    }
    
    private static class DropTableArena extends DropTable {
        DropTableArena() {
            super(
                    List.of(
                            CommonDroppable.CAT_COINS,
                            CommonDroppable.ARTIFACT_ARTIFICER,
                            CommonDroppable.HERO_RECRUIT_VOUCHER,
                            Droppable.ofItem(ItemRegistry.ARTIFACT_UNSTABLE_LIGHTNING_GEM, 50),
                            Droppable.ofItem(ItemRegistry.ARTIFACT_BLOODY_ROSE, 50),
                            Droppable.ofItem(ItemRegistry.ARTIFACT_PHILOSOPHERS_STONE, 50)
                    ),
                    DEFAULT_DROP_TABLE_AMOUNT
            );
        }
    }
    
}