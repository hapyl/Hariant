package me.hapyl.hariant.debug;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.heal.HealingSource;
import me.hapyl.hariant.entity.player.HariantPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public enum EnumDebug implements Debug {
    
    RESET_COOLDOWNS {
        @Override
        public void debug(@NotNull HariantPlayer player, @NotNull ArgumentList args) {
            player.resetCooldowns();
            player.resetUltimate();
            player.chargeUltimate();
            
            player.getHero().debugOnCooldownReset(player);
            
            player.messageSuccess(Component.text("Reset cooldowns and charged ultimate!"));
        }
    },
    
    HEAL {
        @Override
        public void debug(@NotNull HariantPlayer player, @NotNull ArgumentList args) {
            final double healing = args.get(0).toDouble(1);
            
            player.heal(HealingSource.create(healing, null));
            player.messageSuccess(Component.text("Healed for %.0f!".formatted(healing), NamedTextColor.GREEN));
        }
    },
    
    DAMAGE {
        @Override
        public void debug(@NotNull HariantPlayer player, @NotNull ArgumentList args) {
            final double damage = args.get(0).toDouble(1);
            final ElementType elementType = args.get(1).toEnum(ElementType.class, ElementType.PHYSICAL);
            final DamageType damageType = args.get(2).toEnum(DamageType.class, DamageType.MELEE);
            
            player.damage(
                    DamageSource.builder(DamageSourceIdentity.COMMAND, damage)
                                .elementType(elementType)
                                .damageType(damageType)
                                .build()
            );
            
            player.messageSuccess(
                    Component.empty()
                             .append(Component.text("Dealt "))
                             .append(Component.text(damage, NamedTextColor.RED))
                             .appendSpace()
                             .append(elementType.asComponentDamage())
                             .append(Component.text(" (", NamedTextColor.DARK_GRAY))
                             .append(damageType.getName().color(NamedTextColor.GRAY))
                             .append(Component.text(")", NamedTextColor.DARK_GRAY))
                             .append(Component.text(" to you!"))
            );
        }
    }
    
}
