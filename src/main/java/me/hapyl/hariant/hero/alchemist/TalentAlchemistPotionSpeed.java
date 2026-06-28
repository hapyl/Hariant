package me.hapyl.hariant.hero.alchemist;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

public final class TalentAlchemistPotionSpeed extends TalentAlchemistPotionAttribute {
    
    TalentAlchemistPotionSpeed(@NotNull TalentAbyssalBottle talent) {
        super(
                talent,
                "speed",
                Component.text("Potion of Speed"),
                Color.fromRGB(78, 160, 204),
                15,
                AttributeType.MOVEMENT_SPEED,
                Decimal.ofPercentage(40),
                Decimal.ofSeconds(10)
        );
        
        setTalentType(TalentType.ENHANCE);
    }
    
}
