package me.hapyl.hariant.shop.transaction;

import org.jetbrains.annotations.NotNull;

public abstract class TransactionResultImpl implements TransactionResult {
    
    private final Transaction transaction;
    
    TransactionResultImpl(@NotNull Transaction transaction) {
        this.transaction = transaction;
    }
    
    @Override
    public @NotNull Transaction transaction() {
        return transaction;
    }
    
    @Override
    public abstract void refund();
    
}
