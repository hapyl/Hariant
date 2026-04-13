package me.hapyl.hariant.menu.hero;

import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
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
        super(player, () -> Component.text("Select Hero"), ChestSize.SIZE_6);
        
        this.profile = Hariant.getPlayerProfile(player);
        this.heroDirectory = profile.getDatabase().hero;
        
        this.setContents(HeroRegistry.getRegistry().values());
        this.openMenu();
    }
    
    @NotNull
    @Override
    public ItemBuilder createBuilder(@NotNull Hero hero) {
        final HeroInstance heroInstance = heroDirectory.getHero(hero).orElse(null);
        
        // If hero instance doesn't exist, means player doesn't have that hero owned
        if (heroInstance == null) {
            // TODO @Apr 12, 2026 (xanyjl) -> Maybe add preview of the hero
            return ItemBuilder.playerHead(hero.getEquipment().getHeadTexture())
                              .setName(Component.text("???", Colors.ERROR))
                              .addLore()
                              .addWrappedLore(Component.text("You do not own this hero!", Colors.ERROR))
                              .addLore();
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
            HariantLogger.error(player, Component.text("You do not own this hero!"));
            return;
        }
        
        if (clickType.isLeftClick()) {
            if (heroDirectory.getSelectedHero().equals(hero)) {
                HariantLogger.error(player, Component.text("This hero is already selected!"));
                return;
            }
            
            heroDirectory.setSelectedHero(heroInstance);
            HariantLogger.success(player, Component.empty().append(Component.text("Selected ")).append(hero.getName()).append(Component.text("!")));
            
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
