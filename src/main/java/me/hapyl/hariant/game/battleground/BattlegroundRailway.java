package me.hapyl.hariant.game.battleground;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.ImmutableLocation;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

public final class BattlegroundRailway extends BattlegroundImpl {
    BattlegroundRailway() {
        super(Component.text("Railway"), Icon.ofMaterial(Material.RAIL));
        
        this.setTimeBeforePlayersReveal(Tick.fromSeconds(10));
        
        this.setSpawnLocations(
                ImmutableLocation.create(3000, 64, 0, -90, 0),
                ImmutableLocation.create(2982, 76, 0, -90, 0),
                ImmutableLocation.create(3037, 63, 0, 90, 0),
                ImmutableLocation.create(3010, 72, 17, 180, 0),
                ImmutableLocation.create(3010, 72, 17, 180, 0),
                ImmutableLocation.create(3010, 72, -17, 0, 0)
        );
    }
}
