package me.hapyl.hariant.weapon;

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
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
    
    private final Key key;
    private final Icon icon;
    
    private final NormalAttack normalAttack;
    
    private Component name;
    private Component description;
    
    Weapon(@NotNull Key key, @NotNull Icon icon, @NotNull NormalAttack normalAttack) {
        this.key = key;
        this.icon = icon;
        this.normalAttack = normalAttack;
        this.name = Named.defaultValue();
        this.description = Described.defaultValue();
    }
    
    @NotNull
    @Override
    public NormalAttack getMeleeAttack() {
        return normalAttack;
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
    
    @NotNull
    protected ItemBuilder createBuilder0() {
        return icon.createBuilder().setCooldownKey(key);
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
        return player.isOnCooldown(this.getKey());
    }
    
}
