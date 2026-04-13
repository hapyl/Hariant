package me.hapyl.hariant.util;

public interface Selectable {
    
    void select();
    
    boolean isSelected();
    
    default boolean isSelectable() {
        return true;
    }
    
}
