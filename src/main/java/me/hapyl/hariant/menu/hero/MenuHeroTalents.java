package me.hapyl.hariant.menu.hero;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.menu.Menu;
import me.hapyl.hariant.menu.MenuReturn;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentIndex;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MenuHeroTalents extends MenuHeroAbstract {
    
    private final Set<TalentIndex> showDetails;
    
    public MenuHeroTalents(@NotNull Player player, @NotNull HeroInstance heroInstance) {
        super(player, heroInstance, Category.TALENTS);
        
        this.showDetails = Sets.newHashSet();
        this.openMenu();
    }
    
    @Override
    public void updateMenu() {
        super.updateMenu();
        
        final Hero hero = heroInstance.getOrigin();
        
        for (TalentIndex talentIndex : TalentIndex.values()) {
            final Talent talent = hero.getTalent(talentIndex);
            final boolean details = showDetails.contains(talentIndex);
            
            final ItemBuilder builder = details
                                        ? talent.createDetailsBuilder()
                                                .addLore()
                                                .addLore(ButtonComponents.left("hide details"))
                                        : talent.createBuilder()
                                                .addLore()
                                                .addLore(ButtonComponents.left("show details"));
            
            final PlayerMenuAction.Builder actionBuilder = PlayerMenuAction.builder();
            
            actionBuilder.left(player -> {
                if (details) {
                    showDetails.remove(talentIndex);
                }
                else {
                    showDetails.add(talentIndex);
                }
                
                this.openMenu();
            });
            
            // Handle sub menu
            if (talent instanceof SubMenuHandler subMenuHandler) {
                builder.addLore(ButtonComponents.right(subMenuHandler.subMenuAction()));
                actionBuilder.right(player -> subMenuHandler.openSubMenu(player, this));
            }
            
            this.setItem(
                    talentIndex.getSlotMenu(),
                    builder.asIcon(),
                    actionBuilder.build()
            );
        }
    }
    
    public abstract static class SubMenu extends Menu {
        
        private final MenuReturn returnMenu;
        
        public SubMenu(@NotNull Player player, @NotNull Component title, @NotNull Menu returnMenu) {
            super(player, () -> title, ChestSize.SIZE_5);
            
            this.returnMenu = MenuReturn.create(returnMenu.getTitle().asComponent(), () -> returnMenu);
        }
        
        @Override
        @NotNull
        public MenuReturn menuReturn() {
            return returnMenu;
        }
    }
    
    public interface SubMenuHandler {
        
        @NotNull
        String subMenuAction();
        
        void openSubMenu(@NotNull Player player, @NotNull Menu menu);
        
    }
    
}
