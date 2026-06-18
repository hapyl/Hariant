package me.hapyl.hariant.entity.damage.environment;

import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import org.bukkit.damage.DamageType;

public final class EnvironmentDamageSourceCampfire extends EnvironmentDamageSource {
    
    private static final DeathMessage DEATH_MESSAGE = DeathMessage.createWithDefaultKiller("{player} burnt to death");
    
    public EnvironmentDamageSourceCampfire() {
        super(DamageType.CAMPFIRE, DEATH_MESSAGE, ElementType.FIRE, 50);
    }
    
}
