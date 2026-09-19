package me.hapyl.hariant.weapon;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class WeaponMelee extends Weapon {
    
    private static final Component COMPONENT_ABILITY_COOLDOWN = Component.text("\uD83D\uDDE1");
    
    public WeaponMelee(@NotNull Key key, @NotNull Icon icon, @NotNull NormalAttack normalAttack) {
        super(key, icon, normalAttack);
    }
    
    @Override
    public @NotNull Component getPrimaryAbilityCooldownComponent() {
        return COMPONENT_ABILITY_COOLDOWN;
    }
    
}