package me.hapyl.hariant.menu.hero;

import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.PlayerMenuTitle;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.menu.Menu;
import me.hapyl.hariant.menu.MenuReturn;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public class AbstractMenuHero extends Menu {
    
    protected final HeroInstance heroInstance;
    protected final Category category;
    
    public AbstractMenuHero(@NotNull Player player, @NotNull HeroInstance heroInstance, @NotNull Category category) {
        super(player, PlayerMenuTitle.create(heroInstance.getOrigin().getName(), category.getName()), ChestSize.SIZE_6);
        
        this.heroInstance = heroInstance;
        this.category = category;
    }
    
    @Override
    @OverridingMethodsMustInvokeSuper
    public void updateMenu() {
        // Set category items
        for (Category category : Category.values()) {
            final int slot = category.getSlot();
            final ItemBuilder builder = category.createBuilder();
            
            builder.addLore();
            
            if (this.category == category) {
                builder.addLore(Component.text("Currently selected!", Colors.SUCCESS));
                builder.glow();
            }
            else {
                builder.addLore(ButtonComponents.left("select!"));
                this.setAction(slot, PlayerMenuAction.builder().left(player -> category.createMenu(player, heroInstance)).build());
            }
            
            this.setItem(slot, builder.build());
        }
    }
    
    @Nullable
    @Override
    public MenuReturn menuReturn() {
        return MenuReturn.create(Component.text("Hero Selection"), () -> new MenuHeroSelection(player));
    }
    
}
