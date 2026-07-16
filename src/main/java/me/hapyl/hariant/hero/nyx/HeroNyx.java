package me.hapyl.hariant.hero.nyx;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.ParticleSpawner;
import me.hapyl.hariant.hero.*;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.weapon.WeaponMelee;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

public class HeroNyx extends Hero {
    
    private final Particle.DustTransition particleDataDustTransition = new Particle.DustTransition(
            Color.fromRGB(66, 16, 181),
            Color.fromRGB(153, 62, 163),
            1
    );
    
    public HeroNyx(@NotNull Key key) {
        super(
                key,
                Component.text("Nyx"),
                Attributes.base(1000, 100, 80)
                          .adjust(AttributeType.EFFECT_RESISTANCE, 25),
                new WeaponNyx()
        );
        
        final HeroProfile profile = getProfile();
        profile.setAffiliation(Affiliation.THE_WITHERS);
        profile.setElementType(ElementType.AETHER);
        profile.setArchetype(Archetype.SUPPORT);
        profile.setGender(Gender.FEMALE);
        profile.setRace(Race.ELF);
        
        final HeroEquipment equipment = getEquipment();
        equipment.setHeadTexture("e4e7d05432c07cbbe6414def96196f434ffc8759a528202463257f42f304670d");
        equipment.setChestPlate(38, 22, 38, TrimPattern.RAISER, TrimMaterial.NETHERITE);
        equipment.setLeggings(22, 28, 28, TrimPattern.DUNE, TrimMaterial.NETHERITE);
        equipment.setBoots(Material.NETHERITE_BOOTS, TrimPattern.DUNE, TrimMaterial.NETHERITE);
        
        setDescription(Component.text("`Chaos... brings victory...`"));
    }
    
    @Override
    public @NotNull TalentWitherPath getFirstTalent() {
        return TalentRegistry.WITHER_PATH;
    }
    
    @Override
    public @NotNull TalentWiltBlink getSecondTalent() {
        return TalentRegistry.WILT_BLINK;
    }
    
    @Override
    public @NotNull TalentDualVerdict getThirdTalent() {
        return TalentRegistry.DUAL_VERDICT;
    }
    
    @Override
    public @NotNull TalentReverberation getPassiveTalent() {
        return TalentRegistry.REVERBERATION;
    }
    
    @Override
    public @NotNull TalentImpalement getUltimateTalent() {
        return TalentRegistry.IMPALEMENT;
    }
    
    public void spawnParticle(@NotNull ParticleSpawner spawner, @NotNull Location location) {
        spawner.spawnWorldParticle(location, Particle.DUST, 1, 0, 0, 0, 1, particleDataDustTransition);
        spawner.spawnWorldParticle(location, Particle.WITCH, 1, 1);
    }
    
    private static class WeaponNyx extends WeaponMelee {
        WeaponNyx() {
            super(
                    Key.ofString("entropy_edge"),
                    Icon.ofMaterial(Material.NETHERITE_SWORD),
                    new NormalAttack(ElementType.PHYSICAL, AttributeType.ATTACK, 67, 10)
            );
            
            setName(Component.text("Entropy's Edge"));
            setDescription(Component.text("`Even at the edge of entropy, I remain.`"));
        }
    }
    
}