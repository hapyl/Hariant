package me.hapyl.hariant.security;

import me.hapyl.hariant.util.HexId;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Punishment {
    
    @NotNull
    UUID uuid();
    
    @NotNull
    HexId id();
    
    @NotNull
    PunishmentType type();
    
    @NotNull
    Document asDocument();
    
}
