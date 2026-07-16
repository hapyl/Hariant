package me.hapyl.hariant.talent;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.blast_knight.TalentNaniteRush;
import me.hapyl.hariant.hero.alchemist.*;
import me.hapyl.hariant.hero.archer.*;
import me.hapyl.hariant.hero.blast_knight.*;
import me.hapyl.hariant.hero.inferno.*;
import me.hapyl.hariant.hero.mage.*;
import me.hapyl.hariant.hero.nyx.*;
import me.hapyl.hariant.hero.pytaria.*;
import me.hapyl.hariant.hero.shark.*;
import me.hapyl.hariant.hero.troll.*;
import me.hapyl.hariant.registry.StaticRegistry;
import me.hapyl.hariant.registry.StaticRegistryMap;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
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
    public static final TalentSoulFog SOUL_FOG;
    public static final TalentSoulStorm SOUL_STORM;
    public static final TalentSoulHarvest SOUL_HARVEST;
    
    /**
     * {@link HeroTroll}
     */
    public static final TalentSpin SPIN;
    public static final TalentPanicRoll PANIC_ROLL;
    public static final TalentRepulsor REPULSOR;
    public static final TalentLastLaugh LAST_LAUGH;
    public static final TalentStickySituation STICKY_SITUATION;
    
    /**
     * {@link HeroInferno}
     */
    public static final TalentFirePit FIRE_PIT;
    public static final TalentDemonsplitQuazii DEMONSPLIT_QUAZII;
    public static final TalentDemonsplitTyphoeus DEMONSPLIT_TYPHOEUS;
    public static final TalentInfernalWrath INFERNAL_WRATH;
    public static final TalentDemonkind DEMON_KIND;
    
    /**
     * {@link HeroNyx}
     */
    public static final TalentWitherPath WITHER_PATH;
    public static final TalentWiltBlink WILT_BLINK;
    public static final TalentDualVerdict DUAL_VERDICT;
    public static final TalentReverberation REVERBERATION;
    public static final TalentImpalement IMPALEMENT;
    
    /**
     * {@link HeroBlastKnight}
     */
    public static final TalentQuantumWard QUANTUM_WARD;
    public static final TalentShieldRam SHIELD_RAM;
    public static final TalentQuantumDischarge QUANTUM_DISCHARGE;
    public static final TalentQuantumShield QUANTUM_SHIELD;
    public static final TalentNaniteRush NANITE_RUSH;
    
    /**
     * {@link HeroShark}
     */
    public static final TalentSharkBite SHARK_BITE;
    public static final TalentSubmerge SUBMERGE;
    public static final TalentBubbleTrap BUBBLE_TRAP;
    public static final TalentApexPredator APEX_PREDATOR;
    public static final TalentSharknado SHARKNADO;
    
    private static final StaticRegistryMap<Talent> REGISTRY;
    private static final TalentUltimate DUMMY_ULTIMATE;
    
    static {
        REGISTRY = StaticRegistry.requestRegistry(TalentRegistry.class);
        DUMMY_ULTIMATE = new TalentUltimateDummy();
        
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
        METEMPSYCHOSIS = REGISTRY.register("metempsychosis", TalentMetempsychosis::new);
        SOUL_FOG = REGISTRY.register("soul_fog", TalentSoulFog::new);
        SOUL_HARVEST = REGISTRY.register("soul_harvest", TalentSoulHarvest::new);
        SOUL_STORM = REGISTRY.register("soul_storm", TalentSoulStorm::new);
        
        SPIN = REGISTRY.register("spin", TalentSpin::new);
        PANIC_ROLL = REGISTRY.register("panic_roll", TalentPanicRoll::new);
        REPULSOR = REGISTRY.register("repulsor", TalentRepulsor::new);
        LAST_LAUGH = REGISTRY.register("last_laugh", TalentLastLaugh::new);
        STICKY_SITUATION = REGISTRY.register("sticky_situation", TalentStickySituation::new);
        
        FIRE_PIT = REGISTRY.register("fire_pit", TalentFirePit::new);
        DEMONSPLIT_QUAZII = REGISTRY.register("demonsplit_quazii", TalentDemonsplitQuazii::new);
        DEMONSPLIT_TYPHOEUS = REGISTRY.register("demonsplit_typhoeus", TalentDemonsplitTyphoeus::new);
        INFERNAL_WRATH = REGISTRY.register("infernal_wrath", TalentInfernalWrath::new);
        DEMON_KIND = REGISTRY.register("demonkind", TalentDemonkind::new);
        
        WITHER_PATH = REGISTRY.register("wither_path", TalentWitherPath::new);
        WILT_BLINK = REGISTRY.register("wilt_blink", TalentWiltBlink::new);
        DUAL_VERDICT = REGISTRY.register("dual_verdict", TalentDualVerdict::new);
        REVERBERATION = REGISTRY.register("reverberation", TalentReverberation::new);
        IMPALEMENT = REGISTRY.register("impalement", TalentImpalement::new);
        
        QUANTUM_WARD = REGISTRY.register("quantum_ward", TalentQuantumWard::new);
        SHIELD_RAM = REGISTRY.register("shield_ram", TalentShieldRam::new);
        QUANTUM_DISCHARGE = REGISTRY.register("quantum_discharge", TalentQuantumDischarge::new);
        NANITE_RUSH = REGISTRY.register("nanite_rush", TalentNaniteRush::new);
        QUANTUM_SHIELD = REGISTRY.register("quantum_shield", TalentQuantumShield::new);
        
        SHARK_BITE = REGISTRY.register("shark_bite", TalentSharkBite::new);
        SUBMERGE = REGISTRY.register("submerge", TalentSubmerge::new);
        BUBBLE_TRAP = REGISTRY.register("bubble_trap", TalentBubbleTrap::new);
        APEX_PREDATOR = REGISTRY.register("apex_predator", TalentApexPredator::new);
        SHARKNADO = REGISTRY.register("sharknado", TalentSharknado::new);
    }
    
    @NotNull
    public static StaticRegistryMap<Talent> getRegistry() {
        return REGISTRY;
    }
    
    @NotNull
    public static TalentUltimate dummyUltimate() {
        return DUMMY_ULTIMATE;
    }
    
    private static class TalentUltimateDummy extends TalentUltimate {
        public TalentUltimateDummy() {
            super(Key.ofString("dummy_ultimate"), Component.text("Dummy Ultimate"), Icon.ofMaterial(Material.BARRIER), UltimateResourceType.ENERGY, Integer.MAX_VALUE);
        }
        
        @Override
        public @NotNull Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, double consumedResource) {
            return Executable.execute(() -> {});
        }
        
        @Override
        public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
            return TalentTarget.none();
        }
    }
}
