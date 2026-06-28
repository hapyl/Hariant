package me.hapyl.hariant.hero.mage;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.*;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.weapon.Weapon;
import me.hapyl.hariant.weapon.projectile.WeaponRangeProjectile;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HeroMage extends Hero {
    
    private final @NotNull WeaponRangeProjectile weaponSoulEaterUltimate = new WeaponSoulEaterUltimate();
    
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
    }
    
    @NotNull
    @Override
    public TalentArcaneMute getFirstTalent() {
        return TalentRegistry.ARCANE_MUTE;
    }
    
    @NotNull
    @Override
    public TalentMetempsychosis getSecondTalent() {
        return TalentRegistry.METEMPSYCHOSIS;
    }
    
    @NotNull
    @Override
    public TalentSoulFog getThirdTalent() {
        return TalentRegistry.SOUL_FOG;
    }
    
    @NotNull
    @Override
    public TalentSoulHarvest getPassiveTalent() {
        return TalentRegistry.SOUL_HARVEST;
    }
    
    @NotNull
    @Override
    public TalentSoulStorm getUltimateTalent() {
        return TalentRegistry.SOUL_STORM;
    }
    
    @Override
    public @NotNull Weapon getWeapon(@NotNull HariantPlayer player) {
        final HeroDataMage heroData = player.getHeroData(this, HeroDataMage::new);
        
        return heroData.hasSoulStorm()
               ? weaponSoulEaterUltimate
               : super.getWeapon(player);
    }
    
    public void giveWeapon(@NotNull HariantPlayer player, boolean ultimateWeapon) {
        this.giveWeapon(player, ultimateWeapon ? weaponSoulEaterUltimate : super.getWeapon());
    }
    
    @NotNull
    @Override
    public List<Component> supplyActionbar(@NotNull HariantPlayer player) {
        return player.getHeroData(this, HeroDataMage::new).supplyActionbar(player);
    }
    
    @Override
    public void onDebugCooldownReset(@NotNull HariantPlayer player) {
        player.getHeroData(this, HeroDataMage::new).incrementSouls(999);
    }
    
}