package me.hapyl.hariant.profile.message;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public interface MessageChannelLookup {
    
    @NotNull
    Pattern lookupPattern();
    
    static @NotNull MessageChannelLookup create(@NotNull Pattern pattern) {
        return new MessageChannelLookupImpl(pattern);
    }
    
}
