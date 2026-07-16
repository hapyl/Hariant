package me.hapyl.hariant.entity.vanilla;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.weapon.NormalAttackRanged;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Parched;
import org.jetbrains.annotations.NotNull;

public final class VanillaEntityParched extends VanillaEntity<Parched> {
    
    VanillaEntityParched(@NotNull Parched parched) {
        super(
                parched,
                Component.text("Parched"),
                HariantEntity.createHeadComponent("24aeceff5f26dd8413c5c03547c234ac03108d187af0b9cd834a8ce12598591c"),
                Attributes.base(500, 50, 50)
        );
    }
    
    @Override
    public NormalAttackRanged getRangedAttack() {
        return NormalAttack.ranged(ElementType.PHYSICAL, AttributeType.ATTACK, 50, 10);
    }
    
}
