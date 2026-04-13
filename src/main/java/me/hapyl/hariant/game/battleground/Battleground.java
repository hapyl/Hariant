package me.hapyl.hariant.game.battleground;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.hariant.inventory.drop.DropTable;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.ImmutableLocation;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Battleground extends Icon, Named, Described {
    
    @Override
    @NotNull
    Component getName();
    
    @NotNull
    @Override
    Component getDescription();
    
    @NotNull
    List<ImmutableLocation> getSpawnLocations();
    
    @NotNull
    default Location getRandomSpawnLocation() {
        return CollectionUtils.randomElementOrFirst(this.getSpawnLocations()).getLocation();
    }
    
    @Override
    @NotNull
    ItemBuilder createBuilder();
    
    @NotNull
    DropTable getDropTable();
    
    int getTimeBeforePlayerReveal();
    
}
