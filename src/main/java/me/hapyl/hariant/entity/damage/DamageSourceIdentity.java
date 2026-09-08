package me.hapyl.hariant.entity.damage;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface DamageSourceIdentity extends Keyed, Named {
    
    @Override
    @NotNull Key getKey();
    
    @Override
    @NotNull Component getName();
    
    @NotNull DeathMessage getDeathMessage();
    
    static @NotNull DamageSourceIdentity create(@NotNull Key key, @NotNull Component name, @NotNull DeathMessage deathMessage) {
        return new DamageSourceIdentityImpl(key, name, deathMessage);
    }
    
    static @NotNull DamageSourceIdentity createOfNamed(@NotNull Named named, @NotNull Key key, @NotNull DeathMessage deathMessage) {
        return create(key, named.getName(), deathMessage);
    }
    
}