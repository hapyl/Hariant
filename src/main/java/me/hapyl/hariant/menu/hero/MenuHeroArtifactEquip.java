package me.hapyl.hariant.menu.hero;

import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.inventory.item.artifact.ArtifactSlot;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifactInstance;
import me.hapyl.hariant.inventory.item.artifact.ArtifactSet;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MenuHeroArtifactEquip extends MenuHeroAbstract {
    
    private static final Map<ElementType, Icon> ELEMENTAL_BAR_ICONS = Map.of(
            ElementType.PHYSICAL, Icon.ofMaterial(Material.WHITE_STAINED_GLASS_PANE),
            ElementType.FIRE, Icon.ofMaterial(Material.ORANGE_STAINED_GLASS_PANE),
            ElementType.WATER, Icon.ofMaterial(Material.BLUE_STAINED_GLASS_PANE),
            ElementType.ICE, Icon.ofMaterial(Material.LIGHT_BLUE_STAINED_GLASS_PANE),
            ElementType.TOXIC, Icon.ofMaterial(Material.LIME_STAINED_GLASS_PANE),
            ElementType.ELECTRIC, Icon.ofMaterial(Material.YELLOW_STAINED_GLASS_PANE),
            ElementType.AETHER, Icon.ofMaterial(Material.PURPLE_STAINED_GLASS_PANE)
    );
    
    private static final Map<ArtifactSlot, int[]> ELEMENTAL_BAR_SLOT = Map.of(
            ArtifactSlot.SLOT_1, new int[] { 10, 28, 37 },
            ArtifactSlot.SLOT_2, new int[] { 12, 21, 39 },
            ArtifactSlot.SLOT_3, new int[] { 14, 23, 41 },
            ArtifactSlot.SLOT_4, new int[] { 16, 34, 43 }
    );
    
    private static final Icon NON_ELEMENTAL_BAR_ICON = Icon.ofMaterial(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
    
    private final HeroInstance heroInstance;
    
    public MenuHeroArtifactEquip(@NotNull Player player, @NotNull HeroInstance heroInstance) {
        super(player, heroInstance, Category.ARTIFACTS);
        
        this.heroInstance = heroInstance;
        this.openMenu();
    }
    
    @Override
    public void updateMenu() {
        super.updateMenu();
        
        for (ArtifactSlot artifactSlot : ArtifactSlot.values()) {
            final ItemArtifactInstance artifact = heroInstance.getArtifact(artifactSlot).orElse(null);
            final int inventorySlot = artifactSlot.getInventorySlot();
            
            if (artifact == null) {
                setItem(
                        inventorySlot,
                        new ItemBuilder(Material.GRAY_DYE)
                                .setName(Component.text("Not Equipped"))
                                .addLore()
                                .addWrappedLore(Component.text("No artifact is equipped at this slot, click the button below to equip an artifact!"))
                                .addLore()
                                .addLore(ButtonComponents.left("select artifact"))
                                .asIcon(),
                        PlayerMenuAction.builder()
                                        .left(player -> new MenuHeroArtifactSelect(player, heroInstance, artifactSlot, null))
                                        .build()
                );
            }
            else {
                setItem(
                        inventorySlot,
                        artifact.createBuilder()
                                .addLore()
                                .addLore(ButtonComponents.left("change artifact"))
                                .addLore(ButtonComponents.right("unequip"))
                                .asIcon(),
                        PlayerMenuAction.builder()
                                        .left(player -> new MenuHeroArtifactSelect(player, heroInstance, artifactSlot, artifact))
                                        .right(player -> {
                                            heroInstance.unsetArtifact(artifact);
                                            this.openMenu();
                                        })
                                        .build()
                );
            }
        }
        
        // Highlight active sets
        final Set<ArtifactSet> artifactSetsWithAtLeastTwoPieceCount
                = heroInstance.countArtifactSetPieces()
                              .entrySet()
                              .stream()
                              .filter(entry -> entry.getValue().isOrHigher(PieceCount.TWO_PIECE))
                              .map(Map.Entry::getKey)
                              .collect(Collectors.toSet());
        
        for (ArtifactSlot artifactSlot : ArtifactSlot.values()) {
            final ArtifactSet artifactSet = heroInstance.getArtifact(artifactSlot).map(ItemArtifactInstance::getArtifactSet).orElse(null);
            
            if (artifactSet == null) {
                continue;
            }
            
            if (artifactSetsWithAtLeastTwoPieceCount.contains(artifactSet)) {
                final ElementType effectiveElementType = artifactSet.getEffectiveElementType();
                final ItemStack barItem = effectiveElementType != null ? ELEMENTAL_BAR_ICONS.get(effectiveElementType).createIcon() : NON_ELEMENTAL_BAR_ICON.createIcon();
                
                for (int slot : ELEMENTAL_BAR_SLOT.get(artifactSlot)) {
                    this.setItem(slot, barItem);
                }
            }
        }
    }
    
}
