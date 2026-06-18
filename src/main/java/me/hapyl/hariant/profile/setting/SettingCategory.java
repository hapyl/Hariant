package me.hapyl.hariant.profile.setting;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.SlotBound;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public enum SettingCategory implements Named, Described, Icon, SlotBound {
    
    GAMEPLAY(
            2,
            Component.text("Gameplay"),
            Component.text("Gameplay related settings."),
            Icon.ofMaterial(Material.PRIZE_POTTERY_SHERD)
    ),
    
    QUALITY_OF_LIFE(
            4,
            Component.text("Quality of Life"),
            Component.text("These settings make life easier."),
            Icon.ofMaterial(Material.ARMS_UP_POTTERY_SHERD)
    ),
    
    CHAT(
            6,
            Component.text("Chat"),
            Component.text("Customize the chatting experience to your liking."),
            Icon.ofMaterial(Material.FLOW_POTTERY_SHERD)
    ),
    
    
    ;
    
    private final int slot;
    private final Component name;
    private final Component description;
    private final Icon icon;
    
    SettingCategory(int slot, @NotNull Component name, @NotNull Component description, @NotNull Icon icon) {
        this.slot = slot;
        this.name = name;
        this.description = description;
        this.icon = icon;
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
    
    @Override
    public @NotNull Component getDescription() {
        return description;
    }
    
    @Override
    public @NotNull ItemBuilder createBuilder() {
        final ItemBuilder builder = icon.createBuilder();
        
        builder.setName(name);
        builder.addLore();
        builder.addWrappedLore(description);
        
        return builder;
    }
    
}