package me.hapyl.hariant.menu.hero;

import me.hapyl.eterna.module.component.ComponentStyler;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.hero.*;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.weapon.Weapon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MenuHeroProfile extends MenuHeroAbstract {
    
    private static final Icon ICON_AFFILIATION = Icon.ofTexture("b0aca013178a9f47913e894d3d0bfd4b0b66120825b9aab8a4d7d9bf0245abf");
    private static final ComponentStyler STYLER_AFFILIATION = ComponentStyler.builder(Style.style(Colors.GRAY)).withPadding(2).build();
    
    public MenuHeroProfile(@NotNull Player player, @NotNull HeroInstance heroInstance) {
        super(player, heroInstance, Category.PROFILE);
        this.openMenu();
    }
    
    @Override
    public void updateMenu() {
        super.updateMenu();
        
        final Hero hero = heroInstance.getOrigin();
        final HeroProfile profile = hero.getProfile();
        
        // Set affiliation
        final Affiliation affiliation = profile.getAffiliation();
        
        setItem(
                19,
                ICON_AFFILIATION.createBuilder()
                                .setName(Component.text("Affiliation"))
                                .addLore()
                                .addLore(affiliation.asComponent().color(Colors.WHITE))
                                .addWrappedLore(affiliation.getDescription(), STYLER_AFFILIATION)
                                .asIcon()
        );
        
        // Set attribute
        setItem(30, createAttributesIcon());
        
        // Set weapon
        setItem(32, createWeaponIcon());
        
        // Set lore
        setItem(
                25,
                new ItemBuilder(Material.WRITABLE_BOOK)
                        .setName(Component.text("Story"))
                        .addLore()
                        .addLore(Component.text("Learn the story of this hero!"))
                        .addLore()
                        .addLore(Component.text("Find at least one chapter to unlock!", Colors.ERROR))
                        .asIcon()
        );
    }
    
    @NotNull
    private ItemStack createAttributesIcon() {
        final Hero hero = heroInstance.getOrigin();
        final Attributes attributes = hero.getAttributes();
        
        final ItemBuilder builder = new ItemBuilder(Material.COMPARATOR);
        builder.setName(Component.text("Attributes"));
        builder.addLore();
        
        final Map<? extends AttributeType, ? extends Double> sumArtifactAffixes = heroInstance.sumArtifactAffixes();
        
        // Base attributes
        builder.addLore(Component.text("ʙᴀꜱᴇ ᴀᴛᴛʀɪʙᴜᴛᴇꜱ", Colors.DEFAULT_COLOR, TextDecoration.BOLD));
        
        for (AttributeType attributeType : AttributeType.getBaseAttributes()) {
            builder.addLore(attributes.createLore(attributeType, sumArtifactAffixes.get(attributeType)));
        }
        
        // Advanced attributes
        builder.addLore();
        builder.addLore(Component.text("ᴀᴅᴠᴀɴᴄᴇᴅ ᴀᴛᴛʀɪʙᴜᴛᴇꜱ", Colors.DEFAULT_COLOR, TextDecoration.BOLD));
        
        for (AttributeType attributeType : AttributeType.getAdvancedAttributes()) {
            builder.addLore(attributes.createLore(attributeType, sumArtifactAffixes.get(attributeType)));
        }
        
        builder.addLore();
        builder.addLore(Component.text("ᴇʟᴇᴍᴇɴᴛᴀʟ ᴀᴛᴛʀɪʙᴜᴛᴇꜱ", Colors.DEFAULT_COLOR, TextDecoration.BOLD));
        builder.addLore(Component.text(" (Element)  (RES/DMG Bonus)", Colors.DARK_GRAY));
        
        // Elemental attributes
        for (ElementType elementType : ElementType.values()) {
            final AttributeType elementalResistanceAttribute = AttributeType.getElementalResistanceAttribute(elementType);
            final AttributeType elementalDamageBonusAttribute = AttributeType.getElementalDamageBonusAttribute(elementType);
            
            builder.addLore(
                    Component.empty()
                             .append(Component.text(" "))
                             .append(elementType.asComponent())
                             .append(Component.text("    "))
                             .append(createElementalLore(attributes, elementalResistanceAttribute, sumArtifactAffixes.get(elementalResistanceAttribute)).color(Colors.GREEN))
                             .append(Component.text(" / ", Colors.DARK_GRAY))
                             .append(createElementalLore(attributes, elementalDamageBonusAttribute, sumArtifactAffixes.get(elementalDamageBonusAttribute)).color(Colors.RED))
            );
        }
        
        return builder.asIcon();
    }
    
    private static @NotNull Component createElementalLore(@NotNull Attributes attributes, @NotNull AttributeType attributeType, @Nullable Double externalValue) {
        return Component.empty()
                        .append(attributeType.format(attributes.get(attributeType) + (externalValue != null ? externalValue : 0)))
                        .append(Attributes.createExternalValueComponent(externalValue));
    }
    
    @NotNull
    private ItemStack createWeaponIcon() {
        final Weapon weapon = heroInstance.getOrigin().getWeapon();
        final ItemBuilder builder = weapon.createBuilder();
        
        builder.setName(Component.text("Weapon"));
        
        builder.editLore(lore -> {
            lore.addFirst(weapon.getName().color(Colors.DARK_GRAY).decoration(TextDecoration.ITALIC, false));
        });
        
        return builder.asIcon();
    }
    
    private static @NotNull Component fromArtifacts(@Nullable Double value) {
        return value != null ? Component.text(" +%,.0f".formatted(value), Colors.GREEN) : Component.empty();
    }
    
}
