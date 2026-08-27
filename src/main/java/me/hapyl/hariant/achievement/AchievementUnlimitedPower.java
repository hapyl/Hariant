package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class AchievementUnlimitedPower extends AchievementImpl {
    
    AchievementUnlimitedPower(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Unlimited Power!"),
                Component.empty()
                         .append(Component.text("Execute an "))
                         .append(Component.text("overcharged", Colors.ULTIMATE_OVERCHARGE))
                         .append(Component.text(" ultimate once."))
        );
    }
    
}
