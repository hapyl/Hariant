package me.hapyl.hariant.entity.damage.environment;

import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import org.bukkit.damage.DamageType;

public final class EnvironmentDamageSourceCactus extends EnvironmentDamageSource {
    
    private static final DeathMessage DEATH_MESSAGE = DeathMessage.createWithDefaultKiller("{player} was prickled to death");
    
    public EnvironmentDamageSourceCactus() {
        super(DamageType.CACTUS, DEATH_MESSAGE, ElementType.PHYSICAL, 50);
    }
    
}
