package me.hapyl.hariant.menu.hero;

import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.hero.HeroDirectory;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.menu.MenuPage;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

public class MenuHeroSelection extends MenuPage<Hero> {
    
    private final PlayerProfile profile;
    private final HeroDirectory heroDirectory;
    
    public MenuHeroSelection(@NotNull Player player) {
        super(player, () -> Component.text("Select Hero"));
        
        this.profile = Hariant.getPlayerProfile(player);
        this.heroDirectory = profile.getDatabase().heroDirectory;
        
        this.setContents(HeroRegistry.getRegistry().values());
        this.openMenu();
    }
    
    @NotNull
    @Override
    public ItemBuilder createBuilder(@NotNull Hero hero) {
        final HeroInstance heroInstance = heroDirectory.getHero(hero).orElse(null);
        
        // If hero instance doesn't exist, means player doesn't have that hero owned
        if (heroInstance == null) {
            return hero.createBuilder()
                       .setName(hero.getName().color(Colors.ERROR))
                       .addLore()
                       .addWrappedLore(Component.text("You do not own this hero!", Colors.ERROR))
                       .addLore()
                       .addLore(ButtonComponents.left("unlock"));
        }
        else {
            final ItemBuilder builder = heroInstance.createBuilder();
            
            builder.addLore();
            builder.addLore(heroDirectory.getSelectedHero().equals(hero) ? Component.text("Currently selected!", Colors.SUCCESS) : ButtonComponents.left("select"));
            builder.addLore(ButtonComponents.right("preview"));
            
            return builder;
        }
    }
    
    @Override
    public void onClick(@NotNull Hero hero, @NotNull ClickType clickType) {
        final HeroInstance heroInstance = heroDirectory.getHero(hero).orElse(null);
        
        if (heroInstance == null) {
            new MenuHeroUnlock(player, hero);
            return;
        }
        
        if (clickType.isLeftClick()) {
            heroDirectory.trySelectHero(player, heroInstance);
            this.openMenu();
        }
        else if (clickType.isRightClick()) {
            new MenuHeroProfile(player, heroInstance);
        }
        
        updateMenu();
    }
    
    @Override
    public void updateMenu() {
        super.updateMenu();
    }
    
}
