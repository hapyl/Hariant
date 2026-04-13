package me.hapyl.hariant.menu;

import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.eterna.module.inventory.menu.pattern.SlotPattern;
import me.hapyl.eterna.module.inventory.menu.pattern.SlotPatternApplier;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.game.battleground.EnumBattleground;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MenuBattlegroundSelection extends Menu {
    public MenuBattlegroundSelection(@NotNull Player player) {
        super(player, () -> Component.text("Select Battleground"), ChestSize.SIZE_6);
        
        this.openMenu();
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
                    this.broadcastUpdate();
                }
            }));
        }
        
        applier.apply();
    }
    
}
