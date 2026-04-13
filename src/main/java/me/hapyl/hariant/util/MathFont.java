package me.hapyl.hariant.util;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class MathFont {
    
    private static final Map<Integer, String> DIGITS_MAPPED = Map.of(
            0, "𝟎",
            1, "𝟏",
            2, "𝟐",
            3, "𝟑",
            4, "𝟒",
            5, "𝟓",
            6, "𝟔",
            7, "𝟕",
            8, "𝟖",
            9, "𝟗"
    );
    
    private MathFont() {
    }
    
    @NotNull
    public static String format(final int value) {
        return digitsOf(value).stream()
                              .map(DIGITS_MAPPED::get)
                              .collect(Collectors.joining());
    }
    
    @NotNull
    public static List<Integer> digitsOf(final int value) {
        final List<Integer> digits = Lists.newArrayList();
        int absoluteValue = Math.abs(value);
        
        while (absoluteValue > 0) {
            final int digit = absoluteValue % 10;
            absoluteValue /= 10;
            
            digits.addFirst(digit);
        }
        
        return digits;
    }
    
}
