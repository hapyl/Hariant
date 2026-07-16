package me.hapyl.hariant.inventory.adder;

import me.hapyl.hariant.inventory.item.AbstractItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class AdderImpl<I extends AbstractItem, R> implements Adder<I, R> {
    
    private final @Nullable R result;
    private final @Nullable AdderError error;
    
    AdderImpl(@Nullable R result, @Nullable AdderError error) {
        this.result = result;
        this.error = error;
    }
    
    @Override
    public @NotNull Adder<I, R> onSuccess(@NotNull Consumer<R> resultConsumer) {
        if (result != null) {
            resultConsumer.accept(result);
        }
        
        return this;
    }
    
    @Override
    public @NotNull Adder<I, R> onError(@NotNull Consumer<AdderError> errorConsumer) {
        if (error != null) {
            errorConsumer.accept(error);
        }
        
        return this;
    }
    
}
