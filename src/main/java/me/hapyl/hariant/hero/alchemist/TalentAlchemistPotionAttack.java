package me.hapyl.hariant.hero.alchemist;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

public final class TalentAlchemistPotionAttack extends TalentAlchemistPotionAttribute {
    TalentAlchemistPotionAttack(@NotNull TalentAbyssalBottle talent) {
        super(
                talent,
                "attack",
                Component.text("Potion of Strength"),
                Color.fromRGB(148, 16, 23),
                25,
                AttributeType.ATTACK,
                Decimal.ofPercentage(100),
                Decimal.ofSeconds(10)
        );
    }
}
