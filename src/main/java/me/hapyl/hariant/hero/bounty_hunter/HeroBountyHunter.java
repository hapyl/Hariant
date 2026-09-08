package me.hapyl.hariant.hero.bounty_hunter;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.hero.*;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.weapon.WeaponMelee;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

public final class HeroBountyHunter extends Hero {
    
    public HeroBountyHunter(@NotNull Key key) {
        super(
                key,
                Component.text("Bounty Hunter"),
                Attributes.base(1000, 100, 100),
                new WeaponBloodweep()
        );
        
        final HeroProfile profile = getProfile();
        profile.setAffiliation(Affiliation.MERCENARIES);
        profile.setElementType(ElementType.PHYSICAL);
        profile.setGender(Gender.FEMALE);
        profile.setRace(Race.HUMAN);
        
        final HeroEquipment equipment = getEquipment();
        equipment.setHeadTexture("cf4f866f1432f324e31b0a502e6e9ebccd7a66f474f1ca9cb0cfab879ea22ce0");
        equipment.setChestPlate(50, 54, 57, TrimPattern.SILENCE, TrimMaterial.NETHERITE);
        equipment.setLeggings(80, 97, 68);
        equipment.setBoots(160, 101, 64, TrimPattern.SILENCE, TrimMaterial.IRON);
        
        setDescription(
                Component.empty()
                         .append(Component.text("A skilled bounty hunter of The Mercenaries."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("`Jackpot! Everyone here's got a bounty on their head.`"))
        );
    }
    
    @Override
    public @NotNull TalentShorty getFirstTalent() {
        return TalentRegistry.SHORTY;
    }
    
    @Override
    public @NotNull TalentGrapple getSecondTalent() {
        return TalentRegistry.GRAPPLE;
    }
    
    @Override
    public @NotNull TalentBloodBounty getThirdTalent() {
        return TalentRegistry.BLOOD_BOUNTY;
    }
    
    @Override
    public @NotNull TalentBackstabber getPassiveTalent() {
        return TalentRegistry.BACKSTABBER;
    }
    
    @Override
    public @NotNull TalentSeverance getUltimateTalent() {
        return TalentRegistry.SEVERANCE;
    }
    
    private static class WeaponBloodweep extends WeaponMelee {
        
        WeaponBloodweep() {
            super(Key.ofString("bloodweep"), Icon.ofMaterial(Material.IRON_SWORD), NormalAttack.melee(ElementType.PHYSICAL, AttributeType.ATTACK, 77, 10));
            
            setName(Component.text("Bloodweep"));
            setDescription(Component.text("A ceremonial sword covered in blood."));
        }
        
    }
}
