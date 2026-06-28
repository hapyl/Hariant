package me.hapyl.hariant.hero.inferno;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.*;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.weapon.WeaponMelee;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HeroInferno extends Hero implements Listener {
    
    public HeroInferno(@NotNull Key key) {
        super(
                key,
                Component.text("Inferno"),
                Attributes.base(1000, 100, 100)
                          .adjust(AttributeType.CRIT_CHANCE, -100)
                          .adjust(AttributeType.EFFECT_RESISTANCE, 70)
                          .adjust(AttributeType.KNOCKBACK_RESISTANCE, 70)
                          .adjust(AttributeType.FIRE_RESISTANCE, 60),
                new WeaponDemonhand()
        );
        
        final HeroProfile profile = getProfile();
        profile.setElementType(ElementType.FIRE);
        profile.setArchetype(Archetype.DAMAGE);
        profile.setGender(Gender.OTHER);
        profile.setAffiliation(Affiliation.HELL);
        profile.setRace(Race.DEMON);
        
        final HeroEquipment equipment = getEquipment();
        equipment.setHeadTexture("3ec891e2104626342ded1f8d9a14e2be42b2da0c2c6026f99ac1c6ef9ab2915c");
        equipment.setChestPlate(36, 25, 31, TrimPattern.TIDE, TrimMaterial.RESIN);
        equipment.setLeggings(36, 25, 31, TrimPattern.SILENCE, TrimMaterial.NETHERITE);
        equipment.setBoots(Material.GOLDEN_BOOTS, TrimPattern.SILENCE, TrimMaterial.GOLD);
        
        setDescription(
                Component.empty()
                         .append(Component.text("What is the right hand of the Demon King doing here?"))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Is it regret, banishment... or perhaps, boredom?"))
        );
    }
    
    @Override
    public @NotNull TalentFirePit getFirstTalent() {
        return TalentRegistry.FIRE_PIT;
    }
    
    @NotNull
    @Override
    public TalentDemonsplitQuazii getSecondTalent() {
        return TalentRegistry.DEMONSPLIT_QUAZII;
    }
    
    @NotNull
    @Override
    public TalentDemonsplitTyphoeus getThirdTalent() {
        return TalentRegistry.DEMONSPLIT_TYPHOEUS;
    }
    
    @Override
    public @NotNull TalentDemonkind getPassiveTalent() {
        return TalentRegistry.DEMON_KIND;
    }
    
    @Override
    public @NotNull TalentInfernalWrath getUltimateTalent() {
        return TalentRegistry.INFERNAL_WRATH;
    }
    
    @Override
    public @NotNull List<Component> supplyActionbar(@NotNull HariantPlayer player) {
        return player.getHeroData(this, HeroDataInferno::new).supplyActionbar(player);
    }
    
    @EventHandler
    public void handlePlayerArmSwingEvent(PlayerArmSwingEvent ev) {
        Hariant.getPlayer(ev.getPlayer()).ifPresent(player -> {
            player.touchHeroData(this, HeroDataInferno.class, data -> {
                if (data.currentDemon != null) {
                    data.currentDemon.swingArm();
                }
            });
        });
    }
    
    private static class WeaponDemonhand extends WeaponMelee {
        WeaponDemonhand() {
            super(
                    Key.ofString("demonhand"),
                    Icon.ofMaterial(Material.BLAZE_ROD),
                    new WeaponDemonhandNormalAttack()
            );
            
            setName(Component.text("Demonhand"));
            setDescription(Component.text("A hand of the demon itself."));
        }
    }
    
    private static class WeaponDemonhandNormalAttack extends NormalAttack {
        
        private static final DamageSourceIdentity DAMAGE_SOURCE_IDENTITY = DamageSourceIdentity.create(
                Key.ofString("demonhand"),
                Component.text("Demonhand"),
                DeathMessage.create("{player} was demonically killed [by {killer}]")
        );
        
        WeaponDemonhandNormalAttack() {
            super(ElementType.FIRE, AttributeType.ATTACK, 66, 10);
        }
        
        @NotNull
        @Override
        public DamageSource.Builder createDamageSource(@NotNull HariantEntity attacker) {
            return DamageSource.builder(DAMAGE_SOURCE_IDENTITY, getScaledValue(attacker))
                               .elementType(elementType)
                               .source(attacker)
                               .components(DamageComponent.trueDamage());
        }
    }
    
}
