package me.hapyl.hariant.util.decimal;

import me.hapyl.eterna.module.text.NumberToWord;
import me.hapyl.hariant.attribute.AttributeFormatter;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * <p>Represents a {@link Decimal}, which is a numeric structure extending {@link Number}.</p>
 *
 * <p>
 * It supports the native conversion operations defined by {@link Number}:
 * <ul>
 *     <li>{@link Number#intValue()}</li>
 *     <li>{@link Number#longValue()}</li>
 *     <li>{@link Number#floatValue()}</li>
 *     <li>{@link Number#doubleValue()}</li>
 * </ul>
 * </p>
 *
 * <p>
 * The inherited conversion methods return the internal numeric representation
 * of this {@link Decimal}. If a subclass changes the internal representation,
 * it must override <b>all</b> conversion methods to remain consistent.
 * </p>
 *
 * <p>
 * The {@link #getValue()} returns the logical value used exclusively for
 * {@link #format() formatting}. It is not intended for numeric access and
 * must not be used as a replacement for the {@link Number} conversion methods.
 * </p>
 *
 * <p>
 * {@link Decimal} and its subclasses are package-private and must only be
 * instantiated through the factory methods declared in this class.
 * </p>
 */
public abstract class Decimal extends Number implements AttributeFormatter, ComponentLike {
    
    protected final double value;
    
    Decimal(final double value) {
        this.value = value;
    }
    
    public double getValue() {
        return value;
    }
    
    @Override
    public int intValue() {
        return (int) value;
    }
    
    @Override
    public long longValue() {
        return (long) value;
    }
    
    @Override
    public float floatValue() {
        return (float) value;
    }
    
    @Override
    public double doubleValue() {
        return value;
    }
    
    public int intValueSquared() {
        return this.intValue() * this.intValue();
    }
    
    public long longValueSquared() {
        return this.longValue() * this.longValue();
    }
    
    public float floatValueSquared() {
        return this.floatValue() * this.floatValue();
    }
    
    public double doubleValueSquared() {
        return this.doubleValue() * this.doubleValue();
    }
    
    @NotNull
    @Override
    public abstract Component format();
    
    @NotNull
    @Override
    public final Component asComponent() {
        return format();
    }
    
    @NotNull
    public String wordValue() {
        return NumberToWord.toWord(intValue());
    }
    
    @NotNull
    public static Decimal ofValue(final double value) {
        return new DecimalImpl(value);
    }
    
    @NotNull
    public static Decimal ofValue(final double value, @NotNull DecimalFormat format) {
        return new DecimalImpl(value, format);
    }
    
    @NotNull
    public static Decimal ofPercentage(@Range(from = 1, to = Integer.MAX_VALUE) final double percentage) {
        return new DecimalPercentageImpl(percentage);
    }
    
    @NotNull
    public static Decimal ofAttributeBonus(@NotNull AttributeType attributeType, @Range(from = 1, to = Integer.MAX_VALUE) final double value) {
        return new DecimalAttributeBonusImpl(attributeType, value);
    }
    
    @NotNull
    public static Decimal ofSeconds(@Range(from = 0, to = Integer.MAX_VALUE) float seconds) {
        return new DecimalSecondsImpl(seconds);
    }
    
    @NotNull
    public static Decimal ofElementalApplication(@NotNull ElementType elementType, double units) {
        return new DecimalElementalApplicationImpl(elementType, units);
    }
    
}
