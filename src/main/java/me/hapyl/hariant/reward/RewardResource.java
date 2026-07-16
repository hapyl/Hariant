package me.hapyl.hariant.reward;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.inventory.HariantInventory;
import me.hapyl.hariant.inventory.item.Resource;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class RewardResource implements Reward {
    
    private final Key key;
    private final Component name;
    
    private final Map<Resource, Integer> resources;
    
    public RewardResource(@NotNull Key key, @NotNull Component name) {
        this.key = key;
        this.name = name;
        this.resources = Maps.newHashMap();
    }
    
    @NotNull
    public RewardResource set(@NotNull Resource resource, int amount) {
        this.resources.put(resource, amount);
        return this;
    }
    
    public long get(@NotNull Resource resource) {
        return this.resources.getOrDefault(resource, 0);
    }
    
    @NotNull
    @Override
    public Key getKey() {
        return key;
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @Override
    public void reward(@NotNull PlayerProfile profile) {
        final HariantInventory inventory = profile.getDatabase().inventory;
        
        resources.forEach(inventory::addResource);
    }
    
}
