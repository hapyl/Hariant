package me.hapyl.hariant.game;

import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.util.MapMaker;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.task.HariantTask;
import me.hapyl.hariant.task.Scheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class GameInstanceCountdown extends HariantTask {
    
    private static final Component TITLE_COMPONENT = Component.text("ᴛʜᴇ ɢᴀᴍᴇ ꜱᴛᴀʀᴛꜱ ɪɴ...", Colors.BRAND_COLOR);
    private static final Map<Integer, Component> SUBTITLE_COMPONENTS = MapMaker.<Integer, Component>ofTreeMap()
                                                                               .put(0, Component.text("⓪").color(TextColor.color(0x00FF00)))
                                                                               .put(1, Component.text("①").color(TextColor.color(0x33FF00)))
                                                                               .put(2, Component.text("②").color(TextColor.color(0x99FF00)))
                                                                               .put(3, Component.text("③").color(TextColor.color(0xFFCC00)))
                                                                               .put(4, Component.text("④").color(TextColor.color(0xFF6600)))
                                                                               .put(5, Component.text("⑤").color(TextColor.color(0xFF0000)))
                                                                               .makeMap();
    
    private int secondsBeforeTheGameStarts = HariantConstants.GAME_START_COUNTDOWN_IN_SECONDS;
    
    public GameInstanceCountdown() {
        super(Scheduler.ofTimer(10, 20));
    }
    
    @Override
    public void run() {
        // Start the game
        if (secondsBeforeTheGameStarts <= 0) {
            Hariant.cancelCountdown(null);
            Hariant.startNewGameInstance();
        }
        // Display the countdown for all players
        else {
            final Component subtitle = SUBTITLE_COMPONENTS.getOrDefault(secondsBeforeTheGameStarts, Component.empty());
            final Title title = Title.title(TITLE_COMPONENT, subtitle, 0, 25, 5);
            final float pitch = (1.5f - (1.5f * (float) secondsBeforeTheGameStarts / HariantConstants.GAME_START_COUNTDOWN_IN_SECONDS));
            
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.showTitle(title);
            });
            
            PlayerLib.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, pitch);
            PlayerLib.playSound(Sound.BLOCK_NOTE_BLOCK_FLUTE, pitch);
            PlayerLib.playSound(Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, pitch);
        }
        
        secondsBeforeTheGameStarts--;
    }
    
    public void cancel(@Nullable PlayerProfile canceller) {
        this.cancel();
        
        if (canceller != null) {
            HariantLogger.PREFIX_INFO.broadcastMessage(
                    Component.empty()
                             .append(canceller.getNameFormatted())
                             .appendSpace()
                             .append(Component.text("cancelled the countdown!", Colors.ERROR))
            );
        }
    }
    
}