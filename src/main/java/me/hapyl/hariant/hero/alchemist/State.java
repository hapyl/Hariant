package me.hapyl.hariant.hero.alchemist;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentRegistry;
import org.jetbrains.annotations.NotNull;

public enum State {
    
    NORMAL {
        @Override
        public void apply(@NotNull HariantPlayer player) {
            // Return normal items
            player.resetHotBar();
        }
    },
    
    SELECT_POTION {
        @Override
        public void apply(@NotNull HariantPlayer player) {
            // Give potions
            TalentRegistry.ABYSSAL_BOTTLE.giveAlchemistPotions(player);
        }
    };
    
    public void apply(@NotNull HariantPlayer player) {
    }
    
}
