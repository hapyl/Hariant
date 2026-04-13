package me.hapyl.hariant.attribute;

import me.hapyl.eterna.module.util.Nulls;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.util.decimal.DecimalFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public class AttributeElementalImpl extends AttributeImpl {
    
    AttributeElementalImpl(@NotNull ElementType elementType, @NotNull Component name, @NotNull Component description) {
        super(elementType.getPrefix(), name, description, Nulls.or(elementType.getStyle().color(), () -> NamedTextColor.WHITE), DecimalFormat.PERCENTAGE);
    }
    
    @NotNull
    static Attribute ofElementalDamageBonus(@NotNull ElementType elementType) {
        return new AttributeElementalDamageBonusImpl(elementType);
    }
    
    @NotNull
    static Attribute ofElementalResistance(@NotNull ElementType elementType) {
        return new AttributeElementalResistanceImpl(elementType);
    }
    
    public static class AttributeElementalDamageBonusImpl extends AttributeElementalImpl {
        
        AttributeElementalDamageBonusImpl(@NotNull ElementType elementType) {
            super(
                    elementType,
                    Component.empty()
                             .append(elementType.getName())
                             .append(Component.text(" Damage Bonus")),
                    Component.empty()
                             .append(Component.text("Increases the damage dealt by "))
                             .append(elementType.getName())
                             .append(Component.text("."))
            );
        }
        
        @Override
        public double maxValue() {
            return 250; // 250%
        }
    }
    
    public static class AttributeElementalResistanceImpl extends AttributeElementalImpl {
        
        AttributeElementalResistanceImpl(@NotNull ElementType elementType) {
            super(
                    elementType,
                    Component.empty()
                             .append(elementType.getName())
                             .append(Component.text(" Resistance")),
                    Component.empty()
                             .append(Component.text("Decreases the damage taken from "))
                             .append(elementType.getName())
                             .append(Component.text("."))
            );
        }
        
        @Override
        public double minValue() {
            return -80; // -80%
        }
        
        @Override
        public double maxValue() {
            return 300; // 300%
        }
    }
    
}
