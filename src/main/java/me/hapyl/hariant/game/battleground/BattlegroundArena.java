package me.hapyl.hariant.game.battleground;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.hariant.inventory.drop.Amount;
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
        super(Component.text("Arena"), Icon.ofMaterial(Material.COARSE_DIRT));
        
        this.setDropTable(new DropTableArena());
        this.setTimeBeforePlayersReveal(Tick.fromSeconds(5));
        
        this.setSpawnLocations(
                ImmutableLocation.create(500, 64, 0)
        );
    }
    
    private static class DropTableArena extends DropTable {
        DropTableArena() {
            super(
                    List.of(
                            Droppable.ofCatCoins(),
                            Droppable.ofArtifact(ItemRegistry.ARTIFACT_UNSTABLE_LIGHTNING_GEM, 1),
                            Droppable.ofArtifact(ItemRegistry.ARTIFACT_BLOODY_ROSE, 1)
                    ),
                    Amount.range(1, 3)
            );
        }
    }
}
