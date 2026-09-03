package me.hapyl.hariant.hero.mage;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.hero.*;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class HeroMage extends Hero {
    
    public HeroMage(@NotNull Key key) {
        super(key, Component.text("Mage"), Attributes.base(1000, 100, 100), new WeaponSoulEater());
        
        final HeroProfile profile = getProfile();
        profile.setArchetype(Archetype.DAMAGE);
        profile.setGender(Gender.MALE);
        profile.setElementType(ElementType.AETHER);
        
        final HeroEquipment equipment = this.getEquipment();
        equipment.setHeadTexture("f41e6e4bcd2667bb284fb0dde361894840ea782efbfb717f6244e06b951c2b3f");
        equipment.setChestPlate(82, 12, 135, TrimPattern.VEX, TrimMaterial.AMETHYST);
        equipment.setLeggings(82, 12, 135, TrimPattern.TIDE, TrimMaterial.AMETHYST);
        equipment.setBoots(Material.NETHERITE_BOOTS, TrimPattern.TIDE, TrimMaterial.AMETHYST);
        
        setDescription(Component.text("An amateur mage who was deceived and contaminated by the Abyss."));
        
        setRecommendedAttributes(Set.of(AttributeType.ATTACK, AttributeType.ENERGY_RECHARGE, AttributeType.ELEMENTAL_MASTERY, AttributeType.AETHER_DAMAGE_BONUS));
    }
    
    @Override
    public @NotNull TalentArcaneMute getFirstTalent() {
        return TalentRegistry.ARCANE_MUTE;
    }
    
    @Override
    public @NotNull TalentMetempsychosis getSecondTalent() {
        return TalentRegistry.METEMPSYCHOSIS;
    }
    
    @Override
    public @NotNull TalentSoulFog getThirdTalent() {
        return TalentRegistry.SOUL_FOG;
    }
    
    @Override
    public @NotNull TalentSoulHarvest getPassiveTalent() {
        return TalentRegistry.SOUL_HARVEST;
    }
    
    @Override
    public @NotNull TalentSoulStorm getUltimateTalent() {
        return TalentRegistry.SOUL_STORM;
    }
    
}