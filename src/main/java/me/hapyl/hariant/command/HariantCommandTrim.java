package me.hapyl.hariant.command;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.command.completer.CompleterMethod;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.rank.PlayerRank;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class HariantCommandTrim extends HariantPlayerCommand {
    
    private static final Registry<TrimPattern> PATTERN_REGISTRY = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN);
    private static final Registry<TrimMaterial> MATERIAL_REGISTRY = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL);
    
    private static final List<String> PATTERN_VALUES = PATTERN_REGISTRY.keyStream().map(NamespacedKey::value).toList();
    private static final List<String> MATERIAL_VALUES = MATERIAL_REGISTRY.keyStream().map(NamespacedKey::value).toList();
    
    public HariantCommandTrim(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        // trim (pattern) (material)
        final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        final ItemMeta itemMeta = itemInMainHand.getItemMeta();
        
        if (!(itemMeta instanceof ArmorMeta armorMeta)) {
            HariantLogger.error(player, Component.text("This item does not support trims!"));
            return;
        }
        
        
        final ArmorTrim trim = armorMeta.getTrim();
        
        if (args.length == 2) {
            final TrimPattern trimPattern = stringToPattern(args.get(0).toString()).orElse(trim != null ? trim.getPattern() : TrimPattern.BOLT);
            final TrimMaterial trimMaterial = stringToMaterial(args.get(1).toString()).orElse(trim != null ? trim.getMaterial() : TrimMaterial.AMETHYST);
            
            armorMeta.setTrim(new ArmorTrim(trimMaterial, trimPattern));
            itemInMainHand.setItemMeta(armorMeta);
            
            HariantLogger.success(
                    player,
                    Component.empty()
                             .append(Component.text("Set trim to "))
                             .append(trimPattern.description().color(Colors.GOLD))
                             .append(Component.text(" / "))
                             .append(trimMaterial.description())
                             .append(Component.text("!"))
            );
            return;
        }
        
        HariantLogger.error(player, Component.text("Invalid usage!"));
    }
    
    @Override
    public @NotNull CompleterMethod completerMethod() {
        return CompleterMethod.CONTAINS;
    }
    
    @Override
    public @NotNull List<String> tabComplete(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        if (args.length == 1) {
            return PATTERN_VALUES;
        }
        else if (args.length == 2) {
            return MATERIAL_VALUES;
        }
        
        return super.tabComplete(player, args, playerRank);
    }
    
    private static @NotNull Optional<TrimPattern> stringToPattern(@NotNull String string) {
        return Optional.ofNullable(PATTERN_REGISTRY.get(NamespacedKey.minecraft(string.toLowerCase())));
    }
    
    private static @NotNull Optional<TrimMaterial> stringToMaterial(@NotNull String string) {
        return Optional.ofNullable(MATERIAL_REGISTRY.get(NamespacedKey.minecraft(string.toLowerCase())));
    }
    
}