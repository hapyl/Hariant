package me.hapyl.hariant.reward;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.inventory.adder.Adder;
import me.hapyl.hariant.inventory.item.AbstractItem;
import me.hapyl.hariant.inventory.item.Resource;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class RewardResource implements Reward {
    
    private final Key key;
    private final Component name;
    private final Resource resource;
    private final int amount;
    
    RewardResource(@NotNull Key key, @NotNull Resource resource, int amount) {
        this.key = key;
        this.name = createName(resource, amount);
        this.resource = resource;
        this.amount = amount;
    }
    
    @Override
    public @NotNull Key getKey() {
        return key;
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    public @NotNull Resource getResource() {
        return resource;
    }
    
    public int getAmount() {
        return amount;
    }
    
    @Override
    public @NotNull Adder<? extends AbstractItem, ?> reward(@NotNull PlayerProfile profile) {
        return profile.getDatabase().inventory.adderOfResource(resource, amount);
    }
    
    @Override
    public int priority() {
        return resource.getRarity().ordinal();
    }
    
    private static @NotNull Component createName(@NotNull Resource resource, int amount) {
        return amount == 1 ? resource.getNameStyledWithRarity()
                           : Component.empty()
                                      .append(Component.text(amount, Colors.NUMBER))
                                      .append(SEPARATOR)
                                      .append(resource.getNameStyledWithRarity());
    }
    
}