package me.hapyl.hariant.game.battleground;

import me.hapyl.hariant.inventory.drop.DropTable;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.ImmutableLocation;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

public final class BattlegroundSpawn extends BattlegroundImpl {
    BattlegroundSpawn() {
        super(
                Component.text("Spawn"),
                Component.text("That's where you spawn!"),
                DropTable.empty(),
                Icon.ofMaterial(Material.RED_BED)
        );
        
        this.setTimeBeforePlayersReveal(1);
        this.setSpawnLocations(ImmutableLocation.create(0, 64, 0));
    }
}
