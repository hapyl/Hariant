package me.hapyl.hariant.shop.transaction;

import org.jetbrains.annotations.NotNull;

public class TransactionException extends RuntimeException {
    
    public TransactionException(@NotNull String message) {
        super(message);
    }
    
}