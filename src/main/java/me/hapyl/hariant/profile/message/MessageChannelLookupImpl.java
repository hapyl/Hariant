package me.hapyl.hariant.profile.message;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class MessageChannelLookupImpl implements MessageChannelLookup {
    
    private final Pattern lookupPattern;
    
    MessageChannelLookupImpl(@NotNull Pattern lookupPattern) {
        this.lookupPattern = lookupPattern;
    }
    
    @Override
    public @NotNull Pattern lookupPattern() {
        return lookupPattern;
    }
    
}