package me.hapyl.hariant.menu.hero;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.eterna.module.util.Compute;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.hero.ArtifactLoadouts;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.inventory.item.artifact.ArtifactSlot;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifactInstance;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSet;
import me.hapyl.hariant.menu.artifact.MenuArtifactLoadouts;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

public class MenuHeroArtifactEquip extends AbstractMenuHero {
    
    private static final Map<ElementType, ItemStack> ELEMENTAL_BAR_ICONS = Map.of(
            ElementType.PHYSICAL, Icon.createIcon(Material.WHITE_STAINED_GLASS_PANE),
            ElementType.FIRE, Icon.createIcon(Material.ORANGE_STAINED_GLASS_PANE),
            ElementType.WATER, Icon.createIcon(Material.BLUE_STAINED_GLASS_PANE),
            ElementType.ICE, Icon.createIcon(Material.LIGHT_BLUE_STAINED_GLASS_PANE),
            ElementType.TOXIC, Icon.createIcon(Material.LIME_STAINED_GLASS_PANE),
            ElementType.ELECTRIC, Icon.createIcon(Material.YELLOW_STAINED_GLASS_PANE),
            ElementType.AETHER, Icon.createIcon(Material.PURPLE_STAINED_GLASS_PANE)
    );
    
    private static final ItemStack NON_ELEMENTAL_BAR_ICON = Icon.createIcon(Material.GRAY_STAINED_GLASS_PANE);
    
    private static final Map<ArtifactSlot, int[]> ELEMENTAL_BAR_SLOT = Map.of(
            ArtifactSlot.SLOT_1, new int[] { 10, 28, 37 },
            ArtifactSlot.SLOT_2, new int[] { 12, 21, 39 },
            ArtifactSlot.SLOT_3, new int[] { 14, 23, 41 },
            ArtifactSlot.SLOT_4, new int[] { 16, 34, 43 }
    );
    
    public MenuHeroArtifactEquip(@NotNull Player player, @NotNull HeroInstance heroInstance) {
        super(player, heroInstance, Category.ARTIFACTS);
        
        this.openMenu();
    }
    
    @Override
    public void updateMenu() {
        super.updateMenu();
        
        final Map<ArtifactSet, Set<ArtifactSlot>> seenArtifactSetsOnSlots = Maps.newHashMap();
        
        // Display artifacts
        for (ArtifactSlot artifactSlot : ArtifactSlot.values()) {
            final ItemArtifactInstance artifact = heroInstance.getArtifact(artifactSlot).orElse(null);
            final int inventorySlot = artifactSlot.getSlot();
            
            if (artifact == null) {
                final int slotNumber = artifactSlot.ordinal() + 1;
                
                setItem(
                        inventorySlot,
                        new ItemBuilder(Material.GRAY_DYE)
                                .setAmount(slotNumber)
                                .setName(Component.text("Artifact Slot " + slotNumber))
                                .addLore()
                                .addWrappedLore(Component.text("No artifact is equipped at this slot, click the button below to equip an artifact!"))
                                .addLore()
                                .addLore(ButtonComponents.left("select artifact"))
                                .asIcon(),
                        PlayerMenuAction.builder()
                                        .left(player -> new MenuHeroArtifactSelection(player, heroInstance, artifactSlot, null))
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
                                        .left(player -> new MenuHeroArtifactSelection(player, heroInstance, artifactSlot, artifact))
                                        .right(player -> {
                                            heroInstance.unsetArtifact(artifact);
                                            this.openMenu();
                                        })
                                        .build()
                );
                
                // Mark slot as seem for the artifact set
                seenArtifactSetsOnSlots.compute(artifact.getArtifactSet(), Compute.setAdd(artifactSlot));
            }
        }
        
        // Highlight artifacts sets
        seenArtifactSetsOnSlots.forEach((artifactSet, slots) -> {
            final ElementType effectiveElementType = artifactSet.getEffectiveElementType();
            final ItemStack highlightItem = effectiveElementType != null ? ELEMENTAL_BAR_ICONS.get(effectiveElementType) : NON_ELEMENTAL_BAR_ICON;
            
            // Determine how many slots to highlight
            final int numberOfArtifactsEquipped = slots.size();
            final int firstEffectPiece = artifactSet.firstEffectPiece();
            final int lastEffectPiece = artifactSet.lastEffectPiece();
            
            final int toHighlight = numberOfArtifactsEquipped >= lastEffectPiece
                                    ? lastEffectPiece
                                    : numberOfArtifactsEquipped >= firstEffectPiece
                                      ? firstEffectPiece
                                      : 0;
            
            slots.stream()
                 .sorted(Comparator.comparingInt(ArtifactSlot::getSlot))
                 .limit(toHighlight)
                 .forEach(artifactSlot -> {
                     for (int slot : ELEMENTAL_BAR_SLOT.get(artifactSlot)) {
                         setItem(slot, highlightItem);
                     }
                 });
        });
        
        // Unselect all
        final boolean empty = seenArtifactSetsOnSlots.isEmpty();
        
        setFooter(
                2,
                new ItemBuilder(empty ? Material.BUCKET : Material.LAVA_BUCKET)
                        .setName(Component.text("Unselect All", Colors.RED))
                        .addLore()
                        .addWrappedLore(Component.text("Unselects all artifacts currently equipped by this hero."))
                        .addLore()
                        .addLore(empty ? Component.text("No artifacts equipped!", Colors.RED) : ButtonComponents.left("unselect"))
                        .asIcon(),
                PlayerMenuAction.of(player -> {
                    if (empty) {
                        return;
                    }
                    
                    this.heroInstance.unsetArtifacts();
                    this.openMenu();
                    
                    player.playSound(player, Sound.ITEM_ARMOR_EQUIP_CHAIN, 3, 0.0f);
                })
        );
        
        // Artifact loadouts
        setFooter(
                6,
                new ItemBuilder(Material.ARMOR_STAND)
                        .setName(Component.text("Artifact Loadouts"))
                        .addLore()
                        .addWrappedLore(Component.text("Quickly equip artifacts from previously defined sets."))
                        .addLore()
                        .addLore(ButtonComponents.left("open loadouts"))
                        .asIcon(),
                PlayerMenuAction.of(player -> new MenuArtifactLoadouts(player, heroInstance, MenuArtifactLoadouts.ButtonHandler.equip()))
        );
        
        // Save loadout button
        final long numberOfArtifactEquipped = seenArtifactSetsOnSlots.values().stream().mapToLong(Set::size).sum();
        
        // If artifacts are equipped on each slot, add a "Save Loadout" button
        if (numberOfArtifactEquipped == ArtifactSlot.LENGTH) {
            // Make sure there isn't identical loadout
            final ArtifactLoadouts artifactLoadouts = heroInstance.getArtifactLoadouts();
            final @NotNull ItemArtifactInstance[] artifacts = heroInstance.artifactsAsArray();
            
            if (!artifactLoadouts.doesIdenticalExist(artifacts)) {
                setFooter(
                        7,
                        new ItemBuilder(Material.HONEYCOMB)
                                .setName(Component.text("Save to Loadout"))
                                .addLore()
                                .addWrappedLore(Component.text("Saves this artifact configuration to a loadout that can be equipped anytime with a click of a button."))
                                .addLore()
                                .addLore(ButtonComponents.left("save"))
                                .glow()
                                .asIcon(),
                        PlayerMenuAction.of(player -> {
                            new MenuArtifactLoadouts(player, heroInstance, MenuArtifactLoadouts.ButtonHandler.save(artifacts));
                        })
                );
            }
            
        }
      
    }
    
}