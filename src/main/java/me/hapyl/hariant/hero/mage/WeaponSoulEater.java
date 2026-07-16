package me.hapyl.hariant.hero.mage;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.util.Definition;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.weapon.projectile.WeaponRangeProjectile;
import me.hapyl.hariant.weapon.projectile.WeaponRangeProjectileTypeRayCast;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public final class WeaponSoulEater extends WeaponRangeProjectile {
    
    // This shit has to be static because of deadlock
    public static final int SOUL_COST = 2;
    
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
                             .append(Component.text(SOUL_COST))
                             .appendSpace()
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
        public void onShoot(@NotNull HariantPlayer player, @NotNull WeaponRangeProjectile weapon) {
            // Decrement souls
            player.getHeroData(HeroRegistry.MAGE, HeroDataMage::new).decrementSouls(SOUL_COST);
            
            // Fx
            player.playWorldSound(Sound.BLOCK_SOUL_SAND_BREAK, 0.75f);
        }
        
        @Override
        public void onTravel(@NotNull HariantPlayer player, @NotNull WeaponRangeProjectile weapon, @NotNull Location location) {
            player.spawnWorldParticle(location, Particle.SOUL, 1, 0.1, 0.0, 0.1, 0.035f);
        }
    }
}
