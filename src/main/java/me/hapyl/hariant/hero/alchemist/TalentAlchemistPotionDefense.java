package me.hapyl.hariant.hero.alchemist;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

public final class TalentAlchemistPotionDefense extends TalentAlchemistPotionAttribute {
    TalentAlchemistPotionDefense(@NotNull TalentAbyssalBottle talent) {
        super(
                talent,
                "defense",
                Component.text("Potion of Defense"),
                Color.fromRGB(76, 105, 64),
                20,
                AttributeType.DEFENSE,
                Decimal.ofPercentage(500),
                Decimal.ofSeconds(10)
        );
    }
}
