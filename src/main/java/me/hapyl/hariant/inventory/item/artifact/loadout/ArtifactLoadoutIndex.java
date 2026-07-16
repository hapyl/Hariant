package me.hapyl.hariant.inventory.item.artifact.loadout;

import me.hapyl.eterna.module.text.Capitalizable;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.util.SlotBound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public enum ArtifactLoadoutIndex implements SlotBound, ComponentLike {
    
    INDEX_1(11),
    INDEX_2(20),
    INDEX_3(29);
    
    private final int slot;
    private final Component component;
    private final String defaultName;
    
    ArtifactLoadoutIndex(int slot) {
        this.slot = slot;
        this.component = Component.text(Capitalizable.capitalize(this), Colors.DARK_GRAY);
        this.defaultName = "Loadout #" + (ordinal() + 1);
    }
    
    @Override
    public int getSlot() {
        return slot;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return component;
    }
    
    public @NotNull String getDefaultName() {
        return defaultName;
    }
    
}
