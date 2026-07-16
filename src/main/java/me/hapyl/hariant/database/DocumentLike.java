package me.hapyl.hariant.database;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

public interface DocumentLike {
    
    @NotNull Document asDocument();
    
}
