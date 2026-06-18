package me.hapyl.hariant.game.battleground;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.game.battleground.feature.BattlegroundFeature;
import me.hapyl.hariant.inventory.drop.Amount;
import me.hapyl.hariant.inventory.drop.DropTable;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.ImmutableLocation;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Battleground extends Icon, Named, Described, Ticking {
    
    @NotNull Amount DEFAULT_DROP_TABLE_AMOUNT = Amount.range(1, 3);
    
    @Override
    @NotNull Component getName();
    
    @Override
    @NotNull Component getDescription();
    
    @NotNull List<? extends ImmutableLocation> getSpawnLocations();
    
    @NotNull List<? extends BattlegroundFeature> getFeatures();
    
    @NotNull
    default Location getRandomSpawnLocation() {
        return CollectionUtils.randomElementOrFirst(this.getSpawnLocations()).getCenteredLocation();
    }
    
    @Override
    @NotNull
    ItemBuilder createBuilder();
    
    @NotNull
    DropTable getDropTable();
    
    int getTimeBeforePlayerReveal();
    
    @Override
    void tick();
}
