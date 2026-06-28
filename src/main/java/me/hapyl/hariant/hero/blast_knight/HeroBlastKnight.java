package me.hapyl.hariant.hero.blast_knight;

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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class HeroBlastKnight extends Hero {
    
    public HeroBlastKnight(@NotNull Key key) {
        super(
                key,
                Component.text("Blast Knight"),
                Attributes.base(900, 100, 300)
                          .adjust(AttributeType.MOVEMENT_SPEED, 90)
                          .adjust(AttributeType.KNOCKBACK_RESISTANCE, 50),
                new WeaponRoyalSword()
        );
        
        final HeroProfile profile = getProfile();
        profile.setArchetype(Archetype.DEFENSE);
        profile.setAffiliation(Affiliation.THE_KINGDOM);
        profile.setElementType(ElementType.AETHER);
        profile.setGender(Gender.MALE);
        
        final HeroEquipment equipment = getEquipment();
        equipment.setHeadTexture("f6eaa1fd9d2d49d06a894798d3b145d3ae4dcca038b7da718c7b83a66ef264f0");
        equipment.setChestPlate(19, 17, 33, TrimPattern.RAISER, TrimMaterial.AMETHYST);
        equipment.setLeggings(Material.NETHERITE_LEGGINGS, TrimPattern.SILENCE, TrimMaterial.AMETHYST);
        equipment.setBoots(20, 8, 26, TrimPattern.TIDE, TrimMaterial.NETHERITE);
        
        setDescription(Component.text("A royal knight with high-end technology gadgets."));
    }
    
    @Override
    public void giveWeapon(@NotNull HariantPlayer player) {
        super.giveWeapon(player);
        
        // Also give the shield here which is called on respawn
        this.giveShield(player);
    }
    
    public void giveShield(@NotNull HariantPlayer player) {
        player.getInventory().setItem(EquipmentSlot.OFF_HAND, this.getPassiveTalent().shieldItem);
    }
    
    @Override
    public @NotNull TalentQuantumWard getFirstTalent() {
        return TalentRegistry.QUANTUM_WARD;
    }
    
    @Override
    public @NotNull TalentShieldRam getSecondTalent() {
        return TalentRegistry.SHIELD_RAM;
    }
    
    @Override
    public @NotNull TalentQuantumDischarge getThirdTalent() {
        return TalentRegistry.QUANTUM_DISCHARGE;
    }
    
    @Override
    public @NotNull TalentQuantumShield getPassiveTalent() {
        return TalentRegistry.QUANTUM_SHIELD;
    }
    
    @Override
    public @NotNull TalentNaniteRush getUltimateTalent() {
        return TalentRegistry.NANITE_RUSH;
    }
    
    @Override
    public @NotNull List<Component> supplyActionbar(@NotNull HariantPlayer player) {
        return player.getHeroData(HeroRegistry.BLAST_KNIGHT, HeroDataBlastKnight::new).supplyActionbar(player);
    }
    
    @Override
    public void onDebugCooldownReset(@NotNull HariantPlayer player) {
        player.getHeroData(this, HeroDataBlastKnight::new).incrementQuantumEnergy(1000);
    }
    
    private static class WeaponRoyalSword extends WeaponMelee {
        WeaponRoyalSword() {
            super(Key.ofString("royal_sword"), Icon.ofMaterial(Material.IRON_SWORD), NormalAttack.melee(ElementType.PHYSICAL, AttributeType.ATTACK, 46, 10));
            
            setName(Component.text("Royal Sword"));
            
            setDescription(
                    Component.empty()
                             .append(Component.text("A royal sword, forget of the best quality ore possible."))
                             .appendNewline()
                             .appendNewline()
                             .append(Component.text("It has tiny golden ornate pieces on the edge of the handle."))
            );
        }
    }
    
}