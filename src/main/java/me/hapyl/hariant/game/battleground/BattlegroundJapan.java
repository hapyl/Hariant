package me.hapyl.hariant.game.battleground;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.ImmutableLocation;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

public class BattlegroundJapan extends BattlegroundImpl {
    BattlegroundJapan() {
        super(Component.text("Japan"), Icon.ofMaterial(Material.CHERRY_SAPLING));
        
        this.setTimeBeforePlayersReveal(Tick.fromSeconds(10));
        
        this.setSpawnLocations(
                ImmutableLocation.create(1000, 64, 0, 180, 0),
                ImmutableLocation.create(1046, 80, -16, 180, 0),
                ImmutableLocation.create(1000, 85, -37, 180, 0),
                ImmutableLocation.create(937, 65, -11, -90, 0),
                ImmutableLocation.create(3041, 72, 0, -90, 0)
        );
    }
}
