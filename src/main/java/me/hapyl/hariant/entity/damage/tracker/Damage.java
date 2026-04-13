package me.hapyl.hariant.entity.damage.tracker;

import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public class Damage implements ComponentLike {
    
    protected final DamageSourceIdentity identity;
    protected double damage;
    
    Damage(@NotNull DamageSourceIdentity identity) {
        this.identity = identity;
        this.damage = 0;
    }
    
    @NotNull
    public DamageSourceIdentity getIdentity() {
        return identity;
    }
    
    public double getDamage() {
        return damage;
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return Component.empty()
                        .append(identity.getName())
                        .append(Component.text(" %,.0f (%s)".formatted(damage, identity.getKeyAsString())));
    }
    
}
