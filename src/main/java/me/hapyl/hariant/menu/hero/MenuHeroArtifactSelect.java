package me.hapyl.hariant.menu.hero;

import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.PlayerMenuTitle;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.eterna.module.inventory.menu.pattern.SlotPattern;
import me.hapyl.eterna.module.inventory.menu.pattern.SlotPatternApplier;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.inventory.item.ItemRegistry;
import me.hapyl.hariant.inventory.item.artifact.ArtifactSlot;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifact;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifactInstance;
import me.hapyl.hariant.menu.Menu;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class MenuHeroArtifactSelect extends Menu {
    
    private final HeroInstance heroInstance;
    private final ArtifactSlot artifactSlot;
    
    @Nullable
    private final ItemArtifactInstance currentArtifact;
    
    public MenuHeroArtifactSelect(@NotNull Player player, @NotNull HeroInstance heroInstance, @NotNull ArtifactSlot artifactSlot, @Nullable ItemArtifactInstance currentArtifact) {
        super(player, PlayerMenuTitle.create(Component.text("Select Artifact"), Component.text(artifactSlot.toString())), ChestSize.SIZE_6);
        
        this.heroInstance = heroInstance;
        this.artifactSlot = artifactSlot;
        this.currentArtifact = currentArtifact;
        
        this.openMenu();
    }
    
    @Override
    public void updateMenu() {
        this.setReturnButton(Component.text("Artifact Equip"), player -> Category.ARTIFACTS.createMenu(player, heroInstance));
        
        final SlotPatternApplier slotPatternApplier = newSlotPatternApplier(SlotPattern.INNER_LEFT_TO_RIGHT, ChestSize.SIZE_2);
        final PlayerDatabase playerDatabase = Hariant.getPlayerDatabase(player);
        
        ItemRegistry.streamOfType(ItemArtifact.class)
                    .forEach(itemArtifact -> {
                        final ItemBuilder builder = itemArtifact.createBuilder();
                        builder.addLore();
                        
                        final List<ItemArtifactInstance> totalArtifacts = playerDatabase.inventory.getItemsByClass(
                                ItemArtifactInstance.class,
                                artifactInstance -> artifactInstance.getOrigin().equals(itemArtifact)
                        );
                        
                        final List<ItemArtifactInstance> availableArtifacts = totalArtifacts.stream()
                                                                                            .filter(Predicate.not(ItemArtifactInstance::isOwned))
                                                                                            .toList();
                        
                        builder.setAmount(availableArtifacts.size());
                        
                        if (totalArtifacts.isEmpty()) {
                            builder.addLore(Component.text("You don't own any of these artifacts!", Colors.ERROR));
                            
                            slotPatternApplier.add(
                                    builder.asIcon(),
                                    PlayerMenuAction.of(player -> {
                                        HariantLogger.error(player, Component.text("You don't own any of these the artifacts!"));
                                        HariantLogger.sound(player, Sound.ENTITY_VILLAGER_NO, 1.0f);
                                    })
                            );
                        }
                        else {
                            builder.addLore(
                                    Component.empty()
                                             .append(Component.text("You have "))
                                             .append(Components.makeComponentFractional(availableArtifacts.size(), totalArtifacts.size()))
                                             .append(Component.text(" available artifacts!"))
                            );
                            builder.addLore();
                            
                            if (availableArtifacts.isEmpty()) {
                                builder.addLore(Component.text("There aren't any available artifacts!", Colors.ERROR));
                                
                                slotPatternApplier.add(
                                        builder.asIcon(),
                                        PlayerMenuAction.of(player -> {
                                            HariantLogger.error(player, Component.text("You don't have any available artifacts!"));
                                            HariantLogger.sound(player, Sound.ENTITY_VILLAGER_NO, 1.0f);
                                        })
                                );
                            }
                            else {
                                builder.addLore(ButtonComponents.left("equip"));
                                
                                slotPatternApplier.add(
                                        builder.asIcon(),
                                        PlayerMenuAction.of(player -> {
                                            this.heroInstance.setArtifact(artifactSlot, availableArtifacts.getFirst());
                                            
                                            new MenuHeroArtifactEquip(player, heroInstance);
                                        })
                                );
                            }
                        }
                    });
        
        slotPatternApplier.apply();
    }
    
}
