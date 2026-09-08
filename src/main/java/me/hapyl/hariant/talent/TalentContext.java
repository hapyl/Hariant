package me.hapyl.hariant.talent;

import org.jetbrains.annotations.NotNull;

public interface TalentContext {
    
    <T> @NotNull T retrieve(@NotNull Class<T> clazz);
    
    static @NotNull TalentContext empty() {
        class Holder {
            private static final TalentContext EMPTY = new TalentContextImpl(new Object());
        }
        
        return Holder.EMPTY;
    }
    
    static @NotNull TalentContext create(@NotNull Object object) {
        return new TalentContextImpl(object);
    }
    
}
