package me.hapyl.hariant.alphabet;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class AlphabetImpl implements Alphabet {
    
    private final Map<Character, Character> alphabet;
    
    AlphabetImpl(@NotNull Map<Character, Character> alphabet) {
        this.alphabet = alphabet;
    }
    
    @Override
    public char alphabetChar(char englishChar) {
        return alphabet.getOrDefault(englishChar, englishChar);
    }
    
    @Override
    public char englishChar(char alphabetChar) {
        for (Map.Entry<Character, Character> entry : alphabet.entrySet()) {
            if (entry.getValue() == alphabetChar) {
                return entry.getKey();
            }
        }
        
        return alphabetChar;
    }
    
    
}
