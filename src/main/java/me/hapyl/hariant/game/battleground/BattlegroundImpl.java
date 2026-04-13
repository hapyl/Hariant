package me.hapyl.hariant.game.battleground;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.inventory.drop.DropTable;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.ImmutableLocation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class BattlegroundImpl implements Battleground {
    
    private final Component name;
    private final Icon icon;
    private final List<ImmutableLocation> spawnLocations;
    
    private DropTable dropTable;
    private Component description;
    private int timeBeforePlayersReveal;
    
    BattlegroundImpl(@NotNull Component name, @NotNull Icon icon) {
        this.name = name;
        this.icon = icon;
        this.spawnLocations = Lists.newArrayList();
        this.dropTable = DropTable.empty();
        this.description = Described.defaultValue();
        this.timeBeforePlayersReveal = 20;
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public Component getDescription() {
        return description;
    }
    
    @Override
    public void setDescription(@NotNull Component description) {
        this.description = description;
    }
    
    @NotNull
    @Override
    public List<ImmutableLocation> getSpawnLocations() {
        return spawnLocations;
    }
    
    @NotNull
    @Override
    public ItemBuilder createBuilder() {
        final ItemBuilder builder = icon.createBuilder();
        builder.setName(name);
        builder.addLore();
        
        // Append description
        builder.addWrappedLore(description);
        builder.addLore();
        
        // Append features
        // TODO @Feb 15, 2026 (xanyjl) ->
        
        // Append drops
        final List<DropTable.Content> dropTableContents = dropTable.getContents();
        
        builder.addLore(Component.text("Possible Drops:", NamedTextColor.GOLD));
        
        dropTableContents.forEach(droppable -> {
            builder.addLore(
                    Component.empty()
                             .append(Component.text("  "))
                             .append(droppable.getAmount().asComponent().color(TextColor.color(0x5fabef)))
                             .append(Component.text(" "))
                             .append(droppable.getName())
                             .append(Component.text("  "))
                             .append(droppable.getDropChanceFormatted().color(NamedTextColor.DARK_GRAY))
            );
        });
        
        return builder;
    }
    
    @NotNull
    @Override
    public DropTable getDropTable() {
        return dropTable;
    }
    
    protected void setDropTable(@NotNull DropTable dropTable) {
        this.dropTable = dropTable;
    }
    
    @Override
    public int getTimeBeforePlayerReveal() {
        return timeBeforePlayersReveal;
    }
    
    protected void setSpawnLocations(@NotNull ImmutableLocation... locations) {
        this.spawnLocations.clear();
        this.spawnLocations.addAll(Arrays.asList(locations));
    }
    
    protected void setTimeBeforePlayersReveal(int timeBeforePlayersReveal) {
        this.timeBeforePlayersReveal = timeBeforePlayersReveal;
    }
}
