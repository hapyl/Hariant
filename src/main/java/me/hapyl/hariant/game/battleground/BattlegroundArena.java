package me.hapyl.hariant.game.battleground;

import me.hapyl.eterna.module.math.Tick;
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
        
        setDescription(
                Component.empty()
                        .append(Component.text("A great arena built as a memorial to the great warriors who fell in the Great War."))
        );
    }
    
    private static class DropTableArena extends DropTable {
        DropTableArena() {
            super(
                    List.of(
                            Droppable.ofCatCoins(),
                            Droppable.ofItem(ItemRegistry.ARTIFACT_UNSTABLE_LIGHTNING_GEM, 50),
                            Droppable.ofItem(ItemRegistry.ARTIFACT_BLOODY_ROSE, 50),
                            Droppable.ofItem(ItemRegistry.ARTIFACT_PHILOSOPHERS_STONE, 50),
                            Droppable.ofHeroRecruitVoucher()
                    ),
                    Battleground.DEFAULT_DROP_TABLE_AMOUNT
            );
        }
    }
    
}