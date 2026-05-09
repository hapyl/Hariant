package me.hapyl.hariant.menu.hero;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.hero.Affiliation;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.hero.HeroProfile;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.weapon.Weapon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MenuHeroProfile extends MenuHeroAbstract {
    
    private static final Icon ICON_AFFILIATION = Icon.ofTexture("b0aca013178a9f47913e894d3d0bfd4b0b66120825b9aab8a4d7d9bf0245abf");
    private static final Style STYLE_AFFILIATION = Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC);
    
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
                                .addLore(affiliation.asComponent().color(NamedTextColor.WHITE))
                                .addWrappedLore(affiliation.getDescription(), _component -> Component.text("  ").append(_component.style(STYLE_AFFILIATION)))
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
        
        
        // Base attributes
        builder.addLore(Component.text("ʙᴀꜱᴇ ᴀᴛᴛʀɪʙᴜᴛᴇꜱ", Colors.DEFAULT_COLOR, TextDecoration.BOLD));
        
        for (AttributeType attributeType : AttributeType.getBaseAttributes()) {
            builder.addLore(attributes.createLore(attributeType));
        }
        
        // Advanced attributes
        builder.addLore();
        builder.addLore(Component.text("ᴀᴅᴠᴀɴᴄᴇᴅ ᴀᴛᴛʀɪʙᴜᴛᴇꜱ", Colors.DEFAULT_COLOR, TextDecoration.BOLD));
        
        for (AttributeType attributeType : AttributeType.getAdvancedAttributes()) {
            
            attributes.createLore(attributeType);
            builder.addLore(
                    Component.empty()
                             .appendSpace()
                             .append(attributeType)
                             .appendSpace()
                             .append(attributeType.format(attributes.base(attributeType)))
            );
        }
        
        builder.addLore();
        builder.addLore(Component.text("ᴇʟᴇᴍᴇɴᴛᴀʟ ᴀᴛᴛʀɪʙᴜᴛᴇꜱ", Colors.DEFAULT_COLOR, TextDecoration.BOLD));
        
        builder.addLore(Component.text(" (Element) (Resistance/DMG Bonus)", NamedTextColor.DARK_GRAY));
        
        // Elemental attributes
        for (ElementType elementType : ElementType.values()) {
            final AttributeType elementalResistanceAttribute = AttributeType.getElementalResistanceAttribute(elementType);
            final AttributeType elementalDamageBonusAttribute = AttributeType.getElementalDamageBonusAttribute(elementType);
            
            final Component elementalResistance = elementalResistanceAttribute.format(attributes.get(elementalResistanceAttribute));
            final Component elementalDamageBonus = elementalDamageBonusAttribute.format(attributes.get(elementalDamageBonusAttribute));
            
            builder.addLore(
                    Component.empty()
                             .appendSpace()
                             .append(elementType.asComponent())
                             .append(Component.text("   "))
                             .append(elementalResistance.color(NamedTextColor.GREEN))
                             .append(Component.text(" / ", NamedTextColor.GRAY))
                             .append(elementalDamageBonus.color(NamedTextColor.RED))
            );
        }
        
        return builder.asIcon();
    }
    
    @NotNull
    private ItemStack createWeaponIcon() {
        final Weapon weapon = heroInstance.getOrigin().getWeapon();
        final ItemBuilder builder = weapon.createBuilder();
        
        builder.setName(Component.text("Weapon"));
        
        builder.editLore(lore -> {
            lore.addFirst(weapon.getName().color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        });
        
        return builder.asIcon();
    }
    
}
