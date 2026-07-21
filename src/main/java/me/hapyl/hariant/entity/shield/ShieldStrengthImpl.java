package me.hapyl.hariant.entity.shield;

import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.element.ElementType;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;

public final class ShieldStrengthImpl implements ShieldStrength {
    
    public static final @NotNull ShieldStrength INSTANCE = _ -> 1;
    
    private final EnumMap<? extends ElementType, Double> elementalStrengthMap;
    
    ShieldStrengthImpl(final EnumMap<? extends ElementType, Double> elementalStrengthMap) {
        this.elementalStrengthMap = elementalStrengthMap;
    }
    
    @Override
    public double strength(@NotNull ElementType elementType) {
        return elementalStrengthMap.getOrDefault(elementType, HariantConstants.SHIELD_STRENGTH_DEFAULT);
    }
    
}