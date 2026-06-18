package me.hapyl.hariant.menu.hero;

import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.hero.HeroDirectory;
import me.hapyl.hariant.inventory.HariantInventory;
import me.hapyl.hariant.inventory.item.Resource;
import me.hapyl.hariant.inventory.item.ResourceRegistry;
import me.hapyl.hariant.inventory.item.resource.ResourceUnlocksHero;
import me.hapyl.hariant.menu.Menu;
import me.hapyl.hariant.shop.MenuTransaction;
import me.hapyl.hariant.shop.transaction.Transaction;
import me.hapyl.hariant.shop.transaction.TransactionException;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MenuHeroUnlock extends Menu {
    
    private final PlayerDatabase playerDatabase;
    private final Hero hero;
    
    public MenuHeroUnlock(@NotNull Player player, @NotNull Hero hero) {
        super(player, () -> Component.text("Unlock Hero"), ChestSize.SIZE_6);
        
        this.playerDatabase = Hariant.getPlayerDatabase(player);
        this.hero = hero;
        
        this.openMenu();
    }
    
    @Override
    public void updateMenu() {
        setItem(
                22,
                hero.createBuilder()
                    .addLore()
                    .addLore(Component.text("This is the hero you're about to unlock!", Colors.SUCCESS))
                    .asIcon()
        );
        
        this.setPurchaseButton(29, ResourceRegistry.HERO_RECRUIT_VOUCHER);
        this.setPurchaseButton(33, ResourceRegistry.RUBY);
    }
    
    private void setPurchaseButton(int slot, @NotNull Resource resource) {
        if (!(resource instanceof ResourceUnlocksHero resourceUnlocksHero)) {
            this.closeMenu();
            HariantLogger.error(
                    player,
                    Component.empty()
                             .append(Component.text("Failed to fetch the menu! "))
                             .append(resource.getName())
                             .append(Component.text(" cannot be used to unlock heroes!"))
            );
            return;
        }
        
        final HariantInventory inventory = playerDatabase.inventory;
        final int amount = inventory.getResource(resource);
        final int unlockAmount = resourceUnlocksHero.unlockAmount();
        final boolean hasEnoughResource = amount >= unlockAmount;
        
        final ItemBuilder builder = resource.createBuilder();
        builder.addLore();
        
        builder.addLore(Component.text("Resources", Colors.DEFAULT_COLOR));
        builder.addLore(Component.space().append(Components.makeComponentFractional(amount, unlockAmount)));
        builder.addLore();
        
        if (hasEnoughResource) {
            builder.addLore(ButtonComponents.left("unlock"));
            
            this.setItem(
                    slot,
                    builder.asIcon(),
                    new MenuTransaction() {
                        @Override
                        public @NotNull Transaction payment() {
                            return Transaction.withResources(Map.of(resource, unlockAmount));
                        }
                        
                        @Override
                        public void deliver(@NotNull Player player, @NotNull PlayerDatabase playerDatabase) throws TransactionException {
                            final HeroDirectory heroDirectory = playerDatabase.heroDirectory;
                            
                            if (heroDirectory.isOwned(hero)) {
                                throw new TransactionException("You already have this hero unlocked!");
                            }
                            
                            heroDirectory.createHero(hero);
                        }
                        
                        @Override
                        public void onSuccess(@NotNull Player player) {
                            new MenuHeroSelection(player);
                            
                            HariantLogger.success(player, Component.text("Successfully unlocked ").append(hero.getName()).append(Component.text("!")));
                            HariantLogger.sound(player, Sound.ENTITY_VILLAGER_YES, 1.0f);
                        }
                        
                        @Override
                        public void onError(@NotNull Player player, @NotNull TransactionException exception) {
                            closeMenu();
                        }
                    }
            );
        }
        else {
            builder.addLore(Component.text("You don't have enough resources!", Colors.ERROR));
            
            this.setItem(
                    slot,
                    builder.asIcon(),
                    PlayerMenuAction.of(player -> {
                        HariantLogger.error(player, Component.text("You don't have enough resources!"));
                        HariantLogger.sound(player, Sound.ENTITY_VILLAGER_NO, 1.0f);
                    })
            );
        }
    }
    
}