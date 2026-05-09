package me.hapyl.hariant.weapon;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.entity.Attacker;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.entity.player.LifecyclePlayer;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.inventory.item.ItemCreator;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.weapon.ability.Ability;
import me.hapyl.hariant.weapon.ability.AbilityType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

/// Represents a base {@link Weapon} class, which is used by a {@link Hero} and stores an appearance as well as damage data by
/// directly extending {@link NormalAttack}.
///
/// <pre>
/// Weapon {@code (package-private)}
/// |- WeaponMelee
/// |- WeaponRange
///     |- WeaponBow
/// </pre>
public class Weapon implements
        Keyed, ItemCreator, Icon, Named,
        Described, LifecyclePlayer, Attacker {
    
    protected final NormalAttack normalAttack;
    
    private final Key key;
    private final Icon icon;
    private final Map<AbilityType, Ability> abilities;
    
    private Component name;
    private Component description;
    
    Weapon(@NotNull Key key, @NotNull Icon icon, @NotNull NormalAttack normalAttack) {
        this.key = key;
        this.icon = icon;
        this.normalAttack = normalAttack;
        this.name = Named.defaultValue();
        this.description = Described.defaultValue();
        this.abilities = Maps.newEnumMap(AbilityType.class);
    }
    
    @NotNull
    @Override
    public NormalAttack getMeleeAttack() {
        return normalAttack;
    }
    
    public void setAbility(@NotNull AbilityType abilityType, @NotNull Ability ability) {
        abilities.put(abilityType, ability);
    }
    
    @Nullable
    public Ability getAbility(@NotNull AbilityType abilityType) {
        return abilities.get(abilityType);
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @Override
    public void setName(@NotNull Component name) {
        this.name = name;
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
    public final Key getKey() {
        return key;
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
        
        final Weapon that = (Weapon) object;
        return Objects.equals(this.key, that.key);
    }
    
    @NotNull
    @Override
    public final ItemBuilder createBuilder() {
        final ItemBuilder builder = this.createBuilder0();
        
        builder.setName(name);
        builder.addLore();
        
        // Add description
        builder.addWrappedLore(description);
        builder.addLore();
        
        // Add abilities
        if (!abilities.isEmpty()) {
            builder.addLore(Component.text(abilities.size() == 1 ? "ᴀʙɪʟɪᴛʏ:" : "ᴀʙɪʟɪᴛɪᴇꜱ:", NamedTextColor.YELLOW, TextDecoration.BOLD));
            builder.addLore();
            
            int index = 0;
            
            for (Map.Entry<AbilityType, Ability> entry : abilities.entrySet()) {
                final AbilityType abilityType = entry.getKey();
                final Ability ability = entry.getValue();
                
                if (index++ != 0) {
                    builder.addLore();
                }
                
                builder.addLore(
                        Component.empty()
                                 .append(Component.text("✦ ", NamedTextColor.GOLD))
                                 .append(ability.getName().color(NamedTextColor.GOLD))
                                 .append(Component.text(" (", NamedTextColor.DARK_GRAY))
                                 .append(abilityType.getName().color(NamedTextColor.DARK_GRAY))
                                 .append(Component.text(")", NamedTextColor.DARK_GRAY))
                );
                
                builder.addWrappedLore(ability.getDescription(), _component -> Component.text("  ").append(_component.style(ItemBuilder.DEFAULT_LORE_STYLE)));
            }
        }
        
        // Add flavor text
        // TODO @Apr 15, 2026 (xanyjl) ->
        
        return builder;
    }
    
    @NotNull
    @Override
    public final ItemStack createItem() {
        return this.createBuilder().asItemStack();
    }
    
    @NotNull
    @Override
    public final ItemStack createIcon() {
        return this.createBuilder().asIcon();
    }
    
    @Override
    public void onCreate(@NotNull HariantPlayer player) {
    }
    
    @Override
    public void onDestroy(@NotNull HariantPlayer player) {
    }
    
    public void startCooldown(@NotNull HariantPlayer player, int cooldown) {
        player.setCooldown(this.getKey(), cooldown);
    }
    
    public int getCooldown(@NotNull HariantPlayer player) {
        return player.getCooldownTimeLeft(this.getKey());
    }
    
    public boolean hasCooldown(@NotNull HariantPlayer player) {
        return player.hasCooldown(this.getKey());
    }
    
    @NotNull
    protected ItemBuilder createBuilder0() {
        return icon.createBuilder().setCooldownKey(key);
    }
    
}
