package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface TabCompleter {
    
    @NotNull
    List<String> tabComplete(@NotNull ArgumentList args);
    
}
