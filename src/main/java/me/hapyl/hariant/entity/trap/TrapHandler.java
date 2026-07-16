package me.hapyl.hariant.entity.trap;

import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.Cancel;
import me.hapyl.hariant.event.HariantAttackEvent;
import me.hapyl.hariant.event.HariantTalentPreconditionEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Input;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInputEvent;
import org.jetbrains.annotations.NotNull;

public final class TrapHandler implements Listener {
    
    @EventHandler
    public void handlePlayerInputEvent(PlayerInputEvent ev) {
        final Input input = ev.getInput();
        
        // If input isn't directional, ignore it
        if (!isInputDirectional(input)) {
            return;
        }
        
        // Handle trap
        final HariantPlayer player = Hariant.getPlayer(ev.getPlayer()).orElse(null);
        
        if (player == null) {
            return;
        }
        
        final Trap trap = player.getTrap();
        
        if (trap == null) {
            return;
        }
        
        // If escaped, remove trap
        if (trap.input(input)) {
            player.untrap(TrapEscape.ESCAPED);
        }
    }
    
    @EventHandler
    public void handleHariantTalentPreconditionEvent(HariantTalentPreconditionEvent ev) {
        final Trap trap = ev.getPlayer().getTrap();
        
        if (trap == null || !trap.blocksTalents()) {
            return;
        }
        
        ev.setCancel(Cancel.cancel(Component.text("Cannot use talents while trapped!")));
    }
    
    @EventHandler
    public void handleHariantAttackEvent(HariantAttackEvent ev) {
        final Trap trap = ev.getAttacker().getTrap();
        
        if (trap == null || !trap.blocksAttacks()) {
            return;
        }
        
        ev.setCancelled(true);
    }
    
    private static boolean isInputDirectional(@NotNull Input input) {
        return input.isForward() || input.isBackward() || input.isLeft() || input.isRight();
    }
    
}
