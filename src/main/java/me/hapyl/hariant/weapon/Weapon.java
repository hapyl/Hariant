package me.hapyl.hariant.weapon;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.entity.Attacker;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.cooldown.HariantCooldown;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.entity.player.LifecyclePlayer;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.inventory.item.ItemCreator;
import me.hapyl.hariant.inventory.item.ItemDetailsCreator;
import me.hapyl.hariant.registry.Registrable;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.field.DisplayFieldInstance;
import me.hapyl.hariant.util.field.DisplayFieldProvider;
import me.hapyl.hariant.weapon.ability.Ability;
import me.hapyl.hariant.weapon.ability.AbilityType;
import me.hapyl.hariant.weapon.projectile.WeaponRangeProjectile;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/// Represents a base {@link Weapon} class, which is used by a {@link Hero} and stores an appearance as well as damage data by
/// directly extending {@link NormalAttack}.
///
/// <pre>
/// Weapon {@code (package-private)}
/// |- {@link WeaponMelee}
/// |- {@link WeaponRange}
///     |- {@link WeaponBow}
///     |- {@link WeaponRangeProjectile}
/// </pre>
public class Weapon implements
        Keyed, ItemCreator, Icon, Named,
        Described, LifecyclePlayer, Attacker, HariantCooldown,
        DisplayFieldProvider, Registrable, ItemDetailsCreator {
    
    protected final NormalAttack normalAttack;
    
    private final Key key;
    private final Icon icon;
    private final Map<AbilityType, Ability> abilities;
    
    private @Unmodifiable List<? extends DisplayFieldInstance> displayFields;
    
    private Component name;
    private Component description;
    
    Weapon(@NotNull Key key, @NotNull Icon icon, @NotNull NormalAttack normalAttack) {
        this.key = key;
        this.icon = icon;
        this.normalAttack = normalAttack;
        this.name = Named.defaultValue();
        this.description = Described.defaultValue();
        this.abilities = Maps.newEnumMap(AbilityType.class);
        this.displayFields = List.of();
    }
    
    @Override
    public @NotNull NormalAttack getMeleeAttack() {
        return normalAttack;
    }
    
    public void setAbility(@NotNull AbilityType abilityType, @NotNull Ability ability) {
        abilities.put(abilityType, ability);
    }
    
    public @Nullable Ability getAbility(@NotNull AbilityType abilityType) {
        return abilities.get(abilityType);
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public void setName(@NotNull Component name) {
        this.name = name;
    }
    
    @Override
    public @NotNull Component getDescription() {
        return description;
    }
    
    @Override
    public void setDescription(@NotNull Component description) {
        this.description = description;
    }
    
    @Override
    public @NotNull
    final Key getKey() {
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
    
    @Override
    public final @NotNull ItemBuilder createBuilder() {
        final ItemBuilder builder = this.createBuilder0();
        
        builder.setName(name);
        
        // Add description
        builder.addLore();
        builder.addWrappedLore(description, HariantConstants.COMPONENT_STYLER_DESCRIPTION);
        
        // Add abilities
        if (!abilities.isEmpty()) {
            builder.addLore();
            builder.addLore(Component.text(abilities.size() == 1 ? "Ability" : "Abilities", Colors.GREEN));
            
            int index = 0;
            
            for (Map.Entry<AbilityType, Ability> entry : abilities.entrySet()) {
                final AbilityType abilityType = entry.getKey();
                final Ability ability = entry.getValue();
                
                if (index++ != 0) {
                    builder.addLore();
                }
                
                builder.addLore(
                        Component.empty()
                                 .append(Component.text("✦ ", Colors.GOLD))
                                 .append(ability.getName().color(Colors.GOLD))
                                 .appendSpace()
                                 .append(abilityType)
                );
                
                builder.addWrappedLore(ability.getDescription(), HariantConstants.COMPONENT_STYLER_DESCRIPTION_PADDING_3);
            }
        }
        
        // Add flavor text
        // TODO @Apr 15, 2026 (xanyjl) ->
        
        return builder;
    }
    
    @Override
    public final @NotNull ItemStack createItem() {
        return this.createBuilder().asItemStack();
    }
    
    @Override
    public final @NotNull ItemStack createIcon() {
        return this.createBuilder().asIcon();
    }
    
    @Override
    public void onCreate(@NotNull HariantPlayer player) {
    }
    
    @Override
    public void onDestroy(@NotNull HariantPlayer player) {
    }
    
    public void startCooldown(@NotNull HariantPlayer player, int cooldown) {
        // TODO (xanyjl @ Saturday, July 18) -> Maybe introduce ATTACK SPEED, but it would require different scaling (0% -> 100%
        player.setCooldown(this, cooldown, null);
    }
    
    public int getCooldown(@NotNull HariantPlayer player) {
        return player.getCooldownTimeLeft(this);
    }
    
    public boolean hasCooldown(@NotNull HariantPlayer player) {
        return player.hasCooldown(this);
    }
    
    @Override
    public @NotNull Key getCooldownKey() {
        return key;
    }
    
    @Override
    public int getCooldown() {
        return normalAttack.getAttackCooldown();
    }
    
    public @NotNull AbilityType getPrimaryAbilityType() {
        return AbilityType.RIGHT_CLICK;
    }
    
    public @NotNull Component getPrimaryAbilityCooldownComponent() {
        return Component.empty();
    }
    
    public final @Nullable Component getPrimaryAbilityCooldown(@NotNull HariantPlayer player) {
        final Ability ability = abilities.get(this.getPrimaryAbilityType());
        
        if (ability == null) {
            return null;
        }
        
        final int cooldown = player.getCooldownTimeLeft(ability);
        
        // Don't display the cooldown component if player has no cooldown
        if (cooldown <= 0) {
            return null;
        }
        
        return Component.empty()
                        .append(this.getPrimaryAbilityCooldownComponent())
                        .appendSpace()
                        .append(Component.text(Tick.format(cooldown), Colors.TICK));
    }
    
    public boolean hasAbilities() {
        return !abilities.isEmpty();
    }
    
    @Override
    public void onRegister() {
        this.displayFields = DisplayFieldInstance.parse(this);
    }
    
    @OverridingMethodsMustInvokeSuper
    @Override
    public void initDisplayFields(@NotNull List<? super DisplayFieldInstance> displayFields) {
        // Append Melee DMG & Cooldown
        displayFields.add(DisplayFieldInstance.create(Component.text("Melee DMG"), normalAttack.format()));
        displayFields.add(DisplayFieldInstance.create(Component.text("Melee Attack Speed"), normalAttack.formatAttackSpeed()));
    }
    
    @Override
    public @NotNull ItemBuilder createDetailsBuilder() {
        final ItemBuilder builder = icon.createBuilder();
        
        builder.setName(this.getName());
        builder.addLore(Component.text("Details", Colors.DARK_GRAY));
        
        // Append display fields
        if (!displayFields.isEmpty()) {
            builder.addLore();
            builder.addLore(Component.text("Attributes", Colors.GOLD));
            
            displayFields.forEach(instance -> builder.addLore(instance.asComponent()));
        }
        
        return builder;
    }
    
    protected @NotNull ItemBuilder createBuilder0() {
        return icon.createBuilder().setCooldownKey(key);
    }
    
}
