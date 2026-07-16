package me.hapyl.hariant.database.rank;

import org.jetbrains.annotations.NotNull;

public interface FormatRules {
    
    boolean playerHead();
    
    boolean level();
    
    boolean prefix();
    
    boolean playerName();
    
    boolean suffix();
    
    static @NotNull FormatRules create(boolean playerHead, boolean level, boolean prefix, boolean playerName, boolean suffix) {
        return new FormatRulesImpl(playerHead, level, prefix, playerName, suffix);
    }
    
}