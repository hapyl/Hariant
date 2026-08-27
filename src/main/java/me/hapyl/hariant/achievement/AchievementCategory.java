package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.inventory.item.ItemCreator;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public enum AchievementCategory implements Named, Described, ItemCreator {
    
    GENESIS(
            Component.text("Genesis"),
            Component.text("Where it all began."),
            Icon.ofMaterial(Material.PRIZE_POTTERY_SHERD)
    ),
    
    COMBAT(
            Component.text("Combat"),
            Component.text("Conflict means war, but also rewards."),
            Icon.ofMaterial(Material.BLADE_POTTERY_SHERD)
    ),
    
    ELEMENTS_OF_THE_WORLD(
            Component.text("Elementals"),
            Component.text("The flow of the elements."),
            Icon.ofMaterial(Material.FLOW_POTTERY_SHERD)
    ),
    
    HERO_PATH(
            Component.text("Path of a Hero"),
            Component.text("One must overcome the weaknesses to be called a Hero."),
            Icon.ofMaterial(Material.ARMS_UP_POTTERY_SHERD)
    );
    
    private final Component name;
    private final Component description;
    private final Icon icon;
    
    AchievementCategory( @NotNull Component name, @NotNull Component description, @NotNull Icon icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
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
    public @NotNull ItemBuilder createBuilder() {
        return icon.createBuilder();
    }
    
}