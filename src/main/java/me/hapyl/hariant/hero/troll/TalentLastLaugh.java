package me.hapyl.hariant.hero.troll;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentPassive;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.util.BaseChance;
import me.hapyl.hariant.util.FireworkHelper;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class TalentLastLaugh extends TalentPassive implements Listener {
    
    @DisplayField private final BaseChance chance = BaseChance.baseChance(1);
    
    private final DeathMessage deathMessage = DeathMessage.create("{player} was trolled to death [by {killer}]");
    
    public TalentLastLaugh(@NotNull Key key) {
        super(key, Component.text("Last Laugh"), Icon.ofMaterial(Material.BLAZE_POWDER));
        
        setDescription(
                Component.empty()
                         .append(Component.text("Dealing "))
                         .append(DamageType.MELEE)
                         .append(Component.text(" has "))
                         .append(chance)
                         .appendSpace()
                         // Don't add base chance term because it looks kinda weird, the chance display fields conveys it
                         .append(Component.text(" base chance to instantly kill the enemy."))
        );
    }
    
    @EventHandler
    public void handleHariantDamageEvent(HariantDamageEvent ev) {
        final HariantEntity attacker = ev.getAttacker();
        
        if (!(attacker instanceof HariantPlayer player)) {
            return;
        }
        
        if (!player.getHero().equals(HeroRegistry.TROLL)) {
            return;
        }
        
        if (ev.getDamageType() != DamageType.MELEE) {
            return;
        }
        
        if (!chance.chance(player)) {
            return;
        }
        
        final HariantEntity entity = ev.getEntity();
        
        entity.die(DamageSource.death(DamageSourceIdentity.create(this, deathMessage)).source(player).build());
        
        // Fx
        player.playWorldSound(Sound.ENTITY_EVOKER_PREPARE_WOLOLO, 2.0f);
        
        FireworkHelper.explode(entity.getMidpointLocation(), meta -> {
            meta.setPower(1);
            meta.addEffect(
                    FireworkEffect.builder()
                                  .withColor(
                                          Color.fromRGB(230, 53, 53),
                                          Color.fromRGB(237, 211, 211)
                                  )
                                  .withFlicker()
                                  .build()
            );
        });
    }
    
}