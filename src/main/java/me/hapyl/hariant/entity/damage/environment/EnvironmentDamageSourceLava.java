package me.hapyl.hariant.entity.damage.environment;

import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import org.bukkit.damage.DamageType;

public final class EnvironmentDamageSourceLava extends EnvironmentDamageSource {
    
    private static final DeathMessage DEATH_MESSAGE = DeathMessage.create("{player} tried to swim in lava [while running from {killer}]");
    
    public EnvironmentDamageSourceLava() {
        super(DamageType.LAVA, DEATH_MESSAGE, ElementType.FIRE, 100);
    }
    
}
