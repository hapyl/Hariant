package me.hapyl.hariant.hero.alchemist;

import me.hapyl.hariant.entity.effect.status.EnumStatusEffect;
import me.hapyl.hariant.entity.player.HariantPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

public final class TalentAlchemistPotionInvisibility extends TalentAlchemistPotion {
    TalentAlchemistPotionInvisibility(@NotNull TalentAbyssalBottle talent) {
        super(
                talent,
                "invisibility",
                Component.text("Potion of Invisibility"),
                Color.fromRGB(110, 138, 150),
                15
        );
        
        this.setDescription(EnumStatusEffect.INVISIBILITY.getDescription());
    }
    
    @NotNull
    @Override
    public AlchemistPotionInstance drink(@NotNull HariantPlayer player, @NotNull HeroDataAlchemist heroData) {
        player.addEffect(EnumStatusEffect.INVISIBILITY, this.getDuration());
        
        return new AlchemistPotionInvisibilityInstance(player);
    }
    
    public class AlchemistPotionInvisibilityInstance extends AlchemistPotionInstance {
        
        AlchemistPotionInvisibilityInstance(@NotNull HariantPlayer player) {
            super(player, TalentAlchemistPotionInvisibility.this);
        }
        
        @Override
        public boolean tick() {
            super.tick();
            
            // A little wonky way of checking for whether player has invisibility or not,
            // but there isn't a callback for that
            if (!player.hasEffect(EnumStatusEffect.INVISIBILITY)) {
                return true;
            }
            
            return false;
        }
    }
    
}