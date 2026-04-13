package me.hapyl.hariant.talent;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.hariant.util.SlotBound;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum TalentIndex implements SlotBound, Named {
    
    TALENT_1(0, 20, Component.text("First Talent")),
    TALENT_2(1, 22, Component.text("Second Talent")),
    TALENT_3(2, 24, Component.text("Third Talent")),
    TALENT_PASSIVE(-1, 30, Component.text("Passive Talent")),
    TALENT_ULTIMATE(-1, 32, Component.text("Ultimate Talent"));
    
    private static final List<TalentIndex> ACTIVE_TALENTS = List.of(TalentIndex.TALENT_1, TalentIndex.TALENT_2, TalentIndex.TALENT_3);
    
    private final int slotInventory;
    private final int slotMenu;
    private final Component name;
    
    TalentIndex(final int slotInventory, final int slotMenu, @NotNull Component name) {
        this.slotInventory = slotInventory;
        this.slotMenu = slotMenu;
        this.name = name;
    }
    
    @Override
    public int getSlot() {
        return slotInventory;
    }
    
    public int getSlotMenu() {
        return slotMenu;
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    public static List<TalentIndex> ofActive() {
        return ACTIVE_TALENTS;
    }
    
}
