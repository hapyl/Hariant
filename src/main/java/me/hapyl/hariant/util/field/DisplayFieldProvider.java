package me.hapyl.hariant.util.field;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DisplayFieldProvider {
    
    void initDisplayFields(@NotNull List<? super DisplayFieldInstance> displayFields);
    
}
