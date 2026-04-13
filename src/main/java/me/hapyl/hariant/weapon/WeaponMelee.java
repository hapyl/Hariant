package me.hapyl.hariant.weapon;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.util.Icon;
import org.jetbrains.annotations.NotNull;

public class WeaponMelee extends Weapon {
    public WeaponMelee(@NotNull Key key, @NotNull Icon icon, @NotNull NormalAttack normalAttack) {
        super(key, icon, normalAttack);
    }
}
