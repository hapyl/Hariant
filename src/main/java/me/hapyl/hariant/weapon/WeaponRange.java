package me.hapyl.hariant.weapon;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.field.DisplayFieldInstance;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WeaponRange extends Weapon {
    
    private static final Component COMPONENT_ABILITY_COOLDOWN = Component.text("\uD83C\uDFF9");
    
    protected final NormalAttackRanged normalAttackRanged;
    
    public WeaponRange(@NotNull Key key, @NotNull Icon icon, @NotNull NormalAttack normalAttackMelee, @NotNull NormalAttackRanged normalAttackRanged) {
        super(key, icon, normalAttackMelee);
        
        this.normalAttackRanged = normalAttackRanged;
    }
    
    @Override
    public @NotNull NormalAttackRanged getRangedAttack() {
        return normalAttackRanged;
    }
    
    @Override
    public @NotNull Component getPrimaryAbilityCooldownComponent() {
        return COMPONENT_ABILITY_COOLDOWN;
    }
    
    @Override
    public void initDisplayFields(@NotNull List<? super DisplayFieldInstance> displayFields) {
        super.initDisplayFields(displayFields);
        
        displayFields.add(DisplayFieldInstance.create(Component.text("Range DMG"), normalAttackRanged.asComponent()));
        displayFields.add(DisplayFieldInstance.create(Component.text("Range Attack Speed"), normalAttackRanged.formatAttackSpeed()));
    }
    
}