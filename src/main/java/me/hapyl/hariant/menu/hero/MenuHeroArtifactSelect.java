package me.hapyl.hariant.menu.hero;

import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.PlayerMenuTitle;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.inventory.item.artifact.ArtifactSlot;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifactInstance;
import me.hapyl.hariant.menu.MenuPage;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class MenuHeroArtifactSelect extends MenuPage<ItemArtifactInstance> {
    
    private final HeroInstance heroInstance;
    private final ArtifactSlot artifactSlot;
    
    @Nullable
    private final ItemArtifactInstance currentArtifact;
    
    public MenuHeroArtifactSelect(@NotNull Player player, @NotNull HeroInstance heroInstance, @NotNull ArtifactSlot artifactSlot, @Nullable ItemArtifactInstance currentArtifact) {
        super(player, PlayerMenuTitle.create(Component.text("Select Artifact"), Component.text(artifactSlot.toString())), ChestSize.SIZE_6);
        
        this.heroInstance = heroInstance;
        this.artifactSlot = artifactSlot;
        this.currentArtifact = currentArtifact;
        
        this.setContents(Hariant.getPlayerDatabase(player).inventory.getItemsByClass(
                ItemArtifactInstance.class,
                Predicate.not(ItemArtifactInstance::isOwned)
        ));
        this.openMenu();
    }
    
    @NotNull
    @Override
    public ItemBuilder createBuilder(@NotNull ItemArtifactInstance artifactInstance) {
        return artifactInstance.createBuilder()
                               .addLore()
                               .addLore(ButtonComponents.left("equip"));
    }
    
    @Override
    public void onClick(@NotNull ItemArtifactInstance artifactInstance, @NotNull ClickType clickType) {
        this.heroInstance.setArtifact(artifactSlot, artifactInstance);
        
        new MenuHeroArtifactEquip(player, heroInstance);
    }
    
    @Override
    public void updateMenu() {
        super.updateMenu();
        
        this.setReturnButton(Component.text("Artifact Equip"), player -> Category.ARTIFACTS.createMenu(player, heroInstance));
    }
    
}
