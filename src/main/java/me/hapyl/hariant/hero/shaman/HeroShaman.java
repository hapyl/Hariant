package me.hapyl.hariant.hero.shaman;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.hero.*;
import me.hapyl.hariant.hero.orc.TalentSpiritualCleansing;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.weapon.WeaponMelee;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class HeroShaman extends Hero {
    
    //
    // [1] Totem
    //  Throw a totem that heals teammates and damages enemies
    //
    // [2] Totem Jump
    //  Jump towards the targeting totem
    //
    // [3] Shaman's Mark
    //  Throw a projectile forward that sticks to the first teammate it hits, applying Shaman's Mark unto them.
    //
    // [4] Overheal
    //
    // [5] Spiritual Cleansing
    //
    
    
    public HeroShaman(@NotNull Key key) {
        super(key, Component.text("Shaman"), Attributes.base(1000, 100, 100), new WeaponStick());
        
        final HeroProfile profile = getProfile();
        profile.setElementType(ElementType.TOXIC);
        profile.setAffiliation(Affiliation.THE_JUNGLE);
        profile.setRace(Race.ORC);
        
        final HeroEquipment equipment = getEquipment();
        equipment.setHeadTexture("a90515c41b3e131b623cc04978f101aab2e5b82c892890df991b7c079f91d2bd");
        equipment.setChestPlate(110, 94, 74);
        equipment.setLeggings(57, 40, 90);
        
    }
    
    @Override
    public boolean isEnabled() {
        return false;
    }
    
    @Override
    public @NotNull TalentTotem getFirstTalent() {
        return TalentRegistry.TOTEM;
    }
    
    @Override
    public @NotNull TalentTotemJump getSecondTalent() {
        return TalentRegistry.TOTEM_JUMP;
    }
    
    @Override
    public @NotNull TalentShamanMark getThirdTalent() {
        return TalentRegistry.SHAMAN_MARK;
    }
    
    @Override
    public @NotNull TalentOverheal getPassiveTalent() {
        return TalentRegistry.OVERHEAL;
    }
    
    @Override
    public @NotNull TalentSpiritualCleansing getUltimateTalent() {
        return TalentRegistry.SPIRITUAL_CLEANSING;
    }
    
    private static class WeaponStick extends WeaponMelee {
        
        WeaponStick() {
            super(Key.ofString("shaman_stick"), Icon.ofMaterial(Material.STICK), NormalAttack.melee(ElementType.PHYSICAL, AttributeType.ATTACK, 1, NormalAttack.DEFAULT_ATTACK_COOLDOWN));
        }
        
    }
    
}