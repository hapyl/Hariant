package me.hapyl.hariant.util.decimal;

import me.hapyl.eterna.module.text.NumberToWord;
import me.hapyl.hariant.annotate.Percentage;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.util.Arithmetic;
import me.hapyl.hariant.util.ComponentFormatter;
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
 * The {@link #value()} returns the logical value used exclusively for
 * {@link #format() formatting}. It is not intended for numeric access and
 * must not be used as a replacement for the {@link Number} conversion methods.
 * </p>
 *
 * <p>
 * {@link Decimal} and its subclasses are package-private and must only be
 * instantiated through the factory methods declared in this class.
 * </p>
 */
public abstract class Decimal extends Number implements ComponentFormatter, ComponentLike, Arithmetic<Decimal> {
    
    protected final double value;
    
    public Decimal(final double value) {
        this.value = value;
    }
    
    public double value() {
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
    
    public @NotNull Component textValue() {
        return Component.text(NumberToWord.toWord((int) value));
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
    
    @Override
    public double add(@NotNull Decimal that) {
        return this.value + that.value;
    }
    
    @Override
    public double subtract(@NotNull Decimal that) {
        return this.value - that.value;
    }
    
    @Override
    public double multiply(@NotNull Decimal that) {
        return this.value * that.value;
    }
    
    @Override
    public double divide(@NotNull Decimal that) {
        return that.value != 0 ? this.value / that.value : 0;
    }
    
    /**
     * A static factory method for creating a {@link Decimal} instance for a whole number, formatted via the given {@link DecimalFormat}.
     *
     * @param value  - The value.
     * @param format - The formatter to use.
     * @return a new decimal instance.
     */
    @NotNull
    public static Decimal ofValue(final double value, @NotNull DecimalFormat format) {
        return new DecimalImpl(value, format);
    }
    
    /**
     * A static factory method for creating a {@link Decimal} instance for a whole number, formatted via {@link DecimalFormat#DECIMAL}.
     *
     * @param value - The value.
     * @return a new decimal instance.
     */
    @NotNull
    public static Decimal ofValue(final double value) {
        return ofValue(value, DecimalFormat.DECIMAL);
    }
    
    /**
     * A static factory method for creating a {@link Decimal} instance for a percentage, which must be passed as a whole number (eg: {@code 30} for {@code 30%}, <b><u>not</u></b> {@code 0.3});
     *
     * <p>
     * Calling the numeric methods divides the percentage value by {@code 100}, returning a decimal representation of the percentage, where displaying
     * the decimal formatted via {@link DecimalFormat#decimal(String, String)} with up to two decimal points.
     * </p>
     *
     * @param percentage - The percentage value.
     * @return a new decimal instance.
     */
    @NotNull
    public static Decimal ofPercentage(@Percentage(Percentage.Type.WHOLE_NUMBER) final double percentage) {
        return new DecimalPercentageImpl(percentage);
    }
    
    /**
     * A static factory method for creating a {@link Decimal} instance for an {@link AttributeType} value, formatted with that attribute's {@link DecimalFormat}.
     *
     * @param attributeType - The attribute type.
     * @param value         - The value.
     * @return a new decimal instance.
     */
    @NotNull
    public static Decimal ofAttribute(@NotNull AttributeType attributeType, @Range(from = 1, to = Integer.MAX_VALUE) final double value) {
        return new DecimalAttributeImpl(attributeType, value);
    }
    
    /**
     * A static factory method for creating a {@link Decimal} instance for a time unit of seconds.
     *
     * <p>
     * Calling the numeric methods multiplies the value by {@code 20}, returning a {@code tick} representation of the seconds, where displaying
     * the decimal formatted via {@link DecimalFormat#SECONDS}.
     * </p>
     *
     * @param seconds - The time unit in seconds.
     * @return a new decimal instance.
     */
    @NotNull
    public static Decimal ofSeconds(@Range(from = 0, to = Integer.MAX_VALUE) final float seconds) {
        return new DecimalSecondsImpl(seconds);
    }
    
    /**
     * A static factory method for creating a {@link Decimal} instance for {@link ElementType} application units, which is formatted with a special
     * character and the element type name.
     *
     * @param elementType - The element type.
     * @param units       - The elemental units.
     * @return a new decimal.
     */
    @NotNull
    public static Decimal ofElementalApplication(@NotNull ElementType elementType, final double units) {
        return new DecimalElementalApplicationImpl(elementType, units);
    }
    
}