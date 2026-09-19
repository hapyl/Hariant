package me.hapyl.hariant.menu.hero;

import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.component.ComponentStyler;
import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.hero.HeroProfile;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.weapon.Weapon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MenuHeroProfile extends AbstractMenuHero {
    
    private static final Icon ICON_AFFILIATION = Icon.ofTexture("b0aca013178a9f47913e894d3d0bfd4b0b66120825b9aab8a4d7d9bf0245abf");
    private static final ComponentStyler STYLER_AFFILIATION = ComponentStyler.builder(Style.style(Colors.GRAY)).withPadding(3).build();
    
    private boolean showWeaponDetails;
    
    public MenuHeroProfile(@NotNull Player player, @NotNull HeroInstance heroInstance) {
        super(player, heroInstance, Category.PROFILE);
        
        this.openMenu();
    }
    
    @Override
    public void updateMenu() {
        super.updateMenu();
        
        // Set details
        setItem(19, createDescriptionIcon());
        
        // Set attribute
        setItem(30, createAttributesIcon());
        
        // Set weapon
        setItem(
                32,
                createWeaponIcon(),
                PlayerMenuAction.of(_ -> {
                    this.showWeaponDetails = !this.showWeaponDetails;
                    this.updateMenu();
                })
        );
        
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
    
    private @NotNull ItemStack createDescriptionIcon() {
        final ItemBuilder builder = ICON_AFFILIATION.createBuilder();
        
        final Hero origin = heroInstance.getOrigin();
        final HeroProfile profile = origin.getProfile();
        
        builder.setName(Component.text("Hero Profile"));
        
        appendProfileInfo(builder, profile.getArchetype());
        appendProfileInfo(builder, profile.getAffiliation());
        appendProfileInfo(builder, profile.getRace());
        
        return builder.asIcon();
    }
    
    private @NotNull ItemStack createAttributesIcon() {
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
            final AttributeType elementalDamageAttribute = elementType.getOffensiveAttribute();
            final AttributeType elementalResistanceAttribute = elementType.getDefensiveAttribute();
            
            if (elementalDamageAttribute == null || elementalResistanceAttribute == null) {
                continue;
            }
            
            builder.addLore(
                    Component.empty()
                             .append(Component.text(" "))
                             .append(elementType.asComponent())
                             .append(Component.text("    "))
                             .append(createElementalLore(attributes, elementalResistanceAttribute, sumArtifactAffixes.get(elementalResistanceAttribute)).color(Colors.GREEN))
                             .append(Component.text(" / ", Colors.DARK_GRAY))
                             .append(createElementalLore(attributes, elementalDamageAttribute, sumArtifactAffixes.get(elementalDamageAttribute)).color(Colors.RED))
            );
        }
        
        return builder.asIcon();
    }
    
    private @NotNull ItemStack createWeaponIcon() {
        final Hero origin = heroInstance.getOrigin();
        final Weapon weapon = origin.getWeapon();
        
        final ItemBuilder builder = showWeaponDetails ? weapon.createDetailsBuilder() : weapon.createBuilder();
        
        // Set the item name to weapon
        builder.setName(Component.text("Weapon"));
        
        // Set the first line of lore to the actual name of the weapon
        // FIXME (xanyjl @ Friday, September 18) -> Swap to Eterna method when it is added
        builder.editLore(lore -> {
            lore.addFirst(weapon.getName().color(Colors.DARK_GRAY).decoration(TextDecoration.ITALIC, false));
        });
        
        // If weapon has abilities, glow the builder
        if (weapon.hasAbilities()) {
            builder.glow();
        }
        
        builder.addLore();
        builder.addLore(ButtonComponents.left(showWeaponDetails ? "hide details" : "show details"));
        
        return builder.asIcon();
    }
    
    private static <T extends ComponentLike & Described> void appendProfileInfo(@NotNull ItemBuilder builder, @NotNull T t) {
        builder.addLore();
        builder.addLore(t.asComponent());
        builder.addWrappedLore(t.getDescription(), STYLER_AFFILIATION);
    }
    
    private static @NotNull Component createElementalLore(@NotNull Attributes attributes, @NotNull AttributeType attributeType, @Nullable Double externalValue) {
        return Component.empty()
                        .append(attributeType.format(attributes.get(attributeType) + (externalValue != null ? externalValue : 0)))
                        .append(Attributes.createExternalValueComponent(externalValue));
    }
    
}
