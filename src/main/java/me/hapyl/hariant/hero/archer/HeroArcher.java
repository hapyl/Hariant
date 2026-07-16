package me.hapyl.hariant.hero.archer;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.hero.*;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.weapon.WeaponBow;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class HeroArcher extends Hero {
    
    public HeroArcher(@NotNull Key key) {
        super(
                key,
                Component.text("Archer"),
                Attributes.base(1000, 100, 100)
                          .adjust(AttributeType.MOVEMENT_SPEED, 115),
                new WeaponBowOfDestiny()
        );
        
        final HeroProfile profile = getProfile();
        profile.setArchetype(Archetype.DAMAGE);
        profile.setElementType(ElementType.ELECTRIC);
        profile.setAffiliation(Affiliation.THE_KINGDOM);
        profile.setGender(Gender.MALE);
        
        final HeroEquipment equipment = getEquipment();
        equipment.setHeadTexture("106c16817c73ff64a4a49b590d2cdb25bcfa52c630fe7281a177eabacdaa857b");
        equipment.setChestPlate(86, 86, 87);
        equipment.setLeggings(75, 75, 87);
        equipment.setBoots(51, 51, 51);
        
        setDescription(
                Component.empty()
                         .append(Component.text("The best archer and protector of the Kingdom."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Legends say he never missed a shot."))
        );
        
        setRecommendedAttributes(Set.of(AttributeType.ATTACK, AttributeType.ENERGY_RECHARGE, AttributeType.ELECTRIC_DAMAGE_BONUS));
    }
    
    @NotNull
    @Override
    public TalentTripleShot getFirstTalent() {
        return TalentRegistry.TRIPLE_SHOT;
    }
    
    @NotNull
    @Override
    public TalentShockDart getSecondTalent() {
        return TalentRegistry.SHOCK_DART;
    }
    
    @NotNull
    @Override
    public TalentChainLightning getThirdTalent() {
        return TalentRegistry.CHAIN_LIGHTNING;
    }
    
    @NotNull
    @Override
    public TalentHawkeye getPassiveTalent() {
        return TalentRegistry.HAWKEYE;
    }
    
    @NotNull
    @Override
    public TalentElectrify getUltimateTalent() {
        return TalentRegistry.ELECTRIFY;
    }
    
    public static class WeaponBowOfDestiny extends WeaponBow {
        WeaponBowOfDestiny() {
            super(
                    Key.ofString("bow_of_destiny"),
                    NormalAttack.melee(ElementType.PHYSICAL, AttributeType.ATTACK, 15, 10),
                    NormalAttack.ranged(ElementType.PHYSICAL, AttributeType.ATTACK, 105, 10)
            );
            
            this.setName(Component.text("Bow of Destiny"));
            this.setDescription(Component.text("A custom-made bow made of unique and expensive looking materials."));
        }
    }
}
