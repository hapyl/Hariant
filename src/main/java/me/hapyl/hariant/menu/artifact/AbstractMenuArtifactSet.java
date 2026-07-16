package me.hapyl.hariant.menu.artifact;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.PlayerMenuTitle;
import me.hapyl.hariant.inventory.item.ItemRegistry;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifact;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSet;
import me.hapyl.hariant.menu.MenuPage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractMenuArtifactSet extends MenuPage<ItemArtifact> {
    
    public AbstractMenuArtifactSet(@NotNull Player player, @NotNull PlayerMenuTitle title) {
        super(player, title);
        
        this.setContents(ItemRegistry.streamOfType(ItemArtifact.class).toList());
    }
    
    @Override
    public @NotNull ItemBuilder createBuilder(@NotNull ItemArtifact itemArtifact) {
        final ItemBuilder builder = itemArtifact.getIcon().createBuilder();
        
        final ArtifactSet artifactSet = itemArtifact.getArtifactSet();
        
        builder.setName(artifactSet.getName());
        builder.addLore();
        
        artifactSet.supplyLore(builder, ArtifactSet.ArtifactSetDescription.EMPTY);
        
        return builder;
    }
    
    @Override
    public abstract void onClick(@NotNull ItemArtifact itemArtifact, @NotNull ClickType clickType);
    
}
