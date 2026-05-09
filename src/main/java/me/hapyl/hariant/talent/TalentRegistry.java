package me.hapyl.hariant.talent;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.hero.alchemist.*;
import me.hapyl.hariant.hero.archer.*;
import me.hapyl.hariant.hero.mage.*;
import me.hapyl.hariant.hero.pytaria.*;
import me.hapyl.hariant.hero.troll.*;
import me.hapyl.hariant.registry.StaticRegistry;
import me.hapyl.hariant.registry.StaticRegistryMap;
import org.jetbrains.annotations.NotNull;

public final class TalentRegistry extends StaticRegistry<Talent> {
    
    /**
     * {@link HeroArcher}
     */
    public static final TalentTripleShot TRIPLE_SHOT;
    public static final TalentShockDart SHOCK_DART;
    public static final TalentChainLightning CHAIN_LIGHTNING;
    public static final TalentHawkeye HAWKEYE;
    public static final TalentElectrify ELECTRIFY;
    
    /**
     * {@link HeroPytaria}
     */
    public static final TalentFlowerBreeze FLOWER_BREEZE;
    public static final TalentFlowerEscape FLOWER_ESCAPE;
    public static final TalentRoseIvy ROSE_IVY;
    public static final TalentExcellency EXCELLENCY;
    public static final TalentFeelTheBreeze FEEL_THE_BREEZE;
    
    /**
     * {@link HeroAlchemist}
     */
    public static final TalentAbyssalBottle ABYSSAL_BOTTLE;
    public static final TalentBundleOPotions BUNDLE_O_POTIONS;
    public static final TalentAlchemicalCauldron ALCHEMICAL_CAULDRON;
    public static final TalentAbyssalCorrosion ABYSSAL_CORROSION;
    public static final TalentAbyssalCurse ABYSSAL_CURSE;
    
    /**
     * {@link HeroMage}
     */
    public static final TalentArcaneMute ARCANE_MUTE;
    public static final TalentMetempsychosis METEMPSYCHOSIS;
    public static final TalentSoulStorm SOUL_STORM;
    public static final TalentSoulHarvest SOUL_HARVEST;
    public static final TalentMageUltimate MAGE_ULTIMATE;
    
    /**
     * {@link HeroTroll}
     */
    public static final TalentSpin SPIN;
    public static final TalentPanicRoll PANIC_ROLL;
    public static final TalentRepulsor REPULSOR;
    public static final TalentLastLaugh LAST_LAUGH;
    public static final TalentStickySituation STICKY_SITUATION;
    
    private static final StaticRegistryMap<Talent> REGISTRY;
    
    static {
        REGISTRY = StaticRegistry.requestRegistry(TalentRegistry.class);
        
        TRIPLE_SHOT = REGISTRY.register("triple_shot", TalentTripleShot::new);
        SHOCK_DART = REGISTRY.register("shock_dart", TalentShockDart::new);
        CHAIN_LIGHTNING = REGISTRY.register("chain_lightning", TalentChainLightning::new);
        HAWKEYE = REGISTRY.register("hawkeye", TalentHawkeye::new);
        ELECTRIFY = REGISTRY.register("electrify", TalentElectrify::new);
        
        FLOWER_BREEZE = REGISTRY.register("flower_breeze", TalentFlowerBreeze::new);
        FLOWER_ESCAPE = REGISTRY.register("flower_escape", TalentFlowerEscape::new);
        ROSE_IVY = REGISTRY.register("rose_ivy", TalentRoseIvy::new);
        EXCELLENCY = REGISTRY.register("excellency", TalentExcellency::new);
        FEEL_THE_BREEZE = REGISTRY.register("feel_the_breeze", TalentFeelTheBreeze::new);
        
        ABYSSAL_BOTTLE = REGISTRY.register("abyssal_bottle", TalentAbyssalBottle::new);
        BUNDLE_O_POTIONS = REGISTRY.register("bundle_o_potions", TalentBundleOPotions::new);
        ABYSSAL_CORROSION = REGISTRY.register("abyssal_corrosion", TalentAbyssalCorrosion::new);
        ABYSSAL_CURSE = REGISTRY.register("abyssal_curse", TalentAbyssalCurse::new);
        ALCHEMICAL_CAULDRON = REGISTRY.register("alchemical_cauldron", TalentAlchemicalCauldron::new);
        
        ARCANE_MUTE = REGISTRY.register("arcane_mute", TalentArcaneMute::new);
        SOUL_STORM = REGISTRY.register("soul_storm", TalentSoulStorm::new);
        METEMPSYCHOSIS = REGISTRY.register("metempsychosis", TalentMetempsychosis::new);
        SOUL_HARVEST = REGISTRY.register("soul_harvest", TalentSoulHarvest::new);
        MAGE_ULTIMATE = REGISTRY.register("mage_ultimate_temp", TalentMageUltimate::new);
        
        SPIN = REGISTRY.register("spin", TalentSpin::new);
        PANIC_ROLL = REGISTRY.register("panic_roll", TalentPanicRoll::new);
        REPULSOR = REGISTRY.register("repulsor", TalentRepulsor::new);
        LAST_LAUGH = REGISTRY.register("last_laugh", TalentLastLaugh::new);
        STICKY_SITUATION = REGISTRY.register("sticky_situation", TalentStickySituation::new);
    }
    
    @NotNull
    public static StaticRegistryMap<Talent> getRegistry() {
        return REGISTRY;
    }
    
}
