package me.hapyl.hariant.entity.ticker;

public enum TickerType {
    
    INCREMENT {
        @Override
        public int increment() {
            return 1;
        }
    },
    
    DECREMENT {
        @Override
        public int increment() {
            return -1;
        }
    };
    
    public int increment() {
        return 0;
    }
    
}
