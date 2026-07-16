package me.hapyl.hariant.shop.transaction;

import org.jetbrains.annotations.NotNull;

public interface TransactionResult {
    
    @NotNull Transaction transaction();
    
    void refund();
    
    static @NotNull TransactionResult create(@NotNull Transaction transaction, @NotNull Runnable refund) {
        return new TransactionResultImpl(transaction) {
            @Override
            public void refund() {
                refund.run();
            }
        };
    }
    
}
