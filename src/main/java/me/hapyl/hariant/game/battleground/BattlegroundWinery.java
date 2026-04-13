package me.hapyl.hariant.game.battleground;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.ImmutableLocation;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

public class BattlegroundWinery extends BattlegroundImpl {
    BattlegroundWinery() {
        super(Component.text("Winery `Drunk Cat`"), Icon.ofMaterial(Material.SWEET_BERRIES));
        
        this.setTimeBeforePlayersReveal(Tick.fromSeconds(10));
        
        this.setSpawnLocations(
                ImmutableLocation.create(4976, 68, -1, -45, 0),
                ImmutableLocation.create(4979, 65, 31, -145, 0),
                ImmutableLocation.create(5008, 71, 23, -90, 0),
                ImmutableLocation.create(4997, 66, -27, 0, 0),
                ImmutableLocation.create(5025, 81, 23, 0, 0),
                ImmutableLocation.create(5024, 61, 16, 90, 0)
        );
    }
}
