package me.hapyl.hariant.hero.shark;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.VanillaAttributeModifier;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.*;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.weapon.WeaponMelee;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public final class HeroShark extends Hero {
    
    private final VanillaAttributeModifier vanillaAttributeModifier = VanillaAttributeModifier.create(
            Key.ofString("shark_modifier"),
            Attribute.WATER_MOVEMENT_EFFICIENCY,
            VanillaAttributeModifier.Operation.FLAT,
            2.5
    );
    
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
        
        setRecommendedAttributes(Set.of(AttributeType.ATTACK, AttributeType.ELEMENTAL_MASTERY, AttributeType.ENERGY_RECHARGE, AttributeType.PHYSICAL_DAMAGE_BONUS, AttributeType.WATER_DAMAGE_BONUS));
        
        setDescription(
                Component.empty()
                         .append(Component.text("A warrior from the depths of this world."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Specializes in bleeding enemies to death."))
        );
    }
    
    @Override
    public void onCreate(@NotNull HariantPlayer player) {
        super.onCreate(player);
        
        player.addVanillaAttributeModifier(vanillaAttributeModifier);
    }
    
    @Override
    public @NotNull TalentSharkBite getFirstTalent() {
        return TalentRegistry.SHARK_BITE;
    }
    
    @Override
    public @NotNull TalentSubmerge getSecondTalent() {
        return TalentRegistry.SUBMERGE;
    }
    
    @Override
    public @NotNull TalentBubbleTrap getThirdTalent() {
        return TalentRegistry.BUBBLE_TRAP;
    }
    
    @Override
    public @NotNull TalentApexPredator getPassiveTalent() {
        return TalentRegistry.APEX_PREDATOR;
    }
    
    @Override
    public @NotNull TalentSharknado getUltimateTalent() {
        return TalentRegistry.SHARKNADO;
    }
    
    @Override
    public @NotNull List<Component> supplyActionbar(@NotNull HariantPlayer player) {
        return player.getHeroData(this, HeroDataShark::new).supplyActionbar(player);
    }
    
    private static class WeaponClaws extends WeaponMelee {
        public WeaponClaws() {
            super(Key.ofString("claws"), Icon.ofMaterial(Material.QUARTZ), NormalAttack.melee(ElementType.PHYSICAL, AttributeType.ATTACK, 60, 10));
            
            setName(Component.text("Jaws"));
            setDescription(Component.text("Jaws of an apex predator."));
        }
    }
}
