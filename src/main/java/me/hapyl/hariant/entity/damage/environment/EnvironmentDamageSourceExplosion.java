package me.hapyl.hariant.entity.damage.environment;

import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import org.bukkit.damage.DamageType;

public final class EnvironmentDamageSourceExplosion extends EnvironmentDamageSource {
    
    private static final DeathMessage DEATH_MESSAGE = DeathMessage.createWithDefaultKiller("{player} exploded to death");
    
    public EnvironmentDamageSourceExplosion() {
        super(DamageType.EXPLOSION, DEATH_MESSAGE, ElementType.PHYSICAL, 250);
    }
    
}