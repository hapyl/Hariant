package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantFerocityEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class AchievementChainOfCommand extends AchievementImpl implements Listener {
    
    public AchievementChainOfCommand(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Chain of Command"),
                Component.empty()
                         .append(Component.text("Trigger a "))
                         .append(AttributeType.FEROCITY)
                         .append(Component.text(" attack."))
        );
    }
    
    @EventHandler
    public void handleHariantFerocityEvent(HariantFerocityEvent ev) {
        if (!(ev.getFerocitySource().getSource() instanceof HariantPlayer player)) {
            return;
        }
        
        this.progress(player.getProfile());
    }
    
}
