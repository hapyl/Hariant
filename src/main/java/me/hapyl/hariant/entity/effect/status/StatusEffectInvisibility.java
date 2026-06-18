package me.hapyl.hariant.entity.effect.status;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.StreamRules;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantAttackEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class StatusEffectInvisibility extends StatusEffectImpl implements Listener {
    StatusEffectInvisibility() {
        super(Key.ofString("effect_invisibility"), Component.text("Invisibility"), EffectType.BUFF);
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Makes you completely invisible and unaffectable to the enemy."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Dealing damage clears this effect."))
        );
    }
    
    @EventHandler
    public void handleHariantDamageEvent(HariantAttackEvent ev) {
        final HariantEntity attacker = ev.getAttacker();
        
        // Check if the ATTACKER has invisibility
        if (!attacker.hasEffect(EnumStatusEffect.INVISIBILITY)) {
            return;
        }
        
        // Actually remove the effect
        attacker.removeEffect(EnumStatusEffect.INVISIBILITY);
        
        // Notify that they lost invisibility
        attacker.sendMessage(Component.text("You dealt damage and lost your invisibility!", Colors.ERROR));
        
        attacker.playSound(Sound.ENTITY_HORSE_EAT, 0.75f);
        attacker.playSound(Sound.ENTITY_BLAZE_HURT, 1.25f);
    }
    
    @Override
    public void onApply(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int duration) {
        entity.hide(StreamRules.NOT_TEAMMATES);
        entity.addVanillaEffect(PotionEffectType.INVISIBILITY, 1, HariantConstants.INDEFINITE_DURATION);
    }
    
    @Override
    public void onRemove(@NotNull HariantEntity entity, @NotNull HariantEntity applier) {
        entity.show(StreamRules.ALL);
        entity.removeVanillaEffect(PotionEffectType.INVISIBILITY);
    }
}
