package me.hapyl.hariant.hero.archer;

import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DamageSourceImpl;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class DamageSourceArcherTalent extends DamageSourceImpl {
    DamageSourceArcherTalent(@NotNull DamageSourceIdentity identity, @Nullable HariantEntity attacker, double damage, double elementUnits) {
        super(identity, attacker, DamageType.TALENT, ElementType.ELECTRIC, DamageComponent.ofCommon(), Set.of(), damage, elementUnits);
    }
}
