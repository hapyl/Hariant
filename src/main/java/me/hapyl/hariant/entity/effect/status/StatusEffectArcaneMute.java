package me.hapyl.hariant.entity.effect.status;

import me.hapyl.eterna.module.event.protocol.PacketSendEvent;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantTalentPreconditionEvent;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class StatusEffectArcaneMute extends StatusEffectImpl implements Listener {
    
    public static final Component ARCANE_MUTE_PREFIX = Component.text("⛧", Colors.BLOOD_PURPLE);
    
    StatusEffectArcaneMute() {
        super(Key.ofString("arcane_mute"), Component.text("Arcane Mute"), EffectType.DEBUFF);
    }
    
    @Override
    public void onApply(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int duration) {
        // Fx
        entity.sendTitleSubtitle(ARCANE_MUTE_PREFIX, Component.text("Shhhhh...", Colors.LIGHT_PURPLE), 10, 20, 10);
        entity.playSound(Sound.ENTITY_SILVERFISH_HURT, 0.0f);
    }
    
    @EventHandler
    public void handlePacketSendEvent(PacketSendEvent ev) {
        final ClientboundSoundPacket packet = ev.getPacket(ClientboundSoundPacket.class).orElse(null);
        
        if (packet == null) {
            return;
        }
        
        final HariantPlayer player = Hariant.getPlayer(ev.getPlayer()).orElse(null);
        
        if (player == null || !player.hasEffect(EnumStatusEffect.ARCANE_MUTE)) {
            return;
        }
        
        ev.setCancelled(true);
    }
    
    @EventHandler
    public void handleHariantTalentEvent(HariantTalentPreconditionEvent ev) {
        if (!ev.getPlayer().hasEffect(EnumStatusEffect.ARCANE_MUTE)) {
            return;
        }
        
        ev.setCancelled(HariantTalentPreconditionEvent.cancel(Component.text("Arcane Mute")));
    }
    
}
