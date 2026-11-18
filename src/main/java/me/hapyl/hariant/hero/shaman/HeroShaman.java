package me.hapyl.hariant.hero.shaman;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.*;
import me.hapyl.hariant.hero.orc.TalentSpiritualCleansing;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.weapon.WeaponMelee;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class HeroShaman extends Hero {
    
    public static final Component SHAMAN_S = Component.text("Shaman's", Colors.GREEN);
    
    public HeroShaman(@NotNull Key key) {
        super(
                key,
                Component.text("Shaman"),
                Attributes.base(800, 60, 80)
                          .adjust(AttributeType.EFFECT_RESISTANCE, 25)
                          .adjust(AttributeType.MENDING, 130), // Base MENDING is 100
                new WeaponStick()
        );
        
        final HeroProfile profile = getProfile();
        profile.setElementType(ElementType.TOXIC);
        profile.setAffiliation(Affiliation.THE_JUNGLE);
        profile.setRace(Race.ORC);
        
        final HeroEquipment equipment = getEquipment();
        equipment.setHeadTexture("a90515c41b3e131b623cc04978f101aab2e5b82c892890df991b7c079f91d2bd");
        equipment.setChestPlate(110, 94, 74);
        equipment.setLeggings(57, 40, 90);
        
        setDescription(
                Component.empty()
                         .append(Component.text("An orc from the Jungle."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Always rumbles about something."))
        );
    }
    
    @Override
    public @NotNull TalentTotem getFirstTalent() {
        return TalentRegistry.TOTEM;
    }
    
    @Override
    public @NotNull TalentImprisonment getSecondTalent() {
        return TalentRegistry.IMPRISONMENT;
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
    
    @Override
    public @NotNull List<@NotNull Component> supplyActionbar(@NotNull HariantPlayer player) {
        return player.getHeroData(this, HeroDataShaman::new).supplyActionbar(player);
    }
    
    private static class WeaponStick extends WeaponMelee {
        
        WeaponStick() {
            super(Key.ofString("shaman_stick"), Icon.ofMaterial(Material.BAMBOO), NormalAttack.melee(ElementType.PHYSICAL, AttributeType.ATTACK, 54, NormalAttack.DEFAULT_ATTACK_COOLDOWN));
            
            setName(Component.text("Bamboo Stick"));
            setDescription(Component.text("An ordinary stick found in the jungle."));
        }
        
    }
    
}