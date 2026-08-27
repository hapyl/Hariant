package me.hapyl.hariant.achievement;

import org.jetbrains.annotations.Range;

public interface UniqueId {
    
    @Range(from = 0, to = Integer.MAX_VALUE) int getUniqueId();
    
}
