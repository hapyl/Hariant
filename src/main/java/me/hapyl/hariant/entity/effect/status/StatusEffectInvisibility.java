package me.hapyl.hariant.entity.effect.status;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.StreamRules;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.event.HariantAttackEvent;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.util.ComponentProgress;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class StatusEffectInvisibility extends StatusEffectImpl implements Listener {
    
    private final Style STYLE = Style.style(TextColor.color(0x9EA5BB));
    
    StatusEffectInvisibility() {
        super(Key.ofString("effect_invisibility"), Component.text("Invisibility"), EffectType.BUFF);
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Makes you completely invisible and unaffectable to the enemy."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Dealing or taking damage clears this effect."))
        );
    }
    
    @EventHandler
    public void handleHariantAttackEvent(HariantAttackEvent ev) {
        final HariantEntity attacker = ev.getAttacker();
        
        if (attacker.hasEffect(StatusEffectType.INVISIBILITY)) {
            this.loseInvisibility(attacker, "dealt");
        }
    }
    
    @EventHandler
    public void handleHariantDamageEvent(HariantDamageEvent ev) {
        final HariantEntity entity = ev.getEntity();
        
        if (entity.hasEffect(StatusEffectType.INVISIBILITY)) {
            this.loseInvisibility(entity, "took");
        }
    }
    
    @Override
    public void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick, int duration) {
        // Show the invisibility duration
        entity.showTitle(Title.title(
                ComponentProgress.create("ɪɴᴠɪꜱɪʙʟᴇ", STYLE, (double) tick / duration),
                Component.empty(),
                0, 5, 2
        ));
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
    
    private void loseInvisibility(@NotNull HariantEntity attacker, @NotNull String type) {
        attacker.removeEffect(StatusEffectType.INVISIBILITY);
        
        // Notify that they lost invisibility
        attacker.sendMessage(Component.text("You %s damage and lost your invisibility!".formatted(type), Colors.ERROR));
        
        attacker.playSound(Sound.ENTITY_HORSE_EAT, 0.75f);
        attacker.playSound(Sound.ENTITY_BLAZE_HURT, 1.25f);
    }
    
}