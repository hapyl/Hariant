package me.hapyl.hariant.attribute.modifier;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface AttributeModifiable {
    
    // *-* Add Operations *-* //
    
    void addModifier(@NotNull AttributeModifier attributeModifier);
    
    default void addModifier(@NotNull Key key, int duration, @Nullable HariantEntity applier, @NotNull AttributeModifierAdderHandler handler) {
        final AttributeModifier modifier = new AttributeModifier(key, applier, duration);
        handler.handle(modifier);
        
        this.addModifier(modifier);
    }
    
    default void addModifier(@NotNull Key key, int duration, @NotNull AttributeModifierAdderHandler handler) {
        this.addModifier(key, duration, null, handler);
    }
    
    // *-* Remove Operations *-* //
    
    boolean removeModifier(@NotNull Key key);
    
    boolean removeModifiers(@NotNull Predicate<AttributeModifier> filter);
    
    // *-* Query Operations *-* //
    
    boolean hasModifier(@NotNull Key key);
    
    @NotNull
    List<? extends AttributeModifier> getModifiers();
    
    @NotNull
    Optional<AttributeModifier> getModifier(@NotNull Key key);
    
    @NotNull
    <M extends AttributeModifier> Optional<M> getModifier(@NotNull Class<M> modifierClass);
    
    @NotNull
    Stream<AttributeModifier> streamModifiers();
}
