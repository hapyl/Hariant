package me.hapyl.hariant.lobby;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.builder.ItemFunction;
import me.hapyl.eterna.module.registry.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class LobbyItemImpl implements LobbyItem {
    
    private final int slot;
    private final Key key;
    private final Material material;
    private final Component name;
    private final Component description;
    
    protected int cooldown;
    
    LobbyItemImpl(int slot, @NotNull Key key, @NotNull Material material, @NotNull Component name, @NotNull Component description) {
        this.slot = slot;
        this.key = key;
        this.material = material;
        this.name = name;
        this.description = description;
        this.cooldown = 5;
    }
    
    @Override
    public int getSlot() {
        return slot;
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
        builder.addFunction(
                ItemFunction.builder(this::use)
                            .cooldown(cooldown)
                            .build()
        );
        
        return builder;
    }
    
    @Override
    public abstract void use(@NotNull Player player);
    
    @Override
    public void give(@NotNull Player player) {
        player.getInventory().setItem(slot, createBuilder(player).build());
    }
    
}