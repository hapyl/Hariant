package me.hapyl.hariant.hero.mage;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.weapon.projectile.WeaponRangeProjectile;
import me.hapyl.hariant.weapon.projectile.WeaponRangeProjectileTypeRayCast;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

public final class WeaponSoulEaterUltimate extends WeaponRangeProjectile {
    
    WeaponSoulEaterUltimate() {
        super(
                Key.ofString("soul_eater_2"),
                Icon.ofMaterial(Material.NETHERITE_HOE),
                NormalAttack.melee(ElementType.PHYSICAL, AttributeType.ATTACK, 1, 10),
                NormalAttack.ranged(ElementType.AETHER, AttributeType.ATTACK, 144, 20),
                new WeaponRangeProjectileTypeRestlessSoul()
        );
        
        setName(Component.text("Soul Eater"));
        setDescription(Component.text("A scythe infused with stabilized Restless Souls."));
    }
    
    public static class WeaponRangeProjectileTypeRestlessSoul extends WeaponRangeProjectileTypeRayCast {
        
        public static final Component DESCRIPTION = Component.empty()
                                                             .append(Component.text("A stabilized, more powerful version of a "))
                                                             .append(Component.text("soul", Colors.SOUL))
                                                             .append(Component.text(" that deals greatly increased "))
                                                             .append(DamageType.RANGED)
                                                             .append(Component.text("."));
        
        private static final DamageSourceIdentity DAMAGE_SOURCE_IDENTITY = DamageSourceIdentity.create(
                Key.ofString("restless_soul"),
                Component.text("Restless Soul"),
                DeathMessage.create("{player}'s soul was stormed [by {killer}]")
        );
        
        WeaponRangeProjectileTypeRestlessSoul() {
            super(Component.text("Restless Soul"), DESCRIPTION);
            
            this.damageSourceIdentity = DAMAGE_SOURCE_IDENTITY;
        }
        
        @Override
        public void onShoot(@NotNull HariantPlayer player, @NotNull WeaponRangeProjectile weapon) {
            // Decrement soul charge
            final HeroDataMage heroData = player.getHeroData(HeroRegistry.MAGE, HeroDataMage::new);
            final SoulStorm soulStorm = heroData.getSoulStorm();
            
            if (soulStorm == null) {
                return;
            }
            
            soulStorm.decrementCharge();
        }
        
        @Override
        public void onTravel(@NotNull HariantPlayer player, @NotNull WeaponRangeProjectile weapon, @NotNull Location location) {
            player.spawnWorldParticle(location, Particle.SCULK_SOUL, 1, 0.1, 0.0, 0.1, 0.035f);
        }
    }
    
}