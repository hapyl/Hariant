package me.hapyl.hariant.team;

import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.menu.Menu;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MenuTeamSelection extends Menu {
    
    private static final int[] SLOTS = { 19, 29, 21, 31, 23, 33, 25 };
    
    private final TeamEntry teamEntry;
    
    public MenuTeamSelection(@NotNull Player player) {
        super(player, () -> Component.text("Team Selection"), ChestSize.SIZE_6);
        
        this.teamEntry = TeamEntry.create(player);
        this.openMenu();
    }
    
    @Override
    public void updateMenu() {
        final EnumTeam playerTeam = EnumTeam.getEntryTeam(teamEntry);
        final EnumTeam[] values = EnumTeam.values();
        
        for (int i = 0; i < values.length; i++) {
            final EnumTeam team = values[i];
            
            final ItemBuilder builder = team.createBuilder();
            
            builder.setAmount(Math.max(1, team.getPlayerCount()));
            builder.addLore();
            
            if (playerTeam == team) {
                builder.addLore(Component.text("This is your team!", Colors.SUCCESS));
            }
            else {
                builder.addLore(ButtonComponents.left("join"));
            }
            
            setItem(SLOTS[i], builder.asIcon(), PlayerMenuAction.of(player -> {
                team.addPlayer(Hariant.getPlayerProfile(player));
                this.broadcastUpdate();
            }));
        }
    }
    
}