package me.hapyl.hariant.entity.effect.status;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantTalentPreconditionEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class StatusEffectTalentLock extends StatusEffectImpl implements Listener {
    
    StatusEffectTalentLock() {
        super(Key.ofString("talent_lock"), Component.text("Talent Lock"), EffectType.NEUTRAL);
    }
    
    @EventHandler
    public void handleHariantTalentEvent(HariantTalentPreconditionEvent ev) {
        final HariantPlayer player = ev.getPlayer();
        
        if (!player.hasEffect(EnumStatusEffect.TALENT_LOCK)) {
            return;
        }
        
        ev.setCancelled(HariantTalentPreconditionEvent.cancel(Component.text("Cannot use talents in current state!")));
    }
}
