package me.hapyl.hariant.menu.hero;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.menu.Menu;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.SlotBound;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public enum Category implements SlotBound, Icon, Named, Described {
    
    PROFILE(
            2,
            Icon.ofMaterial(Material.FLOW_BANNER_PATTERN),
            Component.text("Profile"),
            Component.text("Shows the profile of the hero, including archetypes and detailed stats."),
            MenuHeroProfile::new
    ),
    
    TALENTS(
            4,
            Icon.ofMaterial(Material.CREEPER_BANNER_PATTERN),
            Component.text("Talents"),
            Component.text("Shows the talent descriptions and details."),
            MenuHeroTalents::new
    ),
    
    ARTIFACTS(
            6,
            Icon.ofMaterial(Material.FLOWER_BANNER_PATTERN),
            Component.text("Artifacts"),
            Component.text("Shows the artifacts equipped by this hero."),
            MenuHeroArtifactEquip::new
    );
    
    private final int slot;
    private final Icon icon;
    private final Component name;
    private final Component description;
    private final BiFunction<Player, HeroInstance, Menu> menuSupplier;
    
    Category(int slot, @NotNull Icon icon, @NotNull Component name, @NotNull Component description, @NotNull BiFunction<Player, HeroInstance, Menu> menuSupplier) {
        this.slot = slot;
        this.icon = icon;
        this.name = name;
        this.description = description;
        this.menuSupplier = menuSupplier;
    }
    
    @Override
    public int getSlot() {
        return slot;
    }
    
    @Override
    @NotNull
    public ItemBuilder createBuilder() {
        return icon.createBuilder()
                   .setName(name)
                   .addLore()
                   .addWrappedLore(description);
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public Component getDescription() {
        return description;
    }
    
    @NotNull
    public Menu createMenu(@NotNull Player player, @NotNull HeroInstance instance) {
        return this.menuSupplier.apply(player, instance);
    }
    
}
