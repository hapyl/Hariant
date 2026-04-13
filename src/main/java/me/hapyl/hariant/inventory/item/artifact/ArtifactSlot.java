package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.text.Capitalizable;

public enum ArtifactSlot {
    
    SLOT_1(19),
    SLOT_2(30),
    SLOT_3(32),
    SLOT_4(25);
    
    private final String toString;
    private final int inventorySlot;
    
    ArtifactSlot(final int inventorySlot) {
        this.toString = Capitalizable.capitalize(this);
        this.inventorySlot = inventorySlot;
    }
    
    public int getInventorySlot() {
        return inventorySlot;
    }
    
    @Override
    public String toString() {
        return toString;
    }
}
