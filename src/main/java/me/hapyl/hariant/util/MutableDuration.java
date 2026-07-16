package me.hapyl.hariant.util;

import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.Nullable;

// TODO (xanyjl @ Sunday, July 12) -> Idk the best way to impl this
public interface MutableDuration {
    
    int calculateDuration(@Nullable HariantEntity source);
    
    default int calculateDuration() {
        return this.calculateDuration(null);
    }
    
}
