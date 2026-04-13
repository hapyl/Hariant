package me.hapyl.hariant.talent;

import org.jetbrains.annotations.NotNull;

public class TalentContextImpl implements TalentContext {
    
    private final Object object;
    
    TalentContextImpl(@NotNull Object object) {
        this.object = object;
    }
    
    @Override
    @NotNull
    public <T> T retrieve(@NotNull Class<T> clazz) {
        if (clazz.isInstance(object)) {
            return clazz.cast(object);
        }
        
        throw new ClassCastException("Expected `%s`, got `%s`!".formatted(object.getClass().getSimpleName(), clazz.getSimpleName()));
    }
    
}
