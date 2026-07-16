package me.hapyl.hariant.entity.damage;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface DamageSourceIdentity extends Keyed, Named {
    
    @NotNull DamageSourceIdentity COMMAND = create(Key.ofString("command"), Component.text("Command"), DeathMessage.create("{player} was killed [by {killer}]"));
    
    @Override
    @NotNull Key getKey();
    
    @Override
    @NotNull Component getName();
    
    @NotNull DeathMessage getDeathMessage();
    
    static @NotNull DamageSourceIdentity create(@NotNull Key key, @NotNull Component name, @NotNull DeathMessage deathMessage) {
        return new DamageSourceIdentityImpl(key, name, deathMessage);
    }
    
    static <K extends Keyed & Named> @NotNull DamageSourceIdentity create(@NotNull K keyedNamed, @NotNull DeathMessage deathMessage) {
        return new DamageSourceIdentityImpl(keyedNamed.getKey(), keyedNamed.getName(), deathMessage);
    }
    
}