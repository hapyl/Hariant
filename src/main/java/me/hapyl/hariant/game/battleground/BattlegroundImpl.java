package me.hapyl.hariant.game.battleground;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.annotate.AutoRegisteredListener;
import me.hapyl.hariant.game.battleground.feature.BattlegroundFeature;
import me.hapyl.hariant.inventory.drop.Amount;
import me.hapyl.hariant.inventory.drop.DropTable;
import me.hapyl.hariant.inventory.drop.DropTier;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.ImmutableLocation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@AutoRegisteredListener
public class BattlegroundImpl implements Battleground {
    
    private final Component name;
    private final Component description;
    private final DropTable dropTable;
    
    private final Icon icon;
    private final List<ImmutableLocation> spawnLocations;
    private final List<BattlegroundFeature> features;
    
    private int timeBeforePlayersReveal;
    private Size size;
    private @NotNull List<BoundingBox> supplyBoundingBoxes;
    
    public BattlegroundImpl(@NotNull Component name, @NotNull Component description, @NotNull DropTable dropTable, @NotNull Icon icon) {
        this.name = name;
        this.description = description;
        this.dropTable = dropTable;
        this.icon = icon;
        this.spawnLocations = Lists.newArrayList();
        this.timeBeforePlayersReveal = 100;
        this.features = Lists.newArrayList();
        this.size = Size.MEDIUM;
        this.supplyBoundingBoxes = List.of();
        
        AutoRegisteredListener.Registry.register(this);
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public @NotNull Component getDescription() {
        return description;
    }
    
    @Override
    public @NotNull Size getSize() {
        return size;
    }
    
    protected void setSize(@NotNull Size size) {
        this.size = size;
    }
    
    @Override
    public @NotNull List<? extends ImmutableLocation> getSpawnLocations() {
        return spawnLocations;
    }
    
    @Override
    public @NotNull List<? extends BattlegroundFeature> getFeatures() {
        return features;
    }
    
    @Override
    public @NotNull ItemBuilder createBuilder() {
        final ItemBuilder builder = icon.createBuilder();
        builder.setName(name);
        builder.addLore(size.getName().color(Colors.DARK_GRAY));
        builder.addLore();
        
        // Append description
        builder.addWrappedLore(description, HariantConstants.COMPONENT_STYLER_DESCRIPTION);
        builder.addLore();
        
        // Append features
        if (!features.isEmpty()) {
            builder.addLore(Component.text("ꜰᴇᴀᴛᴜʀᴇꜱ", Colors.DEFAULT_COLOR, TextDecoration.BOLD));
            
            features.forEach(feature -> {
                builder.addLore(Component.space().append(feature.getName().color(Colors.SUCCESS)));
                builder.addWrappedLore(feature.getDescription(), HariantConstants.COMPONENT_STYLER_DESCRIPTION_PADDING_2);
                builder.addLore();
            });
        }
        
        // Append drops
        final Map<DropTier, List<DropTable.Content>> dropTableContents = dropTable.getContentsTiered();
        
        builder.addLore(Component.text("ᴘᴏꜱꜱɪʙʟᴇ ᴅʀᴏᴘꜱ", Colors.DEFAULT_COLOR, TextDecoration.BOLD));
        
        dropTableContents.forEach((dropTier, contents) -> {
            builder.addLore(
                    Component.empty()
                             .appendSpace()
                             .append(dropTier.asComponent().decorate(TextDecoration.BOLD))
                             .append(Component.text(" (%.0f%%)".formatted(dropTier.threshold() * 100), Colors.DARK_GRAY))
            );
            
            contents.forEach(content -> {
                final Amount amount = content.getAmount();
                
                builder.addLore(
                        Component.empty()
                                 .append(Component.text("  ● ", Colors.DARK_GRAY))
                                 .append(content.getName())
                                 .appendSpace()
                                 .append(amount.asComponent().color(Colors.GRAY))
                );
            });
            
            builder.addLore();
        });
        
        // Append disclaimer
        builder.addWrappedLore(
                Component.empty()
                         .append(Component.text("At least ", Colors.DARK_GRAY))
                         .append(dropTable.getRolls().asComponent().color(Colors.DARK_GRAY))
                         .append(Component.text(" non-guaranteed items are guaranteed to drop!", Colors.DARK_GRAY))
        );
        
        return builder;
    }
    
    @Override
    public @NotNull DropTable getDropTable() {
        return dropTable;
    }
    
    @Override
    public int getTimeBeforePlayerReveal() {
        return timeBeforePlayersReveal;
    }
    
    @Override
    public void tick() {
        // Tick features
        this.features.forEach(BattlegroundFeature::tick);
    }
    
    protected void setFeatures(@NotNull BattlegroundFeature... features) {
        this.features.clear();
        this.features.addAll(Arrays.asList(features));
    }
    
    protected void setSpawnLocations(@NotNull ImmutableLocation... locations) {
        this.spawnLocations.clear();
        this.spawnLocations.addAll(Arrays.asList(locations));
    }
    
    public @NotNull List<? extends BoundingBox> getSupplyBoundingBoxes() {
        return supplyBoundingBoxes;
    }
    
    protected void setSupplyBoxBoundingBox(@NotNull BoundingBox... boundingBoxes) {
        this.supplyBoundingBoxes = List.of(boundingBoxes);
    }
    
    protected void setTimeBeforePlayersReveal(int timeBeforePlayersReveal) {
        this.timeBeforePlayersReveal = timeBeforePlayersReveal;
    }
    
}