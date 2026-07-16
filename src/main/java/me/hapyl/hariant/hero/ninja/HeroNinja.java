package me.hapyl.hariant.hero.ninja;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.damage.KnockbackSource;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.hero.HeroEquipment;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentPassive;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.weapon.Weapon;
import me.hapyl.hariant.weapon.WeaponMelee;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class HeroNinja extends Hero {

//    todo:
//    Shuriken cooldown should start after 3 uses
//    Kemuridama cooldown should start only after leaving invisibility
//    add passive talent("dodge attacks with 25% chance")
//    add ultimate talent("creates mark on the target, shurikens will autonavigate to marked target")
//    remove fall damage

    public HeroNinja(@NotNull Key key) {
        super(  key,
                Component.text("Ninja"),
                Attributes.base(1000, 100, 100)
                        .adjust(AttributeType.CRIT_CHANCE, 30)
                        .adjust(AttributeType.MOVEMENT_SPEED, 120),
                new WeaponKusanagi()
        );

        final HeroEquipment equipment = getEquipment();
        equipment.setHeadTexture("9626c019c8b41c7b249ae9bb6760c4e6980051cf0d6895cb3e6846d81245ad11");
        equipment.setChestPlate(255,255,25);
        equipment.setLeggings(255,255,25);
        equipment.setBoots(255,255,25);
    }


    @Override
    public @NotNull Talent getFirstTalent() {
        return TalentRegistry.HAPPO_SHURIKEN;
    }

    @Override
    public @NotNull Talent getSecondTalent() {
        return TalentRegistry.KEMURIDAMA;
    }

    @Override
    public @NotNull Talent getThirdTalent() {
        return TalentRegistry.SHUNSHIN;
    }

    @Override
    public @NotNull TalentPassive getPassiveTalent() {
        return TalentRegistry.ABYSSAL_CORROSION;
    }

    @Override
    public @NotNull TalentUltimate getUltimateTalent() {
        return TalentRegistry.dummyUltimate();
    }

    public static class WeaponKusanagi extends WeaponMelee{
        WeaponKusanagi(){
            super(
                    Key.ofString("kusanagi"),
                    Icon.ofMaterial(Material.IRON_SWORD),
                    new NormalAttack(ElementType.PHYSICAL, AttributeType.ATTACK, 90,10){
                        @Override
                        public @NotNull KnockbackSource createKnockbackCause(@NotNull HariantEntity attacker){
                            return KnockbackSource.create(attacker, 0.6);
                        }
                    }
            );

            setName(Component.text("Kusanagi"));

            setDescription(
                    Component.empty()
                            .append(Component.text("An ancient blade born of mist and sorrow. Weightless as the night, yet sharp enough to sever fate itself"))
            );
        }
    }
}
