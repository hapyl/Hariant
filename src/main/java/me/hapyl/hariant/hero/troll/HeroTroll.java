package me.hapyl.hariant.hero.troll;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.damage.KnockbackSource;
import me.hapyl.hariant.hero.*;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.weapon.WeaponMelee;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class HeroTroll extends Hero {
    
    public HeroTroll(@NotNull Key key) {
        super(
                key,
                Component.text("Troll"),
                Attributes.base(1000, 100, 100)
                          .adjust(AttributeType.LUCK, 20),
                new WeaponStickonator()
        );
        
        final HeroProfile profile = getProfile();
        profile.setElementType(ElementType.PHYSICAL);
        profile.setArchetype(Archetype.HEXBANE);
        profile.setGender(Gender.MALE);
        
        final HeroEquipment equipment = getEquipment();
        equipment.setHeadTexture("9626c019c8b41c7b249ae9bb6760c4e6980051cf0d6895cb3e6846d81245ad11");
        equipment.setChestPlate(255, 204, 84);
        equipment.setLeggings(255, 204, 84);
        equipment.setBoots(255, 204, 84);
        
        setDescription(Component.text("Not a good fighter, but definitely a good troll!"));
        
        setRecommendedAttributes(Set.of(AttributeType.MAX_HEALTH, AttributeType.DEFENSE, AttributeType.LUCK, AttributeType.PHYSICAL_DAMAGE_BONUS));
    }
    
    @Override
    public @NotNull TalentSpin getFirstTalent() {
        return TalentRegistry.SPIN;
    }
    
    @Override
    public @NotNull TalentPanicRoll getSecondTalent() {
        return TalentRegistry.PANIC_ROLL;
    }
    
    @Override
    public @NotNull TalentRepulsor getThirdTalent() {
        return TalentRegistry.REPULSOR;
    }
    
    @Override
    public @NotNull TalentLastLaugh getPassiveTalent() {
        return TalentRegistry.LAST_LAUGH;
    }
    
    @Override
    public @NotNull TalentFunnyTime getUltimateTalent() {
        return TalentRegistry.FUNNY_TIME;
    }
    
    public static class WeaponStickonator extends WeaponMelee {
        WeaponStickonator() {
            super(
                    Key.ofString("stickonator"),
                    Icon.ofMaterial(Material.STICK),
                    new NormalAttack(ElementType.PHYSICAL, AttributeType.ATTACK, 56, 10) {
                        @Override
                        public @NotNull KnockbackSource createKnockbackCause(@NotNull HariantEntity attacker) {
                            return KnockbackSource.create(attacker, 0.4);
                        }
                    }
            );
            
            setName(Component.text("Stickonator"));
            
            setDescription(
                    Component.empty()
                             .append(Component.text("- What's brown and sticky?")).appendNewline()
                             .append(Component.text("- Don't say it...")).appendNewline()
                             .append(Component.text("- A stick!")).appendNewline()
                             .append(Component.text("- ..."))
            );
        }
    }
}
