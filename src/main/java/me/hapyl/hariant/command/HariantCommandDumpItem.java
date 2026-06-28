package me.hapyl.hariant.command;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.text.Capitalizable;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.rank.PlayerRank;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HariantCommandDumpItem extends HariantPlayerCommand {
    
    private static final Pattern TEXTURE_PATTERN = Pattern.compile("https?://textures\\.minecraft\\.net/texture/([a-fA-F0-9]+)");
    
    private static final Registry<TrimPattern> PATTERN_KEY = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN);
    private static final Registry<TrimMaterial> MATERIAL_KEY = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL);
    
    private static final NamespacedKey NULL_KEY = NamespacedKey.minecraft("null");
    
    public HariantCommandDumpItem(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        final ItemStack itemStack = player.getInventory().getItemInMainHand();
        final ItemMeta itemMeta = itemStack.getItemMeta();
        
        final StringBuilder commandBuilder = new StringBuilder();
        final TextComponent.Builder componentBuilder = Component.text();
        
        final Material itemType = itemStack.getType();
        
        // Player head special case
        if (itemMeta instanceof SkullMeta skullMeta) {
            final PlayerProfile profile = skullMeta.getPlayerProfile();
            
            if (profile == null) {
                HariantLogger.error(player, Component.text("There is no texture applied to this player's head!"));
                return;
            }
            
            final PlayerTextures textures = profile.getTextures();
            final URL skin = textures.getSkin();
            
            if (skin == null) {
                HariantLogger.error(player, Component.text("There isn't a skin somehow!?"));
                return;
            }
            
            final String urlString = skin.toString();
            final Matcher matcher = TEXTURE_PATTERN.matcher(urlString);
            final String texture = matcher.find() ? matcher.group(1) : urlString;
            
            commandBuilder.append(texture);
            componentBuilder.append(
                    Component.text("Skin Texture: ").color(Colors.DARK_GREEN),
                    Component.text(texture).color(Colors.GREEN)
            );
        }
        // Else handle color and/or trim
        else {
            Color armorColor;
            ArmorTrim armorTrim;
            
            if (itemMeta instanceof LeatherArmorMeta colorMeta) {
                armorColor = colorMeta.getColor();
                final int red = armorColor.getRed();
                final int green = armorColor.getGreen();
                final int blue = armorColor.getBlue();
                
                commandBuilder.append("%s, %s, %s".formatted(red, green, blue));
                componentBuilder.append(
                        Component.text("Color: ").color(Colors.GREEN),
                        Component.text("⬛⬛⬛").color(TextColor.color(armorColor.asRGB()))
                );
            }
            // If not leather armor, use material
            else {
                final String materialName = itemType.name();
                
                commandBuilder.append("Material.%s".formatted(materialName));
                componentBuilder.append(
                        Component.text("Material: ").color(Colors.DARK_GREEN),
                        Component.text(Capitalizable.capitalize(itemType)).color(Colors.GREEN)
                );
            }
            
            if (itemMeta instanceof ArmorMeta armorMeta) {
                armorTrim = armorMeta.getTrim();
                
                if (armorTrim != null) {
                    final TrimPattern pattern = armorTrim.getPattern();
                    final TrimMaterial material = armorTrim.getMaterial();
                    
                    final String patternKeyString = Objects.requireNonNullElse(PATTERN_KEY.getKey(pattern), NULL_KEY).value();
                    final String materialKeyString = Objects.requireNonNullElse(MATERIAL_KEY.getKey(material), NULL_KEY).value();
                    
                    // Add commas for color/material
                    commandBuilder.append(", ");
                    componentBuilder.append(Component.text(", ").color(Colors.GRAY));
                    
                    // Append trim
                    commandBuilder.append("TrimPattern.%s, TrimMaterial.%s".formatted(patternKeyString.toUpperCase(), materialKeyString.toUpperCase()));
                    
                    componentBuilder.append(
                            Component.empty()
                                     .append(Component.text("Trim: ", Colors.AQUA))
                                     .append(Component.text(Capitalizable.capitalize(patternKeyString), Colors.GOLD))
                                     .appendSpace()
                                     .append(Component.text(Capitalizable.capitalize(materialKeyString), material.description().style()))
                    );
                }
            }
        }
        
        componentBuilder.append(
                Component.empty()
                         .append(Component.text("  "))
                         .append(Component.text("COPY", Colors.GOLD, TextDecoration.BOLD, TextDecoration.UNDERLINED))
                         .hoverEvent(Component.text("Click to copy!").color(Colors.YELLOW))
                         .clickEvent(ClickEvent.suggestCommand(commandBuilder.toString()))
        );
        
        player.sendMessage(componentBuilder);
    }
    
}