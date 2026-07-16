package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.inventory.item.ItemCreator;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.SlotBound;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public enum AchievementCategory implements SlotBound, Named, Described, ItemCreator {
    
    GAMEPLAY(
            2,
            Component.text("Gameplay"),
            Component.text("Achievements related to the game gameplay of Hariant."),
            Icon.ofMaterial(Material.BLADE_POTTERY_SHERD)
    ),
    
    HERO_RELATED(
            4,
            Component.text("Hero's Path"),
            Component.text("Hero-specific achievements."),
            Icon.ofMaterial(Material.ARMS_UP_POTTERY_SHERD)
    ),
    
    MISCELLANEOUS(
            6,
            Component.text("Miscellaneous"),
            Component.text("Miscellaneous achievements."),
            Icon.ofMaterial(Material.PRIZE_POTTERY_SHERD)
    );
    
    private final int slot;
    private final Component name;
    private final Component description;
    private final Icon icon;
    
    AchievementCategory(int slot, @NotNull Component name, @NotNull Component description, @NotNull Icon icon) {
        this.slot = slot;
        this.name = name;
        this.description = description;
        this.icon = icon;
    }
    
    @Override
    public int getSlot() {
        return slot;
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
