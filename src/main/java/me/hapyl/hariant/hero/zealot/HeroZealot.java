package me.hapyl.hariant.hero.zealot;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.*;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.weapon.WeaponMelee;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class HeroZealot extends Hero {
    
    private static final Material[] WEAPON_MATERIAL = {
            Material.DIAMOND_SWORD,
            Material.GOLDEN_SWORD
    };
    
    private final HeroEquipment equipmentOverload;
    
    public HeroZealot(@NotNull Key key) {
        super(
                key,
                Component.text("Zealot"),
                Attributes.base(1000, 100, 100)
                          .adjust(AttributeType.FEROCITY, 25),
                new WeaponPsionicBlade()
        );
        
        final HeroProfile profile = getProfile();
        profile.setArchetype(Archetype.DAMAGE);
        profile.setElementType(ElementType.AETHER);
        profile.setAffiliation(Affiliation.THE_SPACE);
        profile.setRace(Race.ALIEN);
        
        final HeroEquipment equipment = getEquipment();
        equipment.setHeadTexture("131530db74bac84ad9e322280c56c4e0199fbe879883b76c9cf3fd8ff19cf025");
        equipment.setChestPlate(104, 166, 232, TrimPattern.SILENCE, TrimMaterial.DIAMOND);
        equipment.setLeggings(Material.DIAMOND_LEGGINGS, TrimPattern.SILENCE, TrimMaterial.DIAMOND);
        equipment.setBoots(Material.DIAMOND_BOOTS, TrimPattern.SILENCE, TrimMaterial.DIAMOND);
        
        this.equipmentOverload = new HeroEquipment();
        this.equipmentOverload.setHeadTexture(equipment.getHeadTexture());
        this.equipmentOverload.setChestPlate(104, 166, 232, TrimPattern.SILENCE, TrimMaterial.GOLD);
        this.equipmentOverload.setLeggings(Material.GOLDEN_LEGGINGS, TrimPattern.SILENCE, TrimMaterial.GOLD);
        this.equipmentOverload.setBoots(Material.GOLDEN_BOOTS, TrimPattern.RIB, TrimMaterial.GOLD);
        
        setDescription(Component.text("A space ranger with a sole goal of maintaining order."));
    }
    
    @Override
    public @NotNull TalentZealotry getFirstTalent() {
        return TalentRegistry.ZEALOTRY;
    }
    
    @Override
    public @NotNull TalentPsionicOverload getSecondTalent() {
        return TalentRegistry.PSIONIC_OVERLOAD;
    }
    
    @Override
    public @NotNull TalentMalevolentHitshield getThirdTalent() {
        return TalentRegistry.MALEVOLENT_HITSHIELD;
    }
    
    @Override
    public @NotNull TalentReckoning getPassiveTalent() {
        return TalentRegistry.RECKONING;
    }
    
    @Override
    public @NotNull TalentMaintainOrder getUltimateTalent() {
        return TalentRegistry.MAINTAIN_ORDER;
    }
    
    public void equip(@NotNull HariantPlayer player, boolean original) {
        final PlayerInventory inventory = player.getInventory();
        
        // Update weapon texture first because `equip()` calls inventory update
        final int weaponSlot = getWeaponSlot(player);
        final ItemStack weaponItemStack = inventory.getItem(weaponSlot);
        
        if (weaponItemStack != null) {
            inventory.setItem(weaponSlot, weaponItemStack.withType(WEAPON_MATERIAL[original ? 0 : 1]));
        }
        
        if (original) {
            this.getEquipment().equip(player);
        }
        else {
            this.equipmentOverload.equip(player);
        }
    }
    
    @Override
    public @NotNull List<Component> supplyActionbar(@NotNull HariantPlayer player) {
        return player.getHeroData(this, HeroDataZealot::new).supplyActionbar(player);
    }
    
    private static class WeaponPsionicBlade extends WeaponMelee {
        
        WeaponPsionicBlade() {
            super(
                    Key.ofString("psionic_blade"),
                    Icon.ofMaterial(WEAPON_MATERIAL[0]),
                    NormalAttack.melee(ElementType.PHYSICAL, AttributeType.ATTACK, 47, 10)
            );
            
            setName(Component.text("Psionic Blade"));
            setDescription(Component.text("A space katana used by Zealot warriors."));
        }
        
    }
    
}