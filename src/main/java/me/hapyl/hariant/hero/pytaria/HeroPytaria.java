package me.hapyl.hariant.hero.pytaria;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.*;
import me.hapyl.hariant.hero.archer.TalentExcellency;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.weapon.WeaponMelee;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HeroPytaria extends Hero {
    
    public HeroPytaria(@NotNull Key key) {
        super(key, Component.text("Pytaria"), Attributes.base(1200, 80, 20), new WeaponAnnihilallium());
        
        final HeroProfile profile = getProfile();
        profile.setArchetype(Archetype.DAMAGE);
        profile.setElementType(ElementType.PHYSICAL);
        profile.setGender(Gender.FEMALE);
        
        final HeroEquipment equipment = getEquipment();
        equipment.setHeadTexture("7bb0752f9fa87a693c2d0d9f29549375feb6f76952da90d68820e7900083f801");
        equipment.setChestPlate(222, 75, 85);
        equipment.setLeggings(54, 158, 110, TrimPattern.SILENCE, TrimMaterial.IRON);
        equipment.setBoots(179, 204, 204, TrimPattern.SILENCE, TrimMaterial.IRON);
        
        setDescription(
                Component.empty()
                         .append(Component.text("A beautiful girl with addiction to flowers."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("She suffered all her youth, which, in the end, only made her stronger."))
        );
    }
    
    @NotNull
    @Override
    public TalentFlowerBreeze getFirstTalent() {
        return TalentRegistry.FLOWER_BREEZE;
    }
    
    @NotNull
    @Override
    public TalentFlowerEscape getSecondTalent() {
        return TalentRegistry.FLOWER_ESCAPE;
    }
    
    @NotNull
    @Override
    public TalentRoseIvy getThirdTalent() {
        return TalentRegistry.ROSE_IVY;
    }
    
    @NotNull
    @Override
    public TalentExcellency getPassiveTalent() {
        return TalentRegistry.EXCELLENCY;
    }
    
    @NotNull
    @Override
    public TalentFeelTheBreeze getUltimateTalent() {
        return TalentRegistry.FEEL_THE_BREEZE;
    }
    
    @NotNull
    @Override
    public List<Component> supplyActionbar(@NotNull HariantPlayer player) {
        return List.of(player.getHeroData(HeroRegistry.PYTARIA, HeroDataPytaria::new).asComponent());
    }
    
    public static class WeaponAnnihilallium extends WeaponMelee {
        WeaponAnnihilallium() {
            super(
                    Key.ofString("annihilallium"),
                    Icon.ofMaterial(Material.ALLIUM),
                    new NormalAttack(ElementType.PHYSICAL, AttributeType.ATTACK, 85, 10)
            );
            
            setName(Component.text("Annihilallium"));
            
            setDescription(
                    Component.empty()
                             .append(Component.text("A sharp dagger made to look like an Allium."))
                             .appendNewline()
                             .appendNewline()
                             .append(Component.text("It said to be a gift from a loving person."))
            );
        }
    }
    
}
