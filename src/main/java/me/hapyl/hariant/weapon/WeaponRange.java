package me.hapyl.hariant.weapon;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.util.Icon;
import org.jetbrains.annotations.NotNull;

public class WeaponRange extends Weapon {
    
    protected final NormalAttackRanged normalAttackRanged;
    
    public WeaponRange(@NotNull Key key, @NotNull Icon icon, @NotNull NormalAttack normalAttackMelee, @NotNull NormalAttackRanged normalAttackRanged) {
        super(key, icon, normalAttackMelee);
        
        this.normalAttackRanged = normalAttackRanged;
    }
    
    @NotNull
    @Override
    public NormalAttackRanged getRangedAttack() {
        return normalAttackRanged;
    }
    
}