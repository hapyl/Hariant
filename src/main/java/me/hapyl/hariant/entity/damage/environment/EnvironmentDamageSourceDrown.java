package me.hapyl.hariant.entity.damage.environment;

import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import org.bukkit.damage.DamageType;

public final class EnvironmentDamageSourceDrown extends EnvironmentDamageSource {
    
    private static final DeathMessage DEATH_MESSAGE = DeathMessage.createWithDefaultKiller("{player} drowned");
    
    public EnvironmentDamageSourceDrown() {
        super(DamageType.DROWN, DEATH_MESSAGE, ElementType.WATER, 50);
    }
    
}
