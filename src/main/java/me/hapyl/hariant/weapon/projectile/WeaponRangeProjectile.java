package me.hapyl.hariant.weapon.projectile;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.weapon.NormalAttackRanged;
import me.hapyl.hariant.weapon.WeaponRange;
import me.hapyl.hariant.weapon.ability.Ability;
import me.hapyl.hariant.weapon.ability.AbilityType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

public class WeaponRangeProjectile extends WeaponRange {
    
    private final WeaponRangeProjectileType projectileType;
    
    public WeaponRangeProjectile(@NotNull Key key, @NotNull Icon icon, @NotNull NormalAttack normalAttackMelee, @NotNull NormalAttackRanged normalAttackRanged, @NotNull WeaponRangeProjectileType projectileType) {
        super(key, icon, normalAttackMelee, normalAttackRanged);
        
        this.projectileType = projectileType;
        
        // Set ability
        this.setAbility(AbilityType.RIGHT_CLICK, new WeaponProjectileAbility(this));
    }
    
    public @NotNull WeaponRangeProjectileType getProjectileType() {
        return projectileType;
    }
    
    @NotNull
    public Response shootResponse(@NotNull HariantPlayer player) {
        return Response.ok();
    }
    
    public static class WeaponProjectileAbility extends Ability {
        
        private final WeaponRangeProjectile weapon;
        
        WeaponProjectileAbility(@NotNull WeaponRangeProjectile weapon) {
            super(Component.text("Shoot"));
            
            this.weapon = weapon;
            
            // Set cooldown from ranged attack
            this.setCooldown(weapon.normalAttackRanged.getAttackCooldown());
            
            // Set description
            final NormalAttack rangedAttack = weapon.getRangedAttack();
            final Component projectileName = weapon.projectileType.getName();
            
            // Add common projectile description
            final TextComponent.Builder description = Component.text();
            
            description.append(Component.text("Shoot a "))
                       .append(projectileName.color(Colors.GOLD))
                       .append(Component.text(" projectile forward that deals "))
                       .append(rangedAttack.getElementType().asComponentDamage())
                       .append(Component.text("."));
            
            // Add projectile description
            description.appendNewline();
            description.appendNewline();
            
            description.append(
                    Component.empty()
                             .append(projectileName.color(Colors.GOLD))
                             .append(Component.text(" (Projectile Type)", Colors.DARK_GRAY))
            );
            description.appendNewline();
            description.append(weapon.projectileType.getDescription());
            
            setDescription(description.build());
        }
        
        @NotNull
        @Override
        public Response execute(@NotNull HariantPlayer player) {
            // Make sure the player can actually shoot the weapon
            final Response response = weapon.shootResponse(player);
            
            if (response.isError()) {
                return response;
            }
            
            // Launch the projectile which is handled by the type
            weapon.projectileType.launch(player, weapon);
            
            return Response.ok();
        }
    }
    
}
