package me.hapyl.hariant.lobby;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Hariant;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class LobbyItemImpl implements LobbyItem {
    
    private final Key key;
    private final Material material;
    private final Component name;
    private final Component description;
    
    LobbyItemImpl(@NotNull Key key, @NotNull Material material, @NotNull Component name, @NotNull Component description) {
        this.key = key;
        this.material = material;
        this.name = name;
        this.description = description;
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
    
    @NotNull
    @Override
    public ItemBuilder createBuilder(@NotNull Player player) {
        final ItemBuilder builder = new ItemBuilder(material, key);
        builder.setName(name);
        builder.addLore();
        
        builder.addWrappedLore(description);
        builder.addClickAction(this::use);
        
        return builder;
    }
    
    @Override
    public abstract void use(@NotNull Player player);
    
}
