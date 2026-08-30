package me.hapyl.hariant.menu;

import me.hapyl.hariant.command.HariantCommandMenu;
import me.hapyl.hariant.daily.MenuDaily;
import me.hapyl.hariant.experience.MenuLevelling;
import me.hapyl.hariant.menu.achievement.MenuAchievement;
import me.hapyl.hariant.menu.hero.AbstractMenuHero;
import me.hapyl.hariant.menu.hero.Category;
import me.hapyl.hariant.menu.hero.MenuHeroSelection;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public enum Menus {
    
    PROFILE(MenuPlayerProfile::new),
    ACHIEVEMENTS(MenuAchievement::new),
    HERO_SELECTION(MenuHeroSelection::new),
    HERO_PROFILE(player -> AbstractMenuHero.openMenu(player, Category.PROFILE)),
    HERO_TALENTS(player -> AbstractMenuHero.openMenu(player, Category.TALENTS)),
    HERO_ARTIFACTS(player -> AbstractMenuHero.openMenu(player, Category.ARTIFACTS)),
    SETTINGS(MenuSettings::new),
    LEVELLING(MenuLevelling::new),
    BATTLEGROUND(MenuBattlegroundSelection::new),
    DAILY(MenuDaily::new),
    
    ;
    
    private final Function<Player, Menu> supplier;
    private final ClickEvent<?> clickEvent;
    
    Menus(@NotNull Function<Player, Menu> supplier) {
        this.supplier = supplier;
        this.clickEvent = ClickEvent.runCommand(HariantCommandMenu.COMMAND_NAME + " " + this.name().toLowerCase());
    }
    
    public void openMenu(@NotNull Player player) {
        supplier.apply(player);
    }
    
    public @NotNull ClickEvent<?> createClickEvent() {
        return clickEvent;
    }
    
}
