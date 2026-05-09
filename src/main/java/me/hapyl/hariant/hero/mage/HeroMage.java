package me.hapyl.hariant.hero.mage;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.*;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.weapon.projectile.WeaponRangeProjectile;
import me.hapyl.hariant.weapon.projectile.WeaponRangeProjectileTypeRayCast;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HeroMage extends Hero {
    
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
    public TalentSoulStorm getThirdTalent() {
        return TalentRegistry.SOUL_STORM;
    }
    
    @NotNull
    @Override
    public TalentSoulHarvest getPassiveTalent() {
        return TalentRegistry.SOUL_HARVEST;
    }
    
    @NotNull
    @Override
    public TalentUltimate getUltimateTalent() {
        return TalentRegistry.MAGE_ULTIMATE;
    }
    
    @NotNull
    @Override
    public List<Component> supplyActionbar(@NotNull HariantPlayer player) {
        final HeroDataMage heroData = player.getHeroData(this, HeroDataMage::new);
        final int souls = heroData.getSouls();
        
        return List.of(
                Definition.SOUL_FRAGMENT.prefix(Component.text(souls))
        );
    }
    
    @Override
    public void debugOnCooldownReset(@NotNull HariantPlayer player) {
        player.getHeroData(this, HeroDataMage::new).incrementSouls(999);
    }
    
    public static class WeaponSoulEater extends WeaponRangeProjectile {
        
        private static final int SOUL_COST = 2;
        
        WeaponSoulEater() {
            super(
                    Key.ofString("soul_eater"),
                    Icon.ofMaterial(Material.IRON_HOE),
                    NormalAttack.melee(ElementType.PHYSICAL, AttributeType.ATTACK, 76, 10),
                    NormalAttack.ranged(ElementType.AETHER, AttributeType.ATTACK, 156, 40),
                    new WeaponRangeProjectileTypeSoul()
            );
            
            setName(Component.text("Soul Eater"));
            setDescription(Component.text("A scythe forged of unknown material, capable of absorbing souls and converting them into firepower."));
        }
        
        @Override
        public @NotNull Response shootResponse(@NotNull HariantPlayer player) {
            final HeroDataMage heroData = player.getHeroData(HeroRegistry.MAGE, HeroDataMage::new);
            
            if (heroData.getSouls() < SOUL_COST) {
                player.playSound(Sound.ENTITY_PLAYER_BURP, 2.0f);
                return Response.error("Not enough souls!");
            }
            
            return Response.ok();
        }
    }
    
    public static class WeaponRangeProjectileTypeSoul extends WeaponRangeProjectileTypeRayCast {
        WeaponRangeProjectileTypeSoul() {
            super(
                    Component.text("Soul"),
                    Component.empty()
                             .append(Component.text("A fragment of a shattered soul infused with unstable "))
                             .append(ElementType.AETHER)
                             .append(Component.text(" energy."))
                             .appendNewline()
                             .appendNewline()
                             .append(Component.text("Uses "))
                             .append(Component.text(WeaponSoulEater.SOUL_COST))
                             .append(Component.text(" x ", NamedTextColor.DARK_GRAY))
                             .append(Definition.SOUL_FRAGMENT)
                             .append(Component.text(" per shot."))
            );
        }
        
        @Override
        public void onHitEntity(@NotNull HariantPlayer player, @NotNull WeaponRangeProjectile weapon, @NotNull HariantEntity entity) {
            final Location location = entity.getMidpointLocation();
            
            player.spawnWorldParticle(location, Particle.SOUL, 8, 0, 0, 0, 0.10f);
            player.spawnWorldParticle(location, Particle.SOUL_FIRE_FLAME, 10, 0, 0, 0, 0.25f);
        }
        
        @Override
        public void onHitBlock(@NotNull HariantPlayer player, @NotNull WeaponRangeProjectile weapon, @NotNull Block block) {
            super.onHitBlock(player, weapon, block);
        }
        
        @Override
        public void onShoot(@NotNull HariantPlayer player, @NotNull WeaponRangeProjectile weapon) {
            // Decrement souls
            player.getHeroData(HeroRegistry.MAGE, HeroDataMage::new).decrementSouls(WeaponSoulEater.SOUL_COST);
            
            // Fx
            player.playWorldSound(Sound.BLOCK_SOUL_SAND_BREAK, 0.75f);
        }
        
        @Override
        public void onTravel(@NotNull HariantPlayer player, @NotNull WeaponRangeProjectile weapon, @NotNull Location location) {
            player.spawnWorldParticle(location, Particle.SOUL, 1, 0.1, 0.0, 0.1, 0.035f);
        }
    }
}
