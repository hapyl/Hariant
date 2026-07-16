package me.hapyl.hariant.entity.damage.environment;

import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import org.bukkit.damage.DamageType;

public final class EnvironmentDamageSourceHotFloor extends EnvironmentDamageSource {
    
    private static final DeathMessage DEATH_MESSAGE = DeathMessage.createWithDefaultKiller("{player} had their toes burnt");
    
    public EnvironmentDamageSourceHotFloor() {
        super(DamageType.HOT_FLOOR, DEATH_MESSAGE, ElementType.FIRE, 50);
    }
    
}
