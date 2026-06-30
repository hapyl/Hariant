package me.hapyl.hariant.hero.shark;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.hero.*;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentPassive;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.weapon.WeaponMelee;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

public class HeroShark extends Hero {
    
    public HeroShark(@NotNull Key key) {
        super(key, Component.text("Shark"), Attributes.base(1000, 100, 100), new WeaponClaws());
        
        final HeroProfile profile = getProfile();
        profile.setElementType(ElementType.WATER);
        profile.setArchetype(Archetype.DAMAGE);
        profile.setGender(Gender.FEMALE);
        profile.setRace(Race.SHARK);
        
        final HeroEquipment equipment = getEquipment();
        equipment.setHeadTexture("3447e7e8271f573969f2da734c4125f93b2864fb51db69da5ecba7487cf882b0");
        equipment.setChestPlate(157, 175, 194, TrimPattern.RIB, TrimMaterial.QUARTZ);
        equipment.setLeggings(157, 175, 194, TrimPattern.RIB, TrimMaterial.QUARTZ);
        equipment.setBoots(157, 175, 194, TrimPattern.RIB, TrimMaterial.QUARTZ);
        
    }
    
    @Override
    public @NotNull Talent getFirstTalent() {
        return TalentRegistry.TRIPLE_SHOT;
    }
    
    @Override
    public @NotNull Talent getSecondTalent() {
        return TalentRegistry.TRIPLE_SHOT;
    }
    
    @Override
    public @NotNull Talent getThirdTalent() {
        return TalentRegistry.TRIPLE_SHOT;
    }
    
    @Override
    public @NotNull TalentPassive getPassiveTalent() {
        return TalentRegistry.APEX_PREDATOR;
    }
    
    @Override
    public @NotNull TalentUltimate getUltimateTalent() {
        return TalentRegistry.dummyUltimate();
    }
    
    private static class WeaponClaws extends WeaponMelee {
        public WeaponClaws() {
            super(Key.ofString("claws"), Icon.ofMaterial(Material.QUARTZ), NormalAttack.melee(ElementType.PHYSICAL, AttributeType.ATTACK, 60, 10));
            
            setName(Component.text("Claws"));
        }
    }
}
