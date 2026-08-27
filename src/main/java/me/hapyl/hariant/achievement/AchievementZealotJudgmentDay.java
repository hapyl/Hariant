package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDeathEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.hero.zealot.TalentMaintainOrder;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class AchievementZealotJudgmentDay extends AchievementHeroImpl implements Listener {
    
    AchievementZealotJudgmentDay(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Judgment Day"),
                Component.empty()
                         .append(Component.text("Defeat an enemy with "))
                         .append(TalentRegistry.MAINTAIN_ORDER)
                         .append(Component.text("'s slam DMG.")),
                HeroRegistry.ZEALOT
        );
    }
    
    @EventHandler
    public void handleHariantDeathEvent(HariantDeathEvent ev) {
        if (!(ev.getDamageInstance().getDamageSource() instanceof TalentMaintainOrder.DamageSourceMaintainOrderLanding damageSource)) {
            return;
        }
        
        if (!(damageSource.getSource() instanceof HariantPlayer player)) {
            return;
        }
        
        this.progress(player.getProfile());
    }
    
}
