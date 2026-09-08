package me.hapyl.hariant.game.battleground;

public enum BattlegroundWeather {
    
    CLEAR(false, false),
    RAINING(true, false),
    THUNDER(true, true);
    
    private final boolean isStorm;
    private final boolean isThunder;
    
    BattlegroundWeather(boolean isStorm, boolean isThunder) {
        this.isStorm = isStorm;
        this.isThunder = isThunder;
    }
    
    public boolean isStorm() {
        return isStorm;
    }
    
    public boolean isThunder() {
        return isThunder;
    }
    
}
