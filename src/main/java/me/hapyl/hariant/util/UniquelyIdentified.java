package me.hapyl.hariant.util;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface UniquelyIdentified {
    
    @NotNull
    UUID getUuid();
    
}
