package me.hapyl.hariant.menu;

import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.team.MenuTeamSelection;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MenuGameManagement extends Menu {
    
    public MenuGameManagement(@NotNull Player player) {
        super(player, () -> Component.text("Lobby Management"), ChestSize.SIZE_6);
        
        this.openMenu();
    }
    
    @Override
    public void updateMenu() {
        
        setItem(
                20,
                new ItemBuilder(Material.MAP)
                        .setName(Component.text("Battleground"))
                        .addLore()
                        .addWrappedLore(
                                Component.empty()
                                         .append(Component.text("Select a battleground to fight on."))
                                         .appendNewline()
                                         .appendNewline()
                                         .append(Component.text("Different battlegrounds offer unique ways to play the game and unique rewards!"))
                        )
                        .addLore()
                        .addLore(Component.text("Current Battleground", Colors.ORANGE))
                        .addLore(Component.text(" ").append(Hariant.getSelectedBattleground().getName()))
                        .addLore()
                        .addLore(ButtonComponents.left("change"))
                        .asIcon(),
                PlayerMenuAction.of(MenuBattlegroundSelection::new)
        );
        
        setItem(
                31,
                new ItemBuilder(Material.ITEM_FRAME)
                        .setName(Component.text("Game Type"))
                        .asIcon()
        );
        
        final EnumTeam playerTeam = profile.getTeam();
        
        setItem(
                24,
                playerTeam.createBuilder()
                          .addLore()
                          .addLore(ButtonComponents.left("open team selection"))
                          .asIcon(),
                PlayerMenuAction.of(MenuTeamSelection::new)
        );
    }
    
}