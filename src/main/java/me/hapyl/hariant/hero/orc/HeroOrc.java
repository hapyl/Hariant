package me.hapyl.hariant.hero.orc;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.*;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.weapon.Weapon;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

public final class HeroOrc extends Hero {
    
    private final WeaponGorkaMorkaMissing weaponMissing = new WeaponGorkaMorkaMissing();
    
    public HeroOrc(@NotNull Key key) {
        super(
                key,
                Component.text("Pakarat Rakab"),
                Attributes.base(1200, 100, 0)
                          .adjust(AttributeType.MOVEMENT_SPEED, 110)
                          .adjust(AttributeType.EFFECT_RESISTANCE, 25)
                          .adjust(AttributeType.TOXIC_RESISTANCE, 60),
                new WeaponGorkaMorka()
        );
        
        final HeroProfile profile = getProfile();
        profile.setRace(Race.ORC);
        profile.setGender(Gender.MALE);
        profile.setElementType(ElementType.ICE);
        profile.setAffiliation(Affiliation.THE_STRONGHOLD);
        
        final HeroEquipment equipment = getEquipment();
        equipment.setHeadTexture("a06220fdfef4d53da8bcef8cbef9a8a3add3d776de43a3781b2f58869ce3d738");
        equipment.setChestPlate(170, 173, 164, TrimPattern.RIB, TrimMaterial.QUARTZ);
        equipment.setLeggings(39, 45, 61, TrimPattern.DUNE, TrimMaterial.COPPER);
        equipment.setBoots(Material.NETHERITE_BOOTS, TrimPattern.SILENCE, TrimMaterial.NETHERITE);
        
        setDescription(Component.text("A half-orc half-dwarf nomad from the North."));
    }
    
    @Override
    public @NotNull WeaponGorkaMorka getWeapon() {
        return (WeaponGorkaMorka) super.getWeapon();
    }
    
    @Override
    public @NotNull TalentOrcGrowl getFirstTalent() {
        return TalentRegistry.ORC_GROWL;
    }
    
    @Override
    public @NotNull TalentPoleaxeDash getSecondTalent() {
        return TalentRegistry.POLEAXE_DASH;
    }
    
    @Override
    public @NotNull TalentPoleaxeSpin getThirdTalent() {
        return TalentRegistry.POLEAXE_SPIN;
    }
    
    @Override
    public @NotNull TalentAngerIssues getPassiveTalent() {
        return TalentRegistry.ANGER_ISSUES;
    }
    
    @Override
    public @NotNull TalentBerserk getUltimateTalent() {
        return TalentRegistry.BERSERK;
    }
    
    @Override
    public @NotNull Weapon getWeapon(@NotNull HariantPlayer player) {
        final HeroDataOrc heroData = player.getHeroData(this, HeroDataOrc::new);
        
        // If weapon entity exists, give the missing weapon
        if (heroData.getWeaponEntity() != null) {
            return weaponMissing;
        }
        
        return super.getWeapon(player);
    }
    
    public void startRecallCooldown(@NotNull HariantPlayer player) {
        weaponMissing.startRecallCooldown(player);
    }
    
    public void giveWeaponStartThrowCooldown(@NotNull HariantPlayer player) {
        this.giveWeapon(player);
        this.getWeapon().startThrowCooldown(player);
    }
    
}