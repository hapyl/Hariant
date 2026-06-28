package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.rank.PlayerRank;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ColorableArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class HariantCommandColor extends HariantPlayerCommand {
    public HariantCommandColor(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        final String colorString = args.get(0).toString().replace("#", "");
        
        final Color color;
        
        try {
            color = Color.fromRGB(Integer.parseInt(colorString, 16));
        }
        catch (Exception ex) {
            HariantLogger.error(player, Component.text("Invalid color string!"));
            return;
        }
        
        
        final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        final ItemMeta itemMeta = itemInMainHand.getItemMeta();
        
        if (!(itemMeta instanceof ColorableArmorMeta colorableArmorMeta)) {
            HariantLogger.error(player, Component.text("This item cannot be colored!"));
            return;
        }
        
        colorableArmorMeta.setColor(color);
        itemInMainHand.setItemMeta(colorableArmorMeta);
        
        HariantLogger.success(player, Component.text("Set item color to #%s!".formatted(colorString)));
    }
}
