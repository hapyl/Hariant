package me.hapyl.hariant.entity;

import me.hapyl.hariant.util.decimal.Decimal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class HariantRandom extends Random {
    
    public HariantRandom() {
    }
    
    private HariantRandom(long seed) {
        super(seed);
    }
    
    @NotNull
    public <E> E choice(@NotNull Collection<? extends E> collection) {
        final int randomIndex = nextInt(collection.size());
        
        if (collection instanceof List<? extends E> list) {
            return list.get(randomIndex);
        }
        else {
            int index = 0;
            
            for (E e : collection) {
                if (index++ == randomIndex) {
                    return e;
                }
            }
        }
        
        throw new IllegalArgumentException("Cannot choose a random element from an empty collection!");
    }
    
    @NotNull
    public <E> E choice(@NotNull E[] array) {
        if (array.length == 0) {
            throw new IllegalArgumentException("Cannot choose a random element from an empty array!");
        }
        
        return array[nextInt(array.length)];
    }
    
    public boolean chance(@Range(from = 0, to = 1) double chance) {
        return nextDouble() < Math.clamp(chance, 0, 1);
    }
    
    public boolean chance(@NotNull Decimal decimal) {
        return chance(decimal.doubleValue());
    }
    
    public double nextSignedInt(int bound) {
        return this.nextInt(-bound, bound);
    }
    
    public double nextSignedFloat(float bound) {
        return this.nextFloat(-bound, bound);
    }
    
    public double nextSignedDouble(double bound) {
        return this.nextDouble(-bound, bound);
    }
    
}