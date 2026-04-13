package me.hapyl.hariant.util;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface BooleanExplained {
    
    boolean booleanValue();
    
    @NotNull
    Component explain();
    
    @NotNull
    static BooleanExplained ofTrue() {
        return BooleanExplainedImpl.TRUE;
    }
    
    @NotNull
    static BooleanExplained ofFalse(@NotNull Component explain) {
        return new BooleanExplainedImpl(false, explain);
    }
    
    class BooleanExplainedImpl implements BooleanExplained {
        private static final BooleanExplainedImpl TRUE = new BooleanExplainedImpl(true, Component.empty());
        
        private final boolean value;
        private final Component explain;
        
        BooleanExplainedImpl(final boolean value, @NotNull Component explain) {
            this.value = value;
            this.explain = explain;
        }
        
        @Override
        public boolean booleanValue() {
            return value;
        }
        
        @NotNull
        @Override
        public Component explain() {
            return explain;
        }
    }
    
}
