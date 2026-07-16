package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantGameInstanceStateEvent;
import me.hapyl.hariant.game.GameInstanceState;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class AchievementFirstGame extends AchievementImpl implements Listener {
    
    public AchievementFirstGame(@NotNull Key key) {
        super(key, 1);
        
        setName(Component.text("First Game"));
        setDescription(Component.text("Play your very first game."));
    }
    
    @EventHandler
    public void handleHariantGameEvent(HariantGameInstanceStateEvent ev) {
        if (ev.getState() != GameInstanceState.FINISHED) {
            return;
        }
        
        Hariant.getPlayers().map(HariantPlayer::getProfile).forEach(this::progress);
    }
    
}