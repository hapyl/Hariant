package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.hero.inferno.InfernoDemonType;
import me.hapyl.hariant.hero.inferno.TalentDemonsplitTyphoeus;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class AchievementInfernoEchoesOfPain extends AchievementHeroImpl implements Listener {
    
    AchievementInfernoEchoesOfPain(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Echoes of Pain"),
                Component.empty()
                         .append(Component.text("Kill an enemy using "))
                         .append(Component.text("Repeat", Colors.GOLD))
                         .append(Component.text(" reform-ability of "))
                         .append(InfernoDemonType.TYPHOEUS.getName())
                         .append(Component.text(".")),
                HeroRegistry.INFERNO
        );
        
        setTier(AchievementTier.TIER_2);
    }
    
    @EventHandler
    public void handleHariantDamageEvent(HariantDamageEvent ev) {
        final DamageInstance damageInstance = ev.getDamageInstance();
        
        if (!(ev.getDamageInstance().getAttacker() instanceof HariantPlayer player) || !(damageInstance.getDamageSource() instanceof TalentDemonsplitTyphoeus.DamageSourceRepeat) || !damageInstance.isLethal()) {
            return;
        }
        
        progress(player.getProfile());
    }
    
}
