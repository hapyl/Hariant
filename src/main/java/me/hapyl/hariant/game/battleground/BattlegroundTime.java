package me.hapyl.hariant.game.battleground;

public enum BattlegroundTime {
    
    DAWN(0),
    MORNING(1000),
    NOON(6000),
    AFTERNOON(9000),
    SUNSET(12000),
    DUSK(13000),
    NIGHT(15000),
    MIDNIGHT(18000);
    
    private final int absoluteTime;
    
    BattlegroundTime(int absoluteTime) {
        this.absoluteTime = absoluteTime;
    }
    
    public int getAbsoluteTime() {
        return absoluteTime;
    }
    
}
