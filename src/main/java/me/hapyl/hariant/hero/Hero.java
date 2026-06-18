package me.hapyl.hariant.hero;

import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.eterna.module.text.SmallCaps;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.annotate.AutoRegisteredListener;
import me.hapyl.hariant.annotate.Singleton;
import me.hapyl.hariant.annotate.StrictNamingConvention;
import me.hapyl.hariant.attribute.Attributable;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.debug.DebugListener;
import me.hapyl.hariant.entity.HeadComponent;
import me.hapyl.hariant.entity.SmallCapsComponent;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.handler.HariantEventHandler;
import me.hapyl.hariant.inventory.item.ItemCreator;
import me.hapyl.hariant.profile.ui.ActionbarSupplier;
import me.hapyl.hariant.registry.Registrable;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentIndex;
import me.hapyl.hariant.talent.TalentPassive;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.weapon.Weapon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@AutoRegisteredListener
@StrictNamingConvention(startsWith = "Hero")
public abstract class Hero
        implements
        Keyed, Named, Described, Attributable,
        HariantEventHandler, ComponentLike, HeadComponent,
        SmallCapsComponent, ActionbarSupplier, ItemCreator, DebugListener, Registrable {
    
    private final Key key;
    private final Component name;
    private final Attributes attributes;
    private final HeroProfile profile;
    private final HeroEquipment equipment;
    private final Weapon weapon;
    private final Component smallCaps;
    
    private final Map<TalentIndex, Talent> talentsMapped;
    
    private Component description;
    
    public Hero(@NotNull Key key, @NotNull Component name, @NotNull Attributes attributes, @NotNull Weapon weapon) {
        this.key = key;
        this.name = name;
        this.description = Described.defaultValue();
        this.attributes = attributes;
        this.profile = new HeroProfile(this);
        this.equipment = new HeroEquipment();
        this.weapon = weapon;
        this.smallCaps = Component.text(SmallCaps.format(Components.toString(name)));
        
        this.talentsMapped = Map.of(
                TalentIndex.TALENT_1, this.getFirstTalent(),
                TalentIndex.TALENT_2, this.getSecondTalent(),
                TalentIndex.TALENT_3, this.getThirdTalent(),
                TalentIndex.TALENT_PASSIVE, this.getPassiveTalent(),
                TalentIndex.TALENT_ULTIMATE, this.getUltimateTalent()
        );
        
        AutoRegisteredListener.Registry.register(this);
        StrictNamingConvention.Validator.validate(this);
    }
    
    @Override
    @NotNull
    public ItemBuilder createBuilder() {
        final ItemBuilder builder = ItemBuilder.playerHead(equipment.getHeadTexture());
        builder.setName(name);
        builder.addLore();
        
        // Profile
        builder.addLore(Component.text("ᴘʀᴏꜰɪʟᴇ", Colors.DEFAULT_COLOR, TextDecoration.BOLD));
        builder.addLore(Component.text(" Archetype: ", Colors.LIGHT_GRAY).append(profile.getArchetype()));
        builder.addLore(Component.text(" Element: ", Colors.LIGHT_GRAY).append(profile.getElementType()));
        builder.addLore(Component.text(" Affiliation: ", Colors.LIGHT_GRAY).append(profile.getAffiliation()));
        builder.addLore(Component.text(" Gender: ", Colors.LIGHT_GRAY).append(profile.getGender()));
        builder.addLore(Component.text(" Race: ", Colors.LIGHT_GRAY).append(profile.getRace()));
        builder.addLore();
        
        // Attributes
        builder.addLore(Component.text("ᴀᴛᴛʀɪʙᴜᴛᴇꜱ", Colors.DEFAULT_COLOR, TextDecoration.BOLD));
        builder.addLore(attributes.createLore(AttributeType.MAX_HEALTH));
        builder.addLore(attributes.createLore(AttributeType.ATTACK));
        builder.addLore(attributes.createLore(AttributeType.DEFENSE));
        builder.addLore(attributes.createLore(AttributeType.MOVEMENT_SPEED));
        builder.addLore();
        
        // Description
        builder.addLore(Component.text("ᴅᴇꜱᴄʀɪᴘᴛɪᴏɴ", Colors.DEFAULT_COLOR, TextDecoration.BOLD));
        builder.addWrappedLore(description, HariantConstants.COMPONENT_STYLER_DESCRIPTION);
        
        return builder;
    }
    
    @NotNull
    @Override
    public Component asSmallCaps() {
        return smallCaps;
    }
    
    @Singleton
    @NotNull
    public abstract Talent getFirstTalent();
    
    @Singleton
    @NotNull
    public abstract Talent getSecondTalent();
    
    @Singleton
    @NotNull
    public abstract Talent getThirdTalent();
    
    @Singleton
    @NotNull
    public abstract TalentPassive getPassiveTalent();
    
    @Singleton
    @NotNull
    public abstract TalentUltimate getUltimateTalent();
    
    @NotNull
    public Talent getTalent(@NotNull TalentIndex index) {
        return talentsMapped.get(index);
    }
    
    @NotNull
    public HeroProfile getProfile() {
        return profile;
    }
    
    @NotNull
    public HeroEquipment getEquipment() {
        return equipment;
    }
    
    @NotNull
    @Override
    public Key getKey() {
        return key;
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public Component getDescription() {
        return description;
    }
    
    @Override
    public void setDescription(@NotNull Component description) {
        this.description = description;
    }
    
    @NotNull
    @Override
    public Attributes getAttributes() {
        return attributes;
    }
    
    @Override
    public final int hashCode() {
        return Objects.hashCode(this.key);
    }
    
    @Override
    public final boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final Hero that = (Hero) object;
        return Objects.equals(this.key, that.key);
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return Component.empty()
                        .append(profile.getArchetype().getPrefixStyled())
                        .appendSpace()
                        .append(smallCaps);
    }
    
    @NotNull
    @Override
    public Component asHeadComponent() {
        return equipment.asHeadComponent();
    }
    
    @NotNull
    public Weapon getWeapon() {
        return weapon;
    }
    
    @NotNull
    public Weapon getWeapon(@NotNull HariantPlayer player) {
        return weapon;
    }
    
    public int getWeaponSlot(@NotNull HariantPlayer player) {
        return HariantConstants.DEFAULT_WEAPON_SLOT;
    }
    
    public void giveWeapon(@NotNull HariantPlayer player) {
        this.giveWeapon(player, this.getWeapon(player));
    }
    
    public void giveWeapon(@NotNull HariantPlayer player, @NotNull Weapon weapon) {
        final PlayerInventory inventory = player.getInventory();
        final int weaponSlot = this.getWeaponSlot(player);
        
        inventory.setItem(weaponSlot, weapon.createItem());
        inventory.setHeldItemSlot(weaponSlot);
    }
    
    @Override
    public void handleItemHeldEvent(@NotNull HariantPlayer player, @NotNull HeroInstance heroInstance, @NotNull PlayerItemHeldEvent ev) {
        final int newSlot = ev.getNewSlot();
        
        // The talent buttons are now hardcoded, so no need to converts slots, 0-2 are always talents
        final Talent talent = switch (newSlot) {
            case 0 -> this.getFirstTalent();
            case 1 -> this.getSecondTalent();
            case 2 -> this.getThirdTalent();
            default -> null;
        };
        
        // Execute the talent if there is one
        if (talent != null) {
            talent.execute0(player);
        }
    }
    
    @Override
    public void handleSwapHandItemsEvent(@NotNull HariantPlayer player, @NotNull HeroInstance heroInstance, @NotNull PlayerSwapHandItemsEvent ev) {
        getUltimateTalent().execute0(player);
    }
    
    @NotNull
    @Override
    public List<Component> supplyActionbar(@NotNull HariantPlayer player) {
        return List.of();
    }
    
    @Override
    public void debugOnCooldownReset(@NotNull HariantPlayer player) {
    }
    
    @Override
    public void onRegister() {
    }
    
    @Override
    public void onUnregister() {
    }
    
}