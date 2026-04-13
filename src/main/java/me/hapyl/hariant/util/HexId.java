package me.hapyl.hariant.util;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

public final class HexId implements Comparable<HexId> {
    
    private static final char[] HEX_CHARS = new char[] {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'
    };
    
    private static final Random RANDOM = new SecureRandom();
    private static final int ID_LENGTH = 8;
    private static final char STRING_CHAR = '#';
    
    private final char[] value;
    
    HexId(final char[] value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return STRING_CHAR + new String(value);
    }
    
    @Override
    public int compareTo(@NotNull HexId that) {
        return Arrays.compare(this.value, that.value);
    }
    
    @NotNull
    public static HexId ofRandom() {
        final char[] value = new char[ID_LENGTH];
        
        for (int i = 0; i < ID_LENGTH; i++) {
            value[i] = HEX_CHARS[RANDOM.nextInt(HEX_CHARS.length)];
        }
        
        return new HexId(value);
    }
    
    @NotNull
    public static HexId ofString(@NotNull String value) {
        if (value.charAt(0) != STRING_CHAR) {
            throw new IllegalArgumentException("HexId must start with `%s`!".formatted(STRING_CHAR));
        }
        
        value = value.substring(1);
        
        if (value.length() != ID_LENGTH) {
            throw new IllegalArgumentException("HexId must have length of %s!".formatted(ID_LENGTH));
        }
        
        final char[] chars = value.toCharArray();
        
        for (char ch : chars) {
            final char lowerCh = Character.toLowerCase(ch);
            
            if (!isValidCharacter(lowerCh)) {
                throw new IllegalArgumentException("Unsupported character: %s".formatted(lowerCh));
            }
        }
        
        return new HexId(chars);
    }
    
    private static boolean isValidCharacter(final char ch) {
        for (char hexChar : HEX_CHARS) {
            if (hexChar == ch) {
                return true;
            }
        }
        
        return false;
    }
    
}
