package me.hapyl.hariant.database.rank;

public final class FormatRulesImpl implements FormatRules {
    
    private final boolean playerHead;
    private final boolean level;
    private final boolean prefix;
    private final boolean playerName;
    private final boolean suffix;
    
    FormatRulesImpl(boolean playerHead, boolean level, boolean prefix, boolean playerName, boolean suffix) {
        this.playerHead = playerHead;
        this.level = level;
        this.prefix = prefix;
        this.playerName = playerName;
        this.suffix = suffix;
    }
    
    @Override
    public boolean playerHead() {
        return playerHead;
    }
    
    @Override
    public boolean level() {
        return level;
    }
    
    @Override
    public boolean prefix() {
        return prefix;
    }
    
    @Override
    public boolean playerName() {
        return playerName;
    }
    
    @Override
    public boolean suffix() {
        return suffix;
    }
    
}