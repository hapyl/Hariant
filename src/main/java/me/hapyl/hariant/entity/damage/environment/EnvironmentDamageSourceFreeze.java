package me.hapyl.hariant.entity.damage.environment;

import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import org.bukkit.damage.DamageType;

public final class EnvironmentDamageSourceFreeze extends EnvironmentDamageSource {
    
    private static final DeathMessage DEATH_MESSAGE = DeathMessage.createWithDefaultKiller("{player} froze to death");
    
    public EnvironmentDamageSourceFreeze() {
        super(DamageType.FREEZE, DEATH_MESSAGE, ElementType.ICE, 50);
    }
    
}
