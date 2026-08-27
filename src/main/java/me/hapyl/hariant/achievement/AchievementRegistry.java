package me.hapyl.hariant.achievement;

import me.hapyl.hariant.registry.StaticRegistry;
import me.hapyl.hariant.registry.StaticRegistryMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class AchievementRegistry extends StaticRegistry<Achievement> {
    
    public static final Achievement FIRST_GAME;
    public static final Achievement BEYOND_CLOUDS;
    
    public static final Achievement ARCHER_BULLSEYE;
    public static final Achievement ARCHER_TRIPLET;
    public static final Achievement ARCHER_CHAIN_LIGHTNING;
    public static final Achievement ARCHER_NO_LUCK_ALL_SKILL;
    
    public static final Achievement PYTARIA_PETAL_STANDING;
    public static final Achievement PYTARIA_HUNGRY_BEE;
    
    public static final Achievement ALCHEMIST_I_THINK_I_DRANK_TOO_MUCH;
    public static final Achievement ALCHEMIST_SPECIAL_DELIVERY;
    public static final Achievement ALCHEMIST_LOCAL_BREWERY;
    
    public static final Achievement MAGE_SILENCE;
    public static final Achievement MAGE_SOUL_HARVESTED;
    public static final Achievement MAGE_SOUL_STORM;
    
    public static final Achievement TROLL_LAUGHING_OUT_LOUD;
    public static final Achievement TROLL_NOT_TODAY_THANK_YOU;
    public static final Achievement TROLL_BLAST_OFF;
    
    public static final Achievement INFERNO_ROTTEN_TO_THE_CORE;
    public static final Achievement INFERNO_ECHOES_OF_PAIN;
    public static final Achievement INFERNO_FULL_IGNITION;
    
    public static final Achievement NYX_DOUBLE_DUTY;
    public static final Achievement NYX_RIPPLE_EFFECT;
    public static final Achievement NYX_LAST_RITES;
    
    public static final Achievement BLAST_KNIGHT_FRONT_LINE;
    public static final Achievement BLAST_KNIGHT_GUARDIAN_ANGEL;
    public static final Achievement BLAST_KNIGHT_PERFECT_TIMING;
    public static final Achievement BLAST_KNIGHT_FULL_RESERVES;
    
    public static final Achievement SHARK_PREY_HUNTED;
    public static final Achievement SHARK_POPPED;
    public static final Achievement SHARK_SURPRISE_ATTACK;
    
    public static final Achievement ZEALOT_OVERCLOCKED;
    public static final Achievement ZEALOT_SIXTH_SENSE;
    public static final Achievement ZEALOT_JUDGMENT_DAY;
    
    public static final Achievement ANOMALY_MASTER_1;
    public static final Achievement ANOMALY_MASTER_2;
    public static final Achievement ANOMALY_MASTER_3;
    
    public static final Achievement COMBAT_MASTER_1;
    public static final Achievement COMBAT_MASTER_2;
    public static final Achievement COMBAT_MASTER_3;
    
    public static final Achievement CRITICAL_HIT_1;
    public static final Achievement CRITICAL_HIT_2;
    public static final Achievement CRITICAL_HIT_3;
    
    public static final Achievement CHAMPION_1;
    public static final Achievement CHAMPION_2;
    public static final Achievement CHAMPION_3;
    public static final Achievement CHAMPION_4;
    
    public static final Achievement ELEMENTALIST;
    public static final Achievement UNLIMITED_POWER;
    public static final Achievement NUH_UH;
    public static final Achievement CHAIN_OF_COMMAND;
    
    private static final StaticRegistryMap<Achievement> REGISTRY;
    private static final Map<AchievementCategory, AchievementCategoryInfo> CATEGORY_INFO;
    
    static {
        REGISTRY = requestRegistry(AchievementRegistry.class);
        
        FIRST_GAME = REGISTRY.register("first_game", AchievementFirstGame::new);
        BEYOND_CLOUDS = REGISTRY.register("beyond_clouds", AchievementBeyondClouds::new);
        
        // *-* Hero Related Achievements Start *-* //
        
        ARCHER_BULLSEYE = REGISTRY.register("archer_bullseye", AchievementArcherBullseye::new);
        ARCHER_TRIPLET = REGISTRY.register("archer_triplet", AchievementArcherTriplet::new);
        ARCHER_CHAIN_LIGHTNING = REGISTRY.register("archer_chain_lightning", AchievementArcherChainLightning::new);
        ARCHER_NO_LUCK_ALL_SKILL = REGISTRY.register("archer_no_luck_all_skill", AchievementArcherNoLuckAllSkill::new);
        
        PYTARIA_PETAL_STANDING = REGISTRY.register("pytaria_last_petal_standing", AchievementPytariaLastPetalStanding::new);
        PYTARIA_HUNGRY_BEE = REGISTRY.register("pytaria_hungry_bee", AchievementPytariaHungryBee::new);
        
        ALCHEMIST_I_THINK_I_DRANK_TOO_MUCH = REGISTRY.register("alchemist_i_think_i_drank_too_much", AchievementAlchemistIThinkIDrankTooMuch::new);
        ALCHEMIST_SPECIAL_DELIVERY = REGISTRY.register("alchemist_special_delivery", AchievementAlchemistSpecialDelivery::new);
        ALCHEMIST_LOCAL_BREWERY = REGISTRY.register("alchemist_local_brewery", AchievementAlchemistLocalBrewery::new);
        
        MAGE_SILENCE = REGISTRY.register("mage_silence", AchievementMageSilence::new);
        MAGE_SOUL_HARVESTED = REGISTRY.register("mage_soul_harvested", AchievementMageSoulHarvested::new);
        MAGE_SOUL_STORM = REGISTRY.register("mage_soul_storm", AchievementMageSoulStorm::new);
        
        TROLL_LAUGHING_OUT_LOUD = REGISTRY.register("troll_laughing_out_loud", AchievementTrollLaughingOutLoud::new);
        TROLL_NOT_TODAY_THANK_YOU = REGISTRY.register("troll_not_today_thank_you", AchievementTrollNotTodayThankYou::new);
        TROLL_BLAST_OFF = REGISTRY.register("troll_blast_off", AchievementTrollBlastOff::new);
        
        INFERNO_ROTTEN_TO_THE_CORE = REGISTRY.register("inferno_rotten_to_the_core", AchievementInfernoRottenToTheCore::new);
        INFERNO_ECHOES_OF_PAIN = REGISTRY.register("inferno_echoes_of_pain", AchievementInfernoEchoesOfPain::new);
        INFERNO_FULL_IGNITION = REGISTRY.register("inferno_full_ignition", AchievementInfernoFullIgnition::new);
        
        NYX_DOUBLE_DUTY = REGISTRY.register("nyx_double_duty", AchievementNyxDoubleDuty::new);
        NYX_RIPPLE_EFFECT = REGISTRY.register("nyx_ripple_effectg", AchievementNyxRippleEffect::new);
        NYX_LAST_RITES = REGISTRY.register("nyx_last_rites", AchievementNyxLastRites::new);
        
        BLAST_KNIGHT_FRONT_LINE = REGISTRY.register("blast_knight_front_line", AchievementBlastKnightFrontLine::new);
        BLAST_KNIGHT_GUARDIAN_ANGEL = REGISTRY.register("blast_knight_guardian_angel", AchievementBlastKnightGuardianAngel::new);
        BLAST_KNIGHT_PERFECT_TIMING = REGISTRY.register("blast_knight_perfect_timing", AchievementBlastKnightPerfectTiming::new);
        BLAST_KNIGHT_FULL_RESERVES = REGISTRY.register("blast_knight_full_reserves", AchievementBlastKnightFullReserves::new);
        
        SHARK_PREY_HUNTED = REGISTRY.register("shark_prey_hunted", AchievementSharkPreyHunted::new);
        SHARK_POPPED = REGISTRY.register("shark_popped", AchievementSharkPopped::new);
        SHARK_SURPRISE_ATTACK = REGISTRY.register("shark_surprise_attack", AchievementSharkSurpriseAttack::new);
        
        ZEALOT_OVERCLOCKED = REGISTRY.register("zealot_overclocked", AchievementZealotOverclocked::new);
        ZEALOT_SIXTH_SENSE = REGISTRY.register("zealot_sixth_sense", AchievementZealotSixthSense::new);
        ZEALOT_JUDGMENT_DAY = REGISTRY.register("zealot_judment_day", AchievementZealotJudgmentDay::new);
        
        // *-* Hero Related Achievements End *-* //
        
        ANOMALY_MASTER_1 = REGISTRY.register("anomaly_master_1", key -> new AchievementAnomalyMaster(key, 1, 5, AchievementTier.TIER_1, null));
        ANOMALY_MASTER_2 = REGISTRY.register("anomaly_master_2", key -> new AchievementAnomalyMaster(key, 2, 20, AchievementTier.TIER_2, ANOMALY_MASTER_1));
        ANOMALY_MASTER_3 = REGISTRY.register("anomaly_master_3", key -> new AchievementAnomalyMaster(key, 3, 50, AchievementTier.TIER_3, ANOMALY_MASTER_2));
        
        COMBAT_MASTER_1 = REGISTRY.register("combat_master_1", key -> new AchievementCombatMaster(key, 1, 1_000, AchievementTier.TIER_1, null));
        COMBAT_MASTER_2 = REGISTRY.register("combat_master_2", key -> new AchievementCombatMaster(key, 2, 20_000, AchievementTier.TIER_2, COMBAT_MASTER_1));
        COMBAT_MASTER_3 = REGISTRY.register("combat_master_3", key -> new AchievementCombatMaster(key, 3, 50_000, AchievementTier.TIER_3, COMBAT_MASTER_2));
        
        CRITICAL_HIT_1 = REGISTRY.register("critical_hit_1", key -> new AchievementCriticalHit(key, 1, 10, AchievementTier.TIER_1, null));
        CRITICAL_HIT_2 = REGISTRY.register("critical_hit_2", key -> new AchievementCriticalHit(key, 2, 100, AchievementTier.TIER_2, CRITICAL_HIT_1));
        CRITICAL_HIT_3 = REGISTRY.register("critical_hit_3", key -> new AchievementCriticalHit(key, 3, 1000, AchievementTier.TIER_3, CRITICAL_HIT_2));
        
        CHAMPION_1 = REGISTRY.register("champion_1", key -> new AchievementChampion(key, 1, 5, AchievementTier.TIER_1, null));
        CHAMPION_2 = REGISTRY.register("champion_2", key -> new AchievementChampion(key, 2, 10, AchievementTier.TIER_2, CHAMPION_1));
        CHAMPION_3 = REGISTRY.register("champion_3", key -> new AchievementChampion(key, 3, 50, AchievementTier.TIER_3, CHAMPION_2));
        CHAMPION_4 = REGISTRY.register("champion_4", key -> new AchievementChampion(key, 4, 100, AchievementTier.TIER_4, CHAMPION_3));
        
        ELEMENTALIST = REGISTRY.register("elementalist", AchievementElementalist::new);
        UNLIMITED_POWER = REGISTRY.register("unlimited_power", AchievementUnlimitedPower::new);
        NUH_UH = REGISTRY.register("nuh_uh", AchievementNuhUh::new);
        CHAIN_OF_COMMAND = REGISTRY.register("chain_of_command", AchievementChainOfCommand::new);
        
        // Map achievements by their category
        CATEGORY_INFO = REGISTRY.values()
                                .stream()
                                .collect(Collectors.groupingBy(
                                        Achievement::getCategory,
                                        Collectors.collectingAndThen(Collectors.toList(), AchievementCategoryInfo::create)
                                ));
    }
    
    public static @NotNull StaticRegistryMap<Achievement> getRegistry() {
        return REGISTRY;
    }
    
    public static @Nullable AchievementCategoryInfo getCategoryInfo(@NotNull AchievementCategory category) {
        return CATEGORY_INFO.get(category);
    }
    
    public static @NotNull Stream<? extends Achievement> streamCategory(@NotNull AchievementCategory category) {
        final AchievementCategoryInfo categoryInfo = getCategoryInfo(category);
        
        return categoryInfo != null ? categoryInfo.stream() : Stream.empty();
    }
    
    public static int totalNumberOfAchievements() {
        return REGISTRY.keys().size();
    }
    
}