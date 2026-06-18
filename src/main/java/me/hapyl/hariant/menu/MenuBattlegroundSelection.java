package me.hapyl.hariant.menu;

import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.eterna.module.inventory.menu.pattern.SlotPattern;
import me.hapyl.eterna.module.inventory.menu.pattern.SlotPatternApplier;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.game.battleground.EnumBattleground;
import me.hapyl.hariant.lobby.MenuGameManagement;
import me.hapyl.hariant.profile.message.EnumMessageChannel;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MenuBattlegroundSelection extends Menu {
    
    public MenuBattlegroundSelection(@NotNull Player player) {
        super(player, () -> Component.text("Select Battleground"), ChestSize.SIZE_6);
        
        this.openMenu();
    }
    
    @Override
    public @Nullable MenuReturn menuReturn() {
        return MenuReturn.create(Component.text("Game Management"), () -> new MenuGameManagement(player));
    }
    
    @Override
    public void updateMenu() {
        final SlotPatternApplier applier = newSlotPatternApplier(SlotPattern.INNER_LEFT_TO_RIGHT, ChestSize.SIZE_2);
        
        for (EnumBattleground battleground : EnumBattleground.values()) {
            if (!battleground.isSelectable()) {
                continue;
            }
            
            applier.add(battleground.createItem(), PlayerMenuAction.of(player -> {
                if (battleground.isSelected()) {
                    HariantLogger.error(player, Component.text("This battleground is already selected!"));
                }
                else {
                    battleground.select();
                    
                    // Broadcast battleground change
                    HariantLogger.PREFIX_INFO.broadcastMessage(
                            Component.empty()
                                     .append(playerProfile.getNameFormatted())
                                     .append(Component.text(" selected ", Colors.SUCCESS))
                                     .append(battleground.getName().color(Colors.GOLD))
                                     .append(Component.text("!", Colors.SUCCESS))
                    );
                    
                    this.broadcastUpdate();
                }
            }));
        }
        
        applier.apply();
    }
    
}
