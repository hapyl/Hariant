package me.hapyl.hariant.shop;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.shop.transaction.Transaction;
import me.hapyl.hariant.shop.transaction.TransactionException;
import me.hapyl.hariant.shop.transaction.TransactionResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ShopTransaction {
    
    private static final Style MESSAGE_STYLE = Style.style(Colors.GRAY);
    
    private transient final AtomicBoolean closed;
    
    public ShopTransaction() {
        this.closed = new AtomicBoolean(false);
    }
    
    public boolean closed() {
        return closed.get();
    }
    
    public abstract @NotNull Transaction payment();
    
    public abstract void deliver(@NotNull Player player, @NotNull PlayerDatabase playerDatabase) throws TransactionException;
    
    @EventLike
    public void onSuccess(@NotNull Player player) {
    }
    
    @EventLike
    public void onError(@NotNull Player player, @NotNull TransactionException exception) {
    }
    
    public final void process(@NotNull Player player) {
        if (this.closed.get()) {
            HariantLogger.error(player, Component.text("This transaction is already closed!"));
            return;
        }
        
        this.closed.set(true);
        
        final PlayerDatabase database = Hariant.getPlayerDatabase(player);
        final Transaction transaction = payment();
        
        HariantLogger.info(
                player,
                Component.empty()
                         .append(Component.text("Executing order...", MESSAGE_STYLE))
                         .appendSpace()
                         .append(Component.text("(%s)".formatted(transaction.getUuid()), Colors.DARK_GRAY))
        );
        
        // First attempt to process the payment
        try {
            HariantLogger.info(player, Component.text("Processing payment...", MESSAGE_STYLE));
            
            final TransactionResult result = transaction.process(player, database);
            
            // If the payment transaction succeeds, attempt to deliver the goods
            try {
                HariantLogger.info(player, Component.text("Delivering goods...", MESSAGE_STYLE));
                
                this.deliver(player, database);
                this.onSuccess(player);
            }
            catch (TransactionException deliveryException) {
                // If delivery failed, refund the payment
                result.refund();
                
                this.onError0(player, deliveryException);
            }
        }
        catch (TransactionException paymentException) {
            this.onError0(player, paymentException);
        }
    }
    
    private void onError0(@NotNull Player player, @NotNull TransactionException exception) {
        this.onError(player, exception);
        
        HariantLogger.error(player, Component.text("Transaction failed!", Colors.SEVERE));
        HariantLogger.error(player, Component.text(exception.getMessage(), Colors.ERROR));
    }
    
}