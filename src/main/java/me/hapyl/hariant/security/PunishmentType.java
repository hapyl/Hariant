package me.hapyl.hariant.security;

public enum PunishmentType {
    
    KICK,
    MUTE,
    BAN;
    
    @Override
    public final String toString() {
        return name().toLowerCase();
    }
    
}
