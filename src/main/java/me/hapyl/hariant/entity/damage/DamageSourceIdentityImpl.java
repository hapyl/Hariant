package me.hapyl.hariant.entity.damage;

import me.hapyl.eterna.module.registry.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class DamageSourceIdentityImpl implements DamageSourceIdentity {
    
    private final Key key;
    private final Component name;
    private final DeathMessage deathMessage;
    
    DamageSourceIdentityImpl(@NotNull Key key, @NotNull Component name, @NotNull DeathMessage deathMessage) {
        this.key = key;
        this.name = name;
        this.deathMessage = deathMessage;
    }
    
    @Override
    @NotNull
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
    public DeathMessage getDeathMessage() {
        return deathMessage;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.key);
    }
    
    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final DamageSourceIdentityImpl that = (DamageSourceIdentityImpl) object;
        return Objects.equals(this.key, that.key);
    }
    
}
