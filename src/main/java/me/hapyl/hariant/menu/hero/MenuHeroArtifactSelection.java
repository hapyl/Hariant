package me.hapyl.hariant.menu.hero;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.PlayerMenuTitle;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.eterna.module.text.Capitalizable;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.inventory.item.artifact.ArtifactFilter;
import me.hapyl.hariant.inventory.item.artifact.ArtifactSlot;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifact;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifactInstance;
import me.hapyl.hariant.inventory.item.artifact.affix.ArtifactAffix;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSet;
import me.hapyl.hariant.menu.Menu;
import me.hapyl.hariant.menu.MenuPage;
import me.hapyl.hariant.menu.MenuReturn;
import me.hapyl.hariant.menu.ObjectCycle;
import me.hapyl.hariant.menu.artifact.AbstractMenuArtifactSet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class MenuHeroArtifactSelection extends MenuPage<ItemArtifactInstance> {
    
    private static final ItemStack ITEM_NO_ARTIFACTS
            = ItemBuilder.playerHead("7df0ee9d25b41cb645dd2fe5c7746cbb8a1d37fd3e01e25e013242f9d03a30d6")
                         .setName(Component.text("No Artifacts!", Colors.RED))
                         .addLore()
                         .addWrappedLore(Component.text("You don't have any artifacts that are applicable to this slot!"))
                         .asIcon();
    
    private static final ItemStack ITEM_NO_ARTIFACTS_FILTERED
            = ItemBuilder.playerHead("609d6a348a9923ebbb3423b196973d495b1aa5c056e4b351b120d36d8331a542")
                         .setName(Component.text("No Matching Artifacts!", Colors.RED))
                         .addLore()
                         .addWrappedLore(Component.text("There aren't any artifacts that match the filter!"))
                         .asIcon();
    
    private static final ItemStack ITEM_CONFIRM
            = ItemBuilder.playerHead("930f4537d214d38666e6304e9c851cd6f7e41a0eb7c25049c9d22c8c5f6545df")
                         .setName(Component.text("Confirm", Colors.GREEN))
                         .addLore()
                         .addWrappedLore(Component.text("Confirms the current filter and returns to the previous menu."))
                         .addLore()
                         .addLore(ButtonComponents.left("confirm"))
                         .asIcon();
    
    private static final Comparator<ItemArtifactInstance> COMPARATOR = Comparator.comparing(ItemArtifactInstance::getTimestamp).reversed();
    
    private static final int MAX_FILTERING_SETS_TO_DISPLAY = 4;
    private static final int MAX_FILTERING_ATTRIBUTES_TO_DISPLAY = 5;
    
    private static final Component COMPONENT_BULLET = Component.text(" ● ", Colors.DARK_GRAY);
    private static final Component COMPONENT_EMPTY = Component.text(" None!", Colors.DARK_GRAY);
    private static final Component COMPONENT_RECOMMENDED_ATTRIBUTE = Component.text(" ⭐", Colors.GOLD, TextDecoration.BOLD);
    
    private final PlayerDatabase playerDatabase;
    private final HeroInstance heroInstance;
    private final ArtifactSlot artifactSlot;
    
    private final @Nullable ItemArtifactInstance currentArtifact;
    private final @NotNull ArtifactFilter artifactFilter;
    
    private final List<? extends AttributeType> possibleAttributes;
    
    public MenuHeroArtifactSelection(@NotNull Player player, @NotNull HeroInstance heroInstance, @NotNull ArtifactSlot artifactSlot, @Nullable ItemArtifactInstance currentArtifact) {
        super(player, PlayerMenuTitle.create(Component.text("Select Artifact"), artifactSlot.asComponent()));
        
        this.playerDatabase = Hariant.getPlayerDatabase(player);
        this.heroInstance = heroInstance;
        this.artifactSlot = artifactSlot;
        this.currentArtifact = currentArtifact;
        this.artifactFilter = heroInstance.getArtifactFilter();
        this.possibleAttributes = artifactSlot.getArtifactAttributeDistribution().listAttributes();
        
        this.updateContentsOpenMenu();
    }
    
    @Override
    public @Nullable MenuReturn menuReturn() {
        return MenuReturn.create(Component.text("Artifact Equip"), () -> Category.ARTIFACTS.createMenu(player, heroInstance));
    }
    
    @Override
    public @NotNull ItemBuilder createBuilder(@NotNull ItemArtifactInstance artifactInstance) {
        final ItemBuilder builder = artifactInstance.createBuilder();
        
        // Compare to current artifact
        if (currentArtifact != null) {
            final ArtifactAffix currentAffix = currentArtifact.getArtifactAffix();
            final ArtifactAffix newAffix = artifactInstance.getArtifactAffix();
            
            final Style style = Style.style(Colors.DARK_GRAY);
            
            if (currentAffix != newAffix) {
                builder.addLore();
                builder.addLore(Component.text("Compared to ", style).append(currentArtifact.getOrigin().getName().style(style)));
                
                final AttributeType currentAffixAttributeType = currentAffix.getAttributeType();
                final AttributeType newAffixAttributeType = newAffix.getAttributeType();
                
                builder.addLore(
                        Component.text(" ")
                                 .append(currentAffixAttributeType)
                                 .append(Component.text(" -", Colors.RED))
                                 .append(currentAffixAttributeType.format(currentAffix.getValue()).color(Colors.RED))
                );
                
                builder.addLore(
                        Component.text(" ")
                                 .append(newAffixAttributeType)
                                 .append(Component.text(" +", Colors.GREEN))
                                 .append(newAffixAttributeType.format(newAffix.getValue()).color(Colors.GREEN))
                );
            }
        }
        
        builder.addLore();
        builder.addLore(ButtonComponents.left("equip"));
        
        return builder;
    }
    
    @Override
    public void onClick(@NotNull ItemArtifactInstance artifactInstance, @NotNull ClickType clickType) {
        this.heroInstance.setArtifact(artifactInstance);
        
        new MenuHeroArtifactEquip(player, heroInstance);
    }
    
    @Override
    public @NotNull ItemStack getItemNoContents() {
        return artifactFilter.isEmpty() ? ITEM_NO_ARTIFACTS : ITEM_NO_ARTIFACTS_FILTERED;
    }
    
    @Override
    public void updateMenu() {
        super.updateMenu();
        
        // Set filter item
        setItem(
                47,
                createFilterBuilder()
                        .addLore()
                        .addLore(ButtonComponents.left("modify"))
                        .addLore(ButtonComponents.right("reset"))
                        .asIcon(),
                PlayerMenuAction.builder()
                                .left(player -> {
                                    new MenuArtifactFilter(player, this, artifactFilter);
                                })
                                .right(player -> {
                                    this.artifactFilter.reset();
                                    this.updateContentsOpenMenu();
                                })
                                .build()
        );
    }
    
    private void updateContentsOpenMenu() {
        this.setContents(
                playerDatabase.inventory.streamItemsOfType(ItemArtifactInstance.class)
                                        .filter(Predicate.not(ItemArtifactInstance::isHeld))
                                        .filter(artifact -> artifact.getArtifactSlot() == artifactSlot)
                                        .filter(artifact -> artifactFilter.test(artifact, possibleAttributes))
                                        .sorted(COMPARATOR)
                                        .toList()
        );
        
        // Force to update the menu at page 1
        this.openMenu(1);
    }
    
    private @NotNull ItemBuilder createFilterBuilder() {
        final ItemBuilder builder = new ItemBuilder(Material.NAME_TAG);
        
        builder.setName(Component.text("Filter"));
        builder.addLore(Component.text(Capitalizable.capitalize(artifactSlot), Colors.DARK_GRAY));
        builder.addLore();
        
        // Show filtering sets
        builder.addLore(Component.text("Filtering Sets:"));
        builder.addLore(createFilteringLore(artifactFilter.getFilteringSets(), MAX_FILTERING_SETS_TO_DISPLAY));
        
        builder.addLore();
        
        // Show filtering attributes
        builder.addLore(Component.text("Filtering Attributes:"));
        builder.addLore(createFilteringLore(artifactFilter.getFilteringAttributes(possibleAttributes), MAX_FILTERING_ATTRIBUTES_TO_DISPLAY));
        
        return builder;
    }
    
    private static @NotNull <T extends ComponentLike> List<? extends Component> createFilteringLore(@NotNull Set<T> filtering, int limit) {
        if (filtering.isEmpty()) {
            return List.of(COMPONENT_EMPTY);
        }
        
        final List<Component> components = Lists.newArrayList();
        int index = 0;
        
        for (T t : filtering) {
            if (index++ >= limit) {
                components.add(Component.text("...and %s more!".formatted(filtering.size() - limit), Colors.DARK_GRAY));
                break;
            }
            
            components.add(COMPONENT_BULLET.append(t.asComponent().color(Colors.GREEN)));
        }
        
        return components;
    }
    
    private static void playButtonSfx(@NotNull Player player, boolean value) {
        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 3, value ? 1.25f : 0.75f);
    }
    
    private static class MenuArtifactFilter extends Menu {
        
        private final MenuHeroArtifactSelection menu;
        private final ArtifactFilter artifactFilter;
        private final ObjectCycle<AttributeType> attributeCycle;
        
        public MenuArtifactFilter(@NotNull Player player, @NotNull MenuHeroArtifactSelection menu, @NotNull ArtifactFilter artifactFilter) {
            super(player, () -> Component.text("Artifact Filter"), ChestSize.SIZE_5);
            
            this.menu = menu;
            this.artifactFilter = artifactFilter;
            this.attributeCycle = new ObjectCycle<>(menu.possibleAttributes) {
                @Override
                public @NotNull Component getName(@NotNull AttributeType attributeType) {
                    return Component.empty()
                                    .append(Component.text("[", Colors.DARK_GRAY))
                                    .append(Components.checkmark(menu.artifactFilter.getFilteringAttributes().contains(attributeType)))
                                    .append(Component.text("]", Colors.DARK_GRAY))
                                    .appendSpace()
                                    .append(attributeType.asComponent())
                                    .append(menu.heroInstance.getOrigin().getRecommendedAttributes().contains(attributeType) ? COMPONENT_RECOMMENDED_ATTRIBUTE : Component.empty());
                }
                
                @Override
                public @NotNull ItemBuilder createBaseBuilder() {
                    final ItemBuilder builder = super.createBaseBuilder();
                    builder.setName(Component.text("Filter Attributes"));
                    
                    builder.addLore();
                    builder.addWrappedLore(Component.text("Filter by attributes applicable to this slot."));
                    
                    builder.addLore();
                    builder.addLore(Component.text("Applicable Attributes:", Colors.GOLD));
                    
                    return builder;
                }
                
                @Override
                public void onCycle(@NotNull AttributeType attributeType) {
                    updateMenu();
                }
            };
            
            this.openMenu();
        }
        
        @Override
        public int getCloseButtonSlot() {
            return -1;
        }
        
        @Override
        public void updateMenu() {
            setItem(
                    20,
                    createFilteringSetsBuilder()
                            .addLore()
                            .addLore(ButtonComponents.left("modify"))
                            .addLore(ButtonComponents.right("reset"))
                            .asIcon(),
                    PlayerMenuAction.builder()
                                    .left(player -> {
                                        new MenuFilterArtifactSet(player, this);
                                    })
                                    .right(player -> {
                                        this.artifactFilter.getFilteringSets().clear();
                                        this.updateMenu();
                                    })
                                    .build()
            );
            
            setItem(
                    24,
                    attributeCycle.createBuilderDefaultCycle()
                                  .addLore(ButtonComponents.swapOffhand("toggle"))
                                  .asIcon(),
                    PlayerMenuAction.builder()
                                    .left(player -> attributeCycle.cycleNext())
                                    .right(player -> attributeCycle.cyclePrevious())
                                    .swapOffhand(player -> {
                                        final AttributeType attributeType = attributeCycle.currentValue();
                                        final Set<AttributeType> filteringAttributes = menu.artifactFilter.getFilteringAttributes();
                                        
                                        if (!filteringAttributes.add(attributeType)) {
                                            filteringAttributes.remove(attributeType);
                                            playButtonSfx(player, false);
                                        }
                                        else {
                                            playButtonSfx(player, true);
                                        }
                                        
                                        this.openMenu();
                                    })
                                    .build()
            );
            
            setItem(40, ITEM_CONFIRM, PlayerMenuAction.of(player -> menu.updateContentsOpenMenu()));
        }
        
        @Override
        public int getReturnButtonSlot() {
            return getMenuSize() - 5;
        }
        
        public @NotNull ItemBuilder createFilteringSetsBuilder() {
            final ItemBuilder builder = new ItemBuilder(Material.ARMOR_STAND);
            
            builder.setName(Component.text("Filter Sets"));
            builder.addLore();
            
            builder.addWrappedLore(Component.text("Filter by artifact sets, applicable to all slots."));
            builder.addLore();
            
            builder.addLore(Component.text("Applicable sets:", Colors.GOLD));
            
            final Set<? extends ArtifactSet> filteringSets = menu.artifactFilter.getFilteringSets();
            
            if (filteringSets.isEmpty()) {
                builder.addLore(COMPONENT_EMPTY);
            }
            else {
                for (ArtifactSet artifactSet : filteringSets) {
                    builder.addLore(COMPONENT_BULLET.append(artifactSet.getName().color(Colors.GREEN)));
                }
            }
            
            return builder;
        }
        
    }
    
    private static class MenuFilterArtifactSet extends AbstractMenuArtifactSet {
        
        private final MenuArtifactFilter menu;
        
        MenuFilterArtifactSet(@NotNull Player player, @NotNull MenuArtifactFilter menu) {
            super(player, PlayerMenuTitle.create(menu.getTitle().asComponent(), Component.text("Artifact Sets")));
            
            this.menu = menu;
            this.openMenu();
        }
        
        @Override
        public int getCloseButtonSlot() {
            return -1;
        }
        
        @Override
        public @NotNull ItemBuilder createBuilder(@NotNull ItemArtifact itemArtifact) {
            final ItemBuilder builder = super.createBuilder(itemArtifact);
            final boolean filtered = menu.artifactFilter.getFilteringSets().contains(itemArtifact.getArtifactSet());
            
            builder.setName(itemArtifact.getArtifactSet().getName().color(filtered ? Colors.GREEN : Colors.RED));
            builder.addLore();
            
            if (filtered) {
                builder.addLore(Component.text("This set is being filtered!", Colors.GREEN));
            }
            else {
                builder.addLore(Component.text("This set is not being filtered!", Colors.RED));
            }
            
            builder.addLore();
            builder.addLore(ButtonComponents.left("toggle"));
            
            return builder;
        }
        
        @Override
        public void onClick(@NotNull ItemArtifact itemArtifact, @NotNull ClickType clickType) {
            final Set<ArtifactSet> filteringSets = menu.artifactFilter.getFilteringSets();
            final ArtifactSet artifactSet = itemArtifact.getArtifactSet();
            
            if (!filteringSets.add(artifactSet)) {
                filteringSets.remove(artifactSet);
                playButtonSfx(player, false);
            }
            else {
                playButtonSfx(player, true);
            }
            
            this.openMenu();
        }
        
        @Override
        public void openMenu(@Range(from = 1, to = Integer.MAX_VALUE) int page) {
            super.openMenu(page);
            
            setItem(49, ITEM_CONFIRM, PlayerMenuAction.of(player -> menu.openMenu()));
        }
    }
    
    
}
