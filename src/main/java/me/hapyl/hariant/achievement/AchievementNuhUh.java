package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantInterruptEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class AchievementNuhUh extends AchievementImpl implements Listener {
    
    AchievementNuhUh(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Nuh-uh!"),
                Component.empty()
                         .append(Component.text("Successfully "))
                         .append(Component.text("interrupt", Colors.YELLOW))
                         .append(Component.text(" another player's action."))
        );
        
        setTier(AchievementTier.TIER_2);
    }
    
    @EventHandler
    public void handleHariantInterruptEvent(HariantInterruptEvent ev) {
        if (!(ev.getAssistSource().source() instanceof HariantPlayer player) || !ev.hasCancelledDelegates()) {
            return;
        }
        
        this.progress(player.getProfile());
    }
    
}