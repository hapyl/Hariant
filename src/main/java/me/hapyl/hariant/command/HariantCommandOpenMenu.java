package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.util.StringList;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.experience.MenuLevelling;
import me.hapyl.hariant.menu.Menu;
import me.hapyl.hariant.menu.MenuBattlegroundSelection;
import me.hapyl.hariant.menu.MenuPlayerProfile;
import me.hapyl.hariant.menu.MenuSettings;
import me.hapyl.hariant.menu.achievement.MenuAchievement;
import me.hapyl.hariant.menu.hero.AbstractMenuHero;
import me.hapyl.hariant.menu.hero.Category;
import me.hapyl.hariant.menu.hero.MenuHeroSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class HariantCommandOpenMenu extends HariantPlayerCommand {
    
    private static String COMMAND_NAME;
    
    public HariantCommandOpenMenu(@NotNull String name) {
        super(name, PlayerRank.DEFAULT);
        
        COMMAND_NAME = name.toLowerCase();
    }
    
    @Override
    public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        final Menus menus = args.get(0).toEnum(Menus.class);
        
        if (menus == null) {
            HariantLogger.error(player, Component.text("Unknown menu: `%s`!".formatted(args.get(0))));
            return;
        }
        
        menus.openMenu(player);
    }
    
    @Override
    public @NotNull List<String> tabComplete(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        if (args.length == 1) {
            return StringList.ofEnumConstantLowercaseNames(Menus.class);
        }
        
        return List.of();
    }
    
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
        
        ;
        
        private final Function<Player, Menu> supplier;
        private final ClickEvent<?> clickEvent;
        
        Menus(@NotNull Function<Player, Menu> supplier) {
            this.supplier = supplier;
            this.clickEvent = ClickEvent.runCommand(COMMAND_NAME + " " + this.name().toLowerCase());
        }
        
        public void openMenu(@NotNull Player player) {
            supplier.apply(player);
        }
        
        public @NotNull ClickEvent<?> createClickEvent() {
            return clickEvent;
        }
        
    }
    
}