package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDeathEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.hero.shark.HeroDataShark;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class AchievementSharkSurpriseAttack extends AchievementHeroImpl implements Listener {
    
    private static final int TICK_THRESHOLD = Tick.fromSeconds(2.5f);
    
    AchievementSharkSurpriseAttack(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Surprise Attack"),
                Component.empty()
                         .append(Component.text("Defeat an enemy shortly after emerging from using "))
                         .append(TalentRegistry.SUBMERGE)
                         .append(Component.text(".")),
                HeroRegistry.SHARK
        );
        
        setTier(AchievementTier.TIER_3);
    }
    
    @EventHandler
    public void handleHariantDeathEvent(HariantDeathEvent ev) {
        if (!(ev.getDamageInstance().getAttacker() instanceof HariantPlayer player)) {
            return;
        }
        
        if (!(player.touchHeroData(HeroRegistry.SHARK, HeroDataShark.class, HeroDataShark::getLastEmergeTick).orElse(null) instanceof Integer lastEmergeTick)) {
            return;
        }
        
        if (player.localTicks() - lastEmergeTick > TICK_THRESHOLD) {
            return;
        }
        
        this.progress(player.getProfile());
    }
    
    public static void progress(@NotNull HariantPlayer player, int lastEmergeTick) {
        if (player.localTicks() - lastEmergeTick > TICK_THRESHOLD) {
            return;
        }
        
        AchievementRegistry.SHARK_SURPRISE_ATTACK.progress(player.getProfile());
    }
    
}