package me.hapyl.hariant.entity.damage.environment;

import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import org.bukkit.damage.DamageType;

public final class EnvironmentDamageSourceInFire extends EnvironmentDamageSource {
    
    private static final DeathMessage DEATH_MESSAGE = DeathMessage.createWithDefaultKiller("{player} burnt to a crisp");
    
    public EnvironmentDamageSourceInFire() {
        super(DamageType.IN_FIRE, DEATH_MESSAGE, ElementType.FIRE, 50);
    }
    
}
