package me.hapyl.hariant.menu.artifact;

import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.component.ComponentStyler;
import me.hapyl.eterna.module.component.Keybind;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.eterna.module.inventory.sign.SignInput;
import me.hapyl.eterna.module.inventory.sign.SignResponse;
import me.hapyl.eterna.module.inventory.sign.SignType;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.hero.ArtifactLoadouts;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.inventory.item.artifact.ArtifactSlot;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifactInstance;
import me.hapyl.hariant.inventory.item.artifact.loadout.ArtifactLoadout;
import me.hapyl.hariant.inventory.item.artifact.loadout.ArtifactLoadoutIndex;
import me.hapyl.hariant.menu.Button;
import me.hapyl.hariant.menu.Menu;
import me.hapyl.hariant.menu.MenuReturn;
import me.hapyl.hariant.menu.hero.MenuHeroArtifactEquip;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MenuArtifactLoadouts extends Menu {
    
    private final HeroInstance heroInstance;
    private final ButtonHandler buttonHandler;
    
    public MenuArtifactLoadouts(@NotNull Player player, @NotNull HeroInstance heroInstance, @NotNull ButtonHandler buttonHandler) {
        super(player, () -> Component.text("Artifact Loadouts"), ChestSize.SIZE_5);
        
        this.heroInstance = heroInstance;
        this.buttonHandler = buttonHandler;
        this.openMenu();
    }
    
    @Override
    public void updateMenu() {
        final ArtifactLoadouts artifactLoadouts = heroInstance.getArtifactLoadouts();
        
        for (ArtifactLoadoutIndex loadoutIndex : ArtifactLoadoutIndex.values()) {
            final int buttonSlot = loadoutIndex.getSlot();
            final ArtifactLoadout loadout = artifactLoadouts.getLoadout(loadoutIndex).orElse(null);
            
            // Always set the button
            final Button button = buttonHandler.button(player, heroInstance, artifactLoadouts, loadoutIndex, loadout);
            
            setItem(
                    buttonSlot,
                    button.createBuilder()
                          .setAmount(loadoutIndex.ordinal() + 1)
                          .asIcon(),
                    button.createMenuAction()
            );
            
            // Show artifacts
            for (int i = 0; i < ArtifactSlot.LENGTH; i++) {
                setItem(buttonSlot + i + 1, createArtifactItem(loadout, i));
            }
        }
    }
    
    @Override
    public @NotNull MenuReturn menuReturn() {
        return MenuReturn.create(Component.text("Artifacts"), () -> new MenuHeroArtifactEquip(player, heroInstance));
    }
    
    private @NotNull ItemStack createArtifactItem(@Nullable ArtifactLoadout loadout, int index) {
        return loadout != null
               ? loadout.getArtifacts()[index].createBuilder().setAmount(index + 1).asIcon()
               : new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                 .setAmount(index + 1)
                 .setName(Component.text("No Artifact!"))
                 .asIcon();
    }
    
    public interface ButtonHandler {
        
        @NotNull Button button(@NotNull Player player, @NotNull HeroInstance heroInstance, @NotNull ArtifactLoadouts artifactLoadouts, @NotNull ArtifactLoadoutIndex loadoutIndex, @Nullable ArtifactLoadout loadout);
        
        static @NotNull ButtonHandler equip() {
            return new ButtonHandlerEquip();
        }
        
        static @NotNull ButtonHandler save(@NotNull ItemArtifactInstance[] artifacts) {
            return new ButtonHandlerSave(artifacts);
        }
        
    }
    
    public static class ButtonHandlerEquip implements ButtonHandler {
        
        private static final ComponentStyler STYLER_ARTIFACT_EQUIPPED_BY_ANOTHER_HERO = ComponentStyler.create(Style.style(Colors.AQUA));
        
        private ButtonHandlerEquip() {
        }
        
        @Override
        public @NotNull Button button(@NotNull Player player, @NotNull HeroInstance heroInstance, @NotNull ArtifactLoadouts artifactLoadouts, @NotNull ArtifactLoadoutIndex loadoutIndex, @Nullable ArtifactLoadout loadout) {
            // No loadout
            if (loadout == null) {
                return Button.create(
                        new ItemBuilder(Material.GRAY_DYE)
                                .setName(Component.text(loadoutIndex.getDefaultName(), Colors.RED))
                                .addLore()
                                .addWrappedLore(Component.text("There is currently no loadout here!"))
                );
            }
            
            // Loadout is identical to currently equipped one
            final boolean identical = loadout.isIdentical(heroInstance.artifactsAsArray());
            
            final ItemBuilder builder = new ItemBuilder(identical ? Material.RED_DYE : Material.LIME_DYE);
            builder.setName(loadout.asComponent().color(identical ? Colors.RED : Colors.GREEN));
            builder.addLore();
            
            if (identical) {
                builder.addWrappedLore(Component.text("Identical artifacts are currently equipped by the hero!"));
                builder.addLore();
            }
            else {
                final boolean isAnyArtifactsEquippedByAnotherHero = loadout.isAnyArtifactsEquippedByAnotherHero(heroInstance);
                
                if (isAnyArtifactsEquippedByAnotherHero) {
                    builder.setType(Material.CYAN_DYE);
                    
                    builder.addWrappedLore(
                            Component.empty()
                                     .append(Component.text("One or multiplie artifacts are currently equipped by another hero!"))
                                     .appendNewline()
                                     .appendNewline()
                                     .append(Component.text("Equipping this loadout will unequip the artifacts from that hero!")),
                            STYLER_ARTIFACT_EQUIPPED_BY_ANOTHER_HERO
                    );
                    builder.addLore();
                }
                
                builder.addLore(ButtonComponents.left("equip"));
            }
            
            builder.addLore(ButtonComponents.right("rename"));
            builder.addLore(
                    Component.empty()
                             .color(Colors.RED)
                             .append(Component.text("\uD83D\uDDD1 "))
                             .append(Component.keybind(Keybind.DROP))
                             .append(Component.text(" to delete"))
            );
            
            return Button.create(
                    builder,
                    PlayerMenuAction.builder()
                                    .left(_ -> {
                                        if (!identical) {
                                            loadout.equip(heroInstance);
                                            player.playSound(player, Sound.ENTITY_HORSE_SADDLE, 3, 1.25f);
                                            
                                            new MenuHeroArtifactEquip(player, heroInstance);
                                        }
                                    })
                                    .right(_ -> {
                                        new SignInput(player, SignType.OAK, "Enter Name") {
                                            @Override
                                            public void onResponse(@NotNull SignResponse response) {
                                                response.synchronize(Hariant.getPlugin(), () -> {
                                                    final String newName = response.toString();
                                                    
                                                    if (newName.isEmpty()) {
                                                        HariantLogger.error(player, Component.text("Loadout name cannot be empty!"));
                                                    }
                                                    else {
                                                        final String previousName = loadout.getName();
                                                        
                                                        loadout.setName(newName);
                                                        
                                                        HariantLogger.success(
                                                                player,
                                                                Component.empty()
                                                                         .append(Component.text("Renamed "))
                                                                         .append(Component.text(previousName))
                                                                         .append(Component.text(" to "))
                                                                         .append(Component.text(newName))
                                                                         .append(Component.text("!"))
                                                        );
                                                        
                                                        player.playSound(player, Sound.ENTITY_VILLAGER_WORK_CARTOGRAPHER, 3, 1.0f);
                                                    }
                                                    
                                                    new MenuArtifactLoadouts(player, heroInstance, ButtonHandler.equip());
                                                });
                                            }
                                        };
                                    })
                                    .drop(_ -> {
                                        artifactLoadouts.deleteLoadout(loadout);
                                        player.playSound(player, Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 3, 2.0f);
                                        
                                        new MenuArtifactLoadouts(player, heroInstance, ButtonHandler.equip());
                                    })
                                    .build()
            );
        }
        
        
    }
    
    public static class ButtonHandlerSave implements ButtonHandler {
        
        private final ItemArtifactInstance[] artifacts;
        
        ButtonHandlerSave(@NotNull ItemArtifactInstance[] artifacts) {
            this.artifacts = artifacts;
        }
        
        @Override
        public @NotNull Button button(@NotNull Player player, @NotNull HeroInstance heroInstance, @NotNull ArtifactLoadouts artifactLoadouts, @NotNull ArtifactLoadoutIndex loadoutIndex, @Nullable ArtifactLoadout loadout) {
            final Component loadoutName = Component.text(loadout != null ? loadout.getName() : loadoutIndex.getDefaultName());
            
            // If identical, don't allow saving
            if (loadout != null && loadout.isIdentical(artifacts)) {
                return Button.create(
                        new ItemBuilder(Material.RED_DYE)
                                .setName(loadoutName.color(Colors.RED))
                                .addLore()
                                .addWrappedLore(Component.text("Identical artifacts are currently saved here!"))
                );
            }
            
            return Button.create(
                    new ItemBuilder(Material.PURPLE_DYE)
                            .setName(loadoutName.color(Colors.LIGHT_PURPLE))
                            .glow()
                            .addLore()
                            .addLore(ButtonComponents.left("save")),
                    PlayerMenuAction.of(_ -> {
                        artifactLoadouts.setLoadout(loadoutIndex, artifacts);
                        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 3, 2.0f);
                        
                        new MenuArtifactLoadouts(player, heroInstance, ButtonHandler.equip());
                    })
            );
        }
        
    }
    
}