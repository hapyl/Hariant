package me.hapyl.hariant;

import me.hapyl.eterna.module.math.Tick;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface HariantConstants {
    
    /**
     * Defines the dummy material used for icons.
     */
    @NotNull
    Material DUMMY_MATERIAL = Material.BRICK;
    
    /**
     * Defines the constant for indefinite duration that doesn't tick down.
     */
    int INDEFINITE_DURATION = -1;
    
    /**
     * Defines the constant for indefinite cooldown.
     */
    int INDEFINITE_COOLDOWN = -1;
    
    /**
     * Defines the safe fall distance.
     */
    double FALL_DAMAGE_SAFE_FALL_DISTANCE = 5;
    
    /**
     * Defines the weight for guaranteed drop.
     */
    int GUARANTEED_DROP_CHANCE = 0;
    
    /**
     * Defines the phi (golden ration).
     */
    double PHI = 1.61803398875;
    
    /**
     * Defines the inverse of phu (golden ration).
     */
    double INVERSE_PHI = 1 / PHI;
    
    /**
     * Defines the value which is divided by the defense.
     */
    double DEFENSE_DIVISOR = 1000;
    
    /**
     * Defines the generic prefix for ultimate talents.
     */
    @NotNull
    Component GENERIC_ULTIMATE_PREFIX = Component.text("※");
    
    /**
     * Defines the maximum player level.
     */
    int MAX_LEVEL = 50;
    
    /**
     * Defines the base experience for level 2.
     */
    long BASE_EXPERIENCE = 500;
    
    /**
     * Defines the exponent for level calculations.
     */
    double EXPERIENCE_EXPONENT = 0.8;
    
    /**
     * Defines the average minecraft nickname length.
     */
    int AVERAGE_NICKNAME_LENGTH = 9;
    
    /**
     * Defines the melee knockback strength.
     */
    double MELEE_KNOCKBACK_STRENGTH = 0.25;
    
    /**
     * Defines the range knockback strength, which is {@code 50%} of melee knockback strength.
     */
    double RANGE_KNOCKBACK_STRENGTH = MELEE_KNOCKBACK_STRENGTH * 0.5;
    
    /**
     * Defines the duration of assists.
     */
    long ASSIST_DURATION_MILLIS = 15_000L;
    
    /**
     * Defines the threshold of damage must be dealt to be considered as assist.
     */
    double ASSIST_DAMAGE_THRESHOLD_PERCENTAGE = 0.3;
    
    /**
     * Defines the weapon inventory slot.
     */
    int WEAPON_SLOT = 4;
    
    /**
     * Defines the absolute minimum health bukkit player may have.
     */
    double ABSOLUTE_MIN_HEALTH = 0.1;
    
    /**
     * Defines the absolute maximum health bukkit player may have.
     */
    double ABSOLUTE_MAX_HEALTH = 40;
    
    /**
     * Defines the healing percentage of max health on player elimination.
     */
    double HEALING_ON_PLAYER_ELIMINATION = 0.3;
    
    /**
     * Defines the healing percentage of max health on player assist.
     */
    double HEALING_ON_PLAYER_ASSIST = 0.1;
    
    /**
     * Defines the amount of elemental units to trigger anomaly.
     */
    double ANOMALY_THRESHOLD = 1000;
    
    /**
     * Defines the amount of elements decremented each tick.
     */
    double ELEMENTAL_UNITS_DECREMENT_PER_TICK = 1;
    
    /**
     * Defines the duration, in ticks, how ofter to sync the database with remote.
     */
    int DATABASE_SYNC_PERIOD = Tick.fromMinutes(10);
}
