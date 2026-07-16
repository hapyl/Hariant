package me.hapyl.hariant.entity.damage.environment;

import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import org.bukkit.damage.DamageType;

public final class EnvironmentDamageSourceInWall extends EnvironmentDamageSource {
    
    private static final DeathMessage DEATH_MESSAGE = DeathMessage.createWithDefaultKiller("{player} suffocated");
    
    public EnvironmentDamageSourceInWall() {
        super(DamageType.IN_WALL, DEATH_MESSAGE, ElementType.PHYSICAL, 50);
    }
    
}
