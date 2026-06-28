package me.hapyl.hariant.hero;

import me.hapyl.eterna.module.inventory.ItemStacks;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.HeadComponent;
import me.hapyl.hariant.entity.player.HariantPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.object.PlayerHeadObjectContents;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

// #norender
public class HeroEquipment implements HeadComponent {
    
    private static final String BASE_64_PATTERN = "{\"textures\" : {\"SKIN\" : {\"url\" : \"https://textures.minecraft.net/texture/%s\"}}}";
    
    private static final int INDEX_HELMET = 0;
    private static final int INDEX_CHEST_PLATE = 1;
    private static final int INDEX_LEGGINGS = 2;
    private static final int INDEX_BOOTS = 3;
    
    private final ItemStack[] equipment;
    
    private String headTexture;
    private Component headComponent;
    
    HeroEquipment() {
        this.equipment = new ItemStack[] {
                ItemStacks.empty(),
                ItemStacks.empty(),
                ItemStacks.empty(),
                ItemStacks.empty()
        };
    }
    
    @NotNull
    public String getHeadTexture() {
        return Objects.requireNonNull(headTexture, "Head texture not set!");
    }
    
    public void setHeadTexture(@NotNull String texture) {
        this.equipment[INDEX_HELMET] = ItemBuilder.playerHead(texture).setHideTooltip(true).asIcon();
        this.headTexture = texture;
        this.headComponent = encodeTexture(texture);
    }
    
    @NotNull
    @Override
    public Component asHeadComponent() {
        return Objects.requireNonNull(headComponent, "Head texture not set!");
    }
    
    @NotNull
    public ItemStack getHelmet() {
        return this.equipment[INDEX_HELMET];
    }
    
    @NotNull
    public ItemStack getChestPlate() {
        return this.equipment[INDEX_CHEST_PLATE];
    }
    
    @NotNull
    public ItemStack getLeggings() {
        return this.equipment[INDEX_LEGGINGS];
    }
    
    @NotNull
    public ItemStack getBoots() {
        return this.equipment[INDEX_BOOTS];
    }
    
    // *-* Chest Plate *-* //
    
    public void setChestPlate(@NotNull Material material, @Nullable TrimPattern trimPattern, @Nullable TrimMaterial trimMaterial) {
        this.equipment[INDEX_CHEST_PLATE] = createItem(material, null, trimPattern, trimMaterial);
    }
    
    public void setChestPlate(@NotNull Material material) {
        this.setChestPlate(material, null, null);
    }
    
    public void setChestPlate(int colorRed, int colorGreen, int colorBlue, @Nullable TrimPattern trimPattern, @Nullable TrimMaterial trimMaterial) {
        this.equipment[INDEX_CHEST_PLATE] = createItem(Material.LEATHER_CHESTPLATE, Color.fromRGB(colorRed, colorGreen, colorBlue), trimPattern, trimMaterial);
    }
    
    public void setChestPlate(int colorRed, int colorGreen, int colorBlue) {
        this.setChestPlate(colorRed, colorGreen, colorBlue, null, null);
    }
    
    // *-* Leggings *-* //
    
    public void setLeggings(@NotNull Material material, @Nullable TrimPattern trimPattern, @Nullable TrimMaterial trimMaterial) {
        this.equipment[INDEX_LEGGINGS] = createItem(material, null, trimPattern, trimMaterial);
    }
    
    public void setLeggings(@NotNull Material material) {
        this.setLeggings(material, null, null);
    }
    
    public void setLeggings(int colorRed, int colorGreen, int colorBlue, @Nullable TrimPattern trimPattern, @Nullable TrimMaterial trimMaterial) {
        this.equipment[INDEX_LEGGINGS] = createItem(Material.LEATHER_LEGGINGS, Color.fromRGB(colorRed, colorGreen, colorBlue), trimPattern, trimMaterial);
    }
    
    public void setLeggings(int colorRed, int colorGreen, int colorBlue) {
        this.setLeggings(colorRed, colorGreen, colorBlue, null, null);
    }
    
    // *-* Boots *-* //
    
    public void setBoots(@NotNull Material material, @Nullable TrimPattern trimPattern, @Nullable TrimMaterial trimMaterial) {
        this.equipment[INDEX_BOOTS] = createItem(material, null, trimPattern, trimMaterial);
    }
    
    public void setBoots(@NotNull Material material) {
        this.setBoots(material, null, null);
    }
    
    public void setBoots(int colorRed, int colorGreen, int colorBlue, @Nullable TrimPattern trimPattern, @Nullable TrimMaterial trimMaterial) {
        this.equipment[INDEX_BOOTS] = createItem(Material.LEATHER_BOOTS, Color.fromRGB(colorRed, colorGreen, colorBlue), trimPattern, trimMaterial);
    }
    
    public void setBoots(int colorRed, int colorGreen, int colorBlue) {
        this.setBoots(colorRed, colorGreen, colorBlue, null, null);
    }
    
    public void equip(@NotNull HariantPlayer player) {
        final PlayerInventory inventory = player.getInventory();
        
        inventory.setHelmet(this.getHelmet());
        inventory.setChestplate(this.getChestPlate());
        inventory.setLeggings(this.getLeggings());
        inventory.setBoots(this.getBoots());
        
        // For some reason we have to forcefully update the inventory ¯\_(ツ)_/¯
        player.getHandle().updateInventory();
    }
    
    @NotNull
    private static Component encodeTexture(@NotNull String texture) {
        final String base64 = Base64.getEncoder().encodeToString(BASE_64_PATTERN.formatted(texture).getBytes(StandardCharsets.UTF_8));
        
        return Component.object(
                ObjectContents.playerHead()
                              .profileProperty(PlayerHeadObjectContents.property("textures", base64, ""))
                              .build()
        ).color(Colors.WHITE);
    }
    
    @NotNull
    private static ItemStack createItem(@NotNull Material material, @Nullable Color dyeColor, @Nullable TrimPattern trimPattern, @Nullable TrimMaterial trimMaterial) {
        final ItemBuilder builder = new ItemBuilder(material);
        
        if (dyeColor != null) {
            builder.setLeatherArmorColor(dyeColor);
        }
        
        if (trimPattern != null && trimMaterial != null) {
            builder.setArmorTrim(trimPattern, trimMaterial);
        }
        
        return builder.setHideTooltip(true).asItemStack();
    }
    
}
