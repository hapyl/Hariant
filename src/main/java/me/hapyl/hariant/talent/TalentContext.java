package me.hapyl.hariant.talent;

import org.jetbrains.annotations.NotNull;

public interface TalentContext {
    
    @NotNull
    <T> T retrieve(@NotNull Class<T> clazz);
    
    @NotNull
    static TalentContext empty() {
        class Holder {
            private static final TalentContext EMPTY = new TalentContextImpl(new Object());
        }
        
        return Holder.EMPTY;
    }
    
    @NotNull
    static TalentContext create(@NotNull Object object) {
        return new TalentContextImpl(object);
    }
    
}
