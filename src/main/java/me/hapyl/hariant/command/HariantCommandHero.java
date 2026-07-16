package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.util.StringList;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.hero.HeroDirectory;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.menu.hero.MenuHeroSelection;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class HariantCommandHero extends HariantPlayerCommand {
    
    HariantCommandHero(@NotNull String name) {
        super(name, PlayerRank.DEFAULT);
    }
    
    @Override
    public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        if (args.length == 0) {
            new MenuHeroSelection(player);
        }
        else {
            final Hero hero = args.get(0).toRegistryItem(HeroRegistry.getRegistry()).orElse(null);
            
            if (hero == null) {
                HariantLogger.error(player, Component.text("No such hero as `%s`!".formatted(args.get(0))));
                return;
            }
            
            final PlayerProfile profile = Hariant.getPlayerProfile(player);
            final HeroDirectory heroDirectory = profile.getDatabase().heroDirectory;
            final HeroInstance heroInstance = heroDirectory.getHero(hero).orElse(null);
            
            if (heroInstance == null) {
                HariantLogger.error(player, Component.text("You don't own this hero!"));
                return;
            }
            
            profile.setSelectedHero(heroInstance);
        }
    }
    
    @NotNull
    @Override
    public List<String> tabComplete(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        if (args.length == 1) {
            return StringList.ofRegistryKeys(HeroRegistry.getRegistry());
        }
        
        return List.of();
    }
    
}