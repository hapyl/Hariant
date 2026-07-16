package me.hapyl.hariant.inventory.adder;

import me.hapyl.hariant.inventory.item.AbstractItem;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface Adder<I extends AbstractItem, R> {

    @NotNull Adder<I, R> onSuccess(@NotNull Consumer<R> resultConsumer);
    
    @NotNull Adder<I, R> onError(@NotNull Consumer<AdderError> errorConsumer);
    
    static <I extends AbstractItem, R> @NotNull Adder<I, R> ofResult(@NotNull R result) {
        return new AdderImpl<>(result, null);
    }
    
    static <I extends AbstractItem, R> @NotNull Adder<I, R> ofError(@NotNull AdderError error) {
        return new AdderImpl<>(null, error);
    }
    
}
