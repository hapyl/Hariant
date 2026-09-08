package me.hapyl.hariant.alphabet;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface Alphabet {
    
    @NotNull Alphabet FUTHARK = create(
            'ᚨ', 'ᛒ', 'ᚲ', 'ᛞ', 'ᛖ', 'ᚠ',
            'ᚷ', 'ᚺ', 'ᛁ', 'ᛃ', 'ᚲ', 'ᛚ',
            'ᛗ', 'ᚾ', 'ᛟ', 'ᛈ', 'ᚲ', 'ᚱ',
            'ᛊ', 'ᛏ', 'ᚢ', 'ᚢ', 'ᚹ', 'ᚴ', 'ᛁ', 'ᛉ'
    );
    
    char alphabetChar(char englishChar);
    
    char englishChar(char alphabetChar);
    
    default @NotNull String translateTo(@NotNull String english) {
        final StringBuilder builder = new StringBuilder();
        
        for (char englishChar : english.toCharArray()) {
            builder.append(this.alphabetChar(Character.toLowerCase(englishChar)));
        }
        
        return builder.toString();
    }
    
    private static @NotNull Alphabet create(
            char a, char b, char c, char d, char e, char f,
            char g, char h, char i, char j, char k, char l,
            char m, char n, char o, char p, char q, char r,
            char s, char t, char u, char v, char w, char x, char y, char z
    ) {
        return new AlphabetImpl(Map.ofEntries(
                entry('a', a), entry('b', b), entry('c', c), entry('d', d), entry('e', e), entry('f', f),
                entry('g', g), entry('h', h), entry('i', i), entry('j', j), entry('k', k), entry('l', l),
                entry('m', m), entry('n', n), entry('o', o), entry('p', p), entry('q', q), entry('r', r),
                entry('s', s), entry('t', t), entry('u', u), entry('v', v), entry('w', w), entry('x', x), entry('y', y), entry('z', z)
        ));
    }
    
    private static @NotNull Map.Entry<Character, Character> entry(char english, char c) {
        return Map.entry(Character.toLowerCase(english), Character.toLowerCase(c));
    }
    
}
