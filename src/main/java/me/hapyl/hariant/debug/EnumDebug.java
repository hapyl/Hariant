package me.hapyl.hariant.debug;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import me.hapyl.hariant.entity.heal.HealingSource;
import me.hapyl.hariant.entity.player.HariantPlayer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public enum EnumDebug implements Debug {
    
    RESET_COOLDOWNS {
        @Override
        public void debug(@NotNull HariantPlayer player, @NotNull ArgumentList args) {
            player.resetCooldowns();
            player.resetUltimate();
            player.chargeUltimate();
            
            player.getHero().onDebugCooldownReset(player);
            
            player.messageSuccess(Component.text("Reset cooldowns and charged ultimate!"));
        }
    },
    
    HEAL {
        @Override
        public void debug(@NotNull HariantPlayer player, @NotNull ArgumentList args) {
            final double healing = args.get(0).toDouble(1);
            
            player.heal(HealingSource.create(healing, Component.text("Debug")));
            player.messageSuccess(Component.text("Healed for %.0f!".formatted(healing), Colors.GREEN));
        }
    },
    
    DAMAGE {
        private static class DebugDamageSource extends DamageSourceImpl {
            
            private static final DamageSourceIdentity DAMAGE_SOURCE_IDENTITY = DamageSourceIdentity.create(Key.ofString("command"), Component.text("Command"), DeathMessage.create("{player} was killed [by {killer}]"));
            
            DebugDamageSource(double damage, ElementType elementType, DamageType damageType) {
                super(DAMAGE_SOURCE_IDENTITY, null, damageType, elementType, DamageComponents.ofCommon(), Set.of(), damage, 0);
            }
            
        }
        
        @Override
        public void debug(@NotNull HariantPlayer player, @NotNull ArgumentList args) {
            final double damage = args.get(0).toDouble(1);
            final ElementType elementType = args.get(1).toEnum(ElementType.class, ElementType.PHYSICAL);
            final DamageType damageType = args.get(2).toEnum(DamageType.class, DamageType.MELEE);
            
            player.damage(new DebugDamageSource(damage, elementType, damageType));
            
            player.messageSuccess(
                    Component.empty()
                             .append(Component.text("Dealt "))
                             .append(Component.text("%,f".formatted(damage), Colors.RED))
                             .appendSpace()
                             .append(elementType.asComponentDamage())
                             .append(Component.text(" (", Colors.DARK_GRAY))
                             .append(damageType.getName().color(Colors.GRAY))
                             .append(Component.text(")", Colors.DARK_GRAY))
                             .append(Component.text(" to you!"))
            );
        }
    }
    
}
