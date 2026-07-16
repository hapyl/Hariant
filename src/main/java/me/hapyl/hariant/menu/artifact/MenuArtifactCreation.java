package me.hapyl.hariant.menu.artifact;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.inventory.HariantInventory;
import me.hapyl.hariant.inventory.item.Resource;
import me.hapyl.hariant.inventory.item.ResourceRegistry;
import me.hapyl.hariant.inventory.item.artifact.ArtifactSlot;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifact;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifactInstance;
import me.hapyl.hariant.inventory.item.artifact.affix.ArtifactAffix;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSet;
import me.hapyl.hariant.menu.Menu;
import me.hapyl.hariant.menu.MenuReturn;
import me.hapyl.hariant.menu.ObjectCycle;
import me.hapyl.hariant.shop.MenuTransaction;
import me.hapyl.hariant.shop.transaction.Transaction;
import me.hapyl.hariant.shop.transaction.TransactionException;
import me.hapyl.hariant.util.BooleanExplained;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class MenuArtifactCreation extends Menu {
    
    private static final Resource ARTIFICING_RESOURCE = ResourceRegistry.ARTIFACT_ARTIFICER;
    private static final int ARTIFICING_COST = 1;
    
    private static final Transaction TRANSACTION = Transaction.withResources(Map.of(ARTIFICING_RESOURCE, ARTIFICING_COST));
    
    private static final ItemStack ITEM_NO_ARTIFACT_SET_SELECTED = ItemBuilder.playerHead("b93a20db9f1f00838c1de68aeb9605c02f71a56988809c5c01817cdbcde0b6b1")
                                                                              .setName(Component.text("No Set Selected!", Colors.RED))
                                                                              .addLore()
                                                                              .addWrappedLore(Component.text("There is current no set selected, you must select a set before artificing an artifact!"))
                                                                              .addLore()
                                                                              .addLore(ButtonComponents.left("select set"))
                                                                              .asIcon();
    
    private static final String TEXTURE_CAN_ARTIFICE = "4312ca4632def5ffaf2eb0d9d7cc7b55a50c4e3920d90372aab140781f5dfbc4";
    private static final String TEXTURE_CANNOT_ARTIFICE = "beb588b21a6f98ad1ff4e085c552dcb050efc9cab427f46048f18fc803475f7";
    
    private static final Style LORE_STYLE_NO_ITALIC = Style.style(Colors.GRAY).decoration(TextDecoration.ITALIC, false);
    
    private final HariantInventory inventory;
    
    private final ObjectCycle<ArtifactSlot> artifactSlotCycle;
    
    // Have to store artifact item instead of set because sets don't have item representations
    private @Nullable ItemArtifact itemArtifact;
    private @NotNull ObjectCycle<AttributeType> attributeTypeCycle;
    
    public MenuArtifactCreation(@NotNull Player player) {
        super(player, () -> Component.text("Artifact Artificing"), ChestSize.SIZE_6);
        
        this.inventory = profile.getDatabase().inventory;
        
        this.itemArtifact = null;
        this.artifactSlotCycle = new ObjectCycleArtifactSlot();
        this.attributeTypeCycle = new ObjectCycleAttributeType();
        
        this.openMenu();
    }
    
    @Override
    public void updateMenu() {
        // Artifact set
        setItem(
                20,
                createArtifactSetItem(),
                PlayerMenuAction.of(MenuArtifactCreationSelectSet::new)
        );
        
        // Artifact slot
        setItem(
                22,
                artifactSlotCycle.createBuilderDefaultCycle().asIcon(),
                PlayerMenuAction.builder()
                                .left(player -> artifactSlotCycle.cycleNext())
                                .right(player -> artifactSlotCycle.cyclePrevious())
                                .build()
        );
        
        // Attribute type
        setItem(
                24,
                attributeTypeCycle.createBuilderDefaultCycle().asIcon(),
                PlayerMenuAction.builder()
                                .left(player -> attributeTypeCycle.cycleNext())
                                .right(player -> attributeTypeCycle.cyclePrevious())
                                .build()
        );
        
        // Artifice button
        final BooleanExplained booleanExplained = canArtifice();
        
        if (booleanExplained.booleanValue() && itemArtifact != null) {
            final ItemBuilder builder = ItemBuilder.playerHead(TEXTURE_CAN_ARTIFICE);
            builder.setName(Component.text("Confirm Artificing"));
            
            // Append artifact set description
            itemArtifact.getArtifactSet().supplyLore(builder, ArtifactSet.ArtifactSetDescription.EMPTY);
            
            // This is a very weird hack, but I really want the artifact set description to have padding
            builder.editMeta(meta -> {
                final List<Component> lore = Lists.newArrayList();
                
                lore.add(Component.empty());
                lore.add(Component.text("Designated Slot:", LORE_STYLE_NO_ITALIC));
                lore.add(
                        Component.empty()
                                 .appendSpace()
                                 .append(artifactSlotCycle.currentValue().getName().color(Colors.GREEN).decoration(TextDecoration.ITALIC, false))
                );
                
                lore.add(Component.empty());
                lore.add(Component.text("Designated Attribute:", LORE_STYLE_NO_ITALIC));
                lore.add(Component.text(" ").append(attributeTypeCycle.currentValue().asComponent().decoration(TextDecoration.ITALIC, false)));
                
                lore.add(Component.empty());
                lore.add(Component.text("Designated Set:", LORE_STYLE_NO_ITALIC));
                
                lore.addAll(
                        Objects.requireNonNullElseGet(meta.lore(), Lists::<Component>newArrayList)
                               .stream()
                               .map(component -> Component.space().append(component))
                               .toList()
                );
                
                meta.lore(lore);
            });
            
            builder.addLore();
            builder.addLore(Component.text("Artificing Cost", Colors.GRAY));
            builder.addLore(
                    Component.empty()
                             .appendSpace()
                             .append(ARTIFICING_RESOURCE.getNameStyledWithRarity())
                             .appendSpace()
                             .append(Components.makeComponentFractional(ARTIFICING_COST, inventory.getResource(ARTIFICING_RESOURCE)))
            );
            
            builder.addLore();
            builder.addLore(ButtonComponents.left("artifice"));
            
            setItem(
                    31,
                    builder.asIcon(),
                    new MenuTransaction() {
                        @Override
                        public @NotNull Transaction payment() {
                            return TRANSACTION;
                        }
                        
                        @Override
                        public void deliver(@NotNull Player player, @NotNull PlayerDatabase playerDatabase) throws TransactionException {
                            if (itemArtifact == null) {
                                throw new TransactionException("Artifact set not selected!");
                            }
                            
                            closeMenu();
                            
                            playerDatabase.inventory.adderOfItem(itemArtifact)
                                                    // If delivered, show the artifact
                                                    .onSuccess(instance -> {
                                                        if (instance instanceof ItemArtifactInstance artifactInstance) {
                                                            artifactInstance.setArtifactSlot(artifactSlotCycle.currentValue());
                                                            artifactInstance.setArtifactAffix(ArtifactAffix.ofAttribute(attributeTypeCycle.currentValue()));
                                                        }
                                                        
                                                        HariantLogger.success(
                                                                player,
                                                                Component.empty()
                                                                         .append(Component.text("Successfully artificed "))
                                                                         .append(instance.getNameStyled().hoverEvent(instance.createHoverEvent()))
                                                                         .append(Component.text("!"))
                                                        );
                                                        
                                                        // Fx
                                                        player.playSound(player, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 3, 1.25f);
                                                        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 3, 2.0f);
                                                    })
                                                    // If failed to deliver, tell why
                                                    .onError(error -> {
                                                        throw new TransactionException(error.getError());
                                                    });
                        }
                    }
            );
        }
        else {
            final ItemBuilder builder = ItemBuilder.playerHead(TEXTURE_CANNOT_ARTIFICE);
            
            builder.setName(Component.text("Cannot Artifice", Colors.RED));
            
            builder.addLore();
            builder.addWrappedLore(Component.text("Artifact artificing is not currently available:"));
            
            builder.addLore();
            builder.addLore(booleanExplained.explain().color(Colors.DARK_RED));
            
            setItem(31, builder.asIcon());
        }
    }
    
    private @NotNull BooleanExplained canArtifice() {
        if (itemArtifact == null) {
            return BooleanExplained.ofFalse(Component.text("Artifact set is not selected!"));
        }
        
        if (inventory.getResource(ARTIFICING_RESOURCE) < ARTIFICING_COST) {
            return BooleanExplained.ofFalse(Component.text("Not enough resources!"));
        }
        
        return BooleanExplained.ofTrue();
    }
    
    private @NotNull ItemStack createArtifactSetItem() {
        if (itemArtifact == null) {
            return ITEM_NO_ARTIFACT_SET_SELECTED;
        }
        
        final ItemBuilder builder = itemArtifact.getIcon().createBuilder();
        final ArtifactSet artifactSet = itemArtifact.getArtifactSet();
        
        builder.setName(Component.text("Artifact Set"));
        builder.addLore();
        
        artifactSet.supplyLore(builder, ArtifactSet.ArtifactSetDescription.EMPTY);
        
        builder.addLore();
        builder.addLore(ButtonComponents.left("change"));
        
        return builder.asIcon();
    }
    
    private class MenuArtifactCreationSelectSet extends AbstractMenuArtifactSet {
        
        public MenuArtifactCreationSelectSet(@NotNull Player player) {
            super(player, () -> Component.text("Select Artifact Set"));
            
            this.openMenu();
        }
        
        @Override
        public @Nullable MenuReturn menuReturn() {
            return MenuArtifactCreation.this;
        }
        
        @Override
        public @NotNull ItemBuilder createBuilder(@NotNull ItemArtifact itemArtifact) {
            final ItemBuilder builder = itemArtifact.createBuilder();
            
            builder.addLore();
            
            if (Objects.equals(MenuArtifactCreation.this.itemArtifact, itemArtifact)) {
                builder.addLore(Component.text("Already selected!", Colors.YELLOW));
            }
            else {
                builder.addLore(ButtonComponents.left("select"));
            }
            
            return builder;
        }
        
        @Override
        public void onClick(@NotNull ItemArtifact itemArtifact, @NotNull ClickType clickType) {
            MenuArtifactCreation.this.itemArtifact = itemArtifact;
            MenuArtifactCreation.this.openMenu();
        }
    }
    
    private class ObjectCycleArtifactSlot extends ObjectCycle<ArtifactSlot> {
        
        ObjectCycleArtifactSlot() {
            super(ArtifactSlot.values());
        }
        
        @Override
        public @NotNull ItemBuilder createBaseBuilder() {
            return currentValue().createBuilder().setName(Component.text("Artifact Slot"));
        }
        
        @Override
        public void onCycle(@NotNull ArtifactSlot artifactSlot) {
            // Update the attribute cycle
            attributeTypeCycle = new ObjectCycleAttributeType();
            openMenu();
        }
        
    }
    
    private class ObjectCycleAttributeType extends ObjectCycle<AttributeType> {
        
        ObjectCycleAttributeType() {
            super(artifactSlotCycle.currentValue().getArtifactAttributeDistribution().listAttributes());
        }
        
        @Override
        public @NotNull Component getName(@NotNull AttributeType attributeType) {
            return attributeType.asComponent();
        }
        
        @Override
        public @NotNull ItemBuilder createBaseBuilder() {
            return super.createBaseBuilder().setName(Component.text("Attribute Type"));
        }
        
        @Override
        public void onCycle(@NotNull AttributeType attributeType) {
            openMenu();
        }
        
    }
}