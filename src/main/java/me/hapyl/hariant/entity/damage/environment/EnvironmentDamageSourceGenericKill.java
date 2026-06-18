package me.hapyl.hariant.entity.damage.environment;

import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import org.bukkit.damage.DamageType;

public final class EnvironmentDamageSourceGenericKill extends EnvironmentDamageSource {
    
    private static final DeathMessage DEATH_MESSAGE = DeathMessage.create("{player} was killed [by {killer}]");
    
    public EnvironmentDamageSourceGenericKill() {
        super(DamageType.GENERIC_KILL, DEATH_MESSAGE, ElementType.PHYSICAL, 1_000_000);
    }
    
}
