package me.hapyl.hariant.statistics;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.hariant.entity.SmallCapsLike;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public enum Statistic implements Named, Described, SmallCapsLike {
    
    KILLS(
            Component.text("Kills"),
            Component.text("Total number of player eliminations.")
    ),
    
    DEATH(
            Component.text("Deaths"),
            Component.text("Total number of deaths.")
    ),
    
    ASSISTS(
            Component.text("Assists"),
            Component.text("Total number of assists.")
    ),
    
    ANOMALY_TRIGGERED(
            Component.text("Anomalies Triggered"),
            Component.text("Total number of elemental anomalies triggered.")
    ),
    
    DAMAGE_DEALT(
            Component.text("DMG Dealt"),
            Component.text("Total amount of damage dealt.")
    ),
    
    DAMAGE_TAKEN(
            Component.text("DMG Taken"),
            Component.text("Total amount of damage taken.")
    ),
    
    TALENT_USAGE(
            Component.text("Talents Used"),
            Component.text("Total number of non-ultimate talents used.")
    ),
    
    ULTIMATE_USAGE(
            Component.text("Ultimate Used"),
            Component.text("Total number of ultimates used.")
    ),
    
    GAMES_PLAYED(
            Component.text("Games Played"),
            Component.text("Total number of games played.")
    ),
    
    GAMES_WON(
            Component.text("Games Won"),
            Component.text("Total number of games won.")
    ),
    
    COINS_EARNED(
            Component.text("Catcoins Earned"),
            Component.text("Total amount of CatCoins earned.")
    ),
    
    EXPERIENCE_GAINED(
            Component.text("Experience Gained"),
            Component.text("Total amount of experience gained.")
    ),
    
    ;
    
    private final Component name;
    private final Component description;
    private final Component smallCaps;
    
    Statistic(@NotNull Component name, @NotNull Component description) {
        this.name = name;
        this.description = description;
        this.smallCaps = SmallCapsLike.asSmallCaps(name);
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public @NotNull Component getDescription() {
        return description;
    }
    
    @Override
    public @NotNull Component asSmallCaps() {
        return smallCaps;
    }
    
}