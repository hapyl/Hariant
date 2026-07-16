package me.hapyl.hariant.hero.alchemist;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

public class TalentAlchemistPotionAttribute extends TalentAlchemistPotion {
    
    private static final Key ATTRIBUTE_MODIFIER_KEY = Key.ofString("alchemist_potion");
    
    protected final AttributeType attributeType;
    protected final Decimal attributeIncrease;
    
    TalentAlchemistPotionAttribute(
            @NotNull TalentAbyssalBottle talent,
            @NotNull String key,
            @NotNull Component name,
            @NotNull Color potionColor,
            double abyssalCorrosion,
            @NotNull AttributeType attributeType,
            @NotNull Decimal attributeIncrease,
            @NotNull Decimal duration
    ) {
        super(talent, key, name, potionColor, abyssalCorrosion);
        
        this.attributeType = attributeType;
        this.attributeIncrease = attributeIncrease;
        
        this.setDuration(duration.intValue());
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Increases your "))
                         .append(attributeType)
                         .append(Component.text(" by "))
                         .append(attributeIncrease)
                         .append(Component.text(" for "))
                         .append(duration)
                         .append(Component.text("."))
        );
    }
    
    @NotNull
    @Override
    public AlchemistPotionInstance drink(@NotNull HariantPlayer player, @NotNull HeroDataAlchemist heroData) {
        player.getAttributes().addModifier(
                ATTRIBUTE_MODIFIER_KEY,
                this.getDuration(),
                player,
                adder -> adder.of(attributeType, AttributeModifierType.ADDITIVE, attributeIncrease.doubleValue())
        );
        
        return new AlchemistPotionInstance(player, this);
    }
    
}
