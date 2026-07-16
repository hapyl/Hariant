package me.hapyl.hariant.inventory.drop;

import me.hapyl.hariant.Hariant;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public interface Amount extends ComponentLike {
    
    int amount();
    
    @Override
    @NotNull
    default Component asComponent() {
        return this.amount() == 1 ? Component.empty() : Component.text(this.amount());
    }
    
    @NotNull
    static Amount fixed(final int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("`amount` cannot be negative!");
        }
        
        return () -> amount;
    }
    
    @NotNull
    static Amount range(final int from, final int to) {
        return new AmountRangeImpl(from, to);
    }
    
    class AmountRangeImpl implements Amount {
        private static final Component SEPARATOR = Component.text("~");
        
        private final int from;
        private final int to;
        
        AmountRangeImpl(final int from, final int to) {
            if (from < 0) {
                throw new IllegalArgumentException("`from` cannot be negative!");
            }
            
            if (from >= to) {
                throw new IllegalArgumentException("`from` cannot be higher or equal to `to`!");
            }
            
            this.from = from;
            this.to = to;
        }
        
        @Override
        public int amount() {
            // Generate a random number, inclusively
            return Hariant.getRandom().nextInt(from, to + 1);
        }
        
        @Override
        @NotNull
        public Component asComponent() {
            return Component.empty().append(Component.text(from)).append(SEPARATOR).append(Component.text(to));
        }
    }
    
}
