package me.hapyl.hariant.entity.player;

import me.hapyl.hariant.util.Prefixed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public enum EliminationFeedback implements Prefixed {
    
    KILL(Component.text("⚔", NamedTextColor.DARK_RED)) {
        @Override
        public void feedback(@NotNull HariantPlayer player, @NotNull HariantPlayer victim) {
            super.feedback(player, victim);
            
            player.playSound(Sound.ITEM_SHIELD_BLOCK, 1.75f);
        }
    },
    
    ASSIST(Component.text("🌿", NamedTextColor.GREEN)) {
        @Override
        public void feedback(@NotNull HariantPlayer player, @NotNull HariantPlayer victim) {
            super.feedback(player, victim);
            
            player.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.75f);
        }
    };
    
    private final Component prefix;
    
    EliminationFeedback(@NotNull Component prefix) {
        this.prefix = prefix;
    }
    
    @NotNull
    @Override
    public Component getPrefix() {
        return prefix;
    }
    
    @OverridingMethodsMustInvokeSuper
    public void feedback(@NotNull HariantPlayer player, @NotNull HariantPlayer victim) {
        player.sendSubtitle(
                Component.empty()
                         .append(prefix)
                         .appendSpace()
                         .append(victim.asDeathComponent()),
                0, 10, 5
        );
    }
    
}
