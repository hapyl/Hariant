package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDeathEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.hero.shark.BloodScent;
import me.hapyl.hariant.hero.shark.HeroDataShark;
import me.hapyl.hariant.util.Definition;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class AchievementSharkPreyHunted extends AchievementHeroImpl implements Listener {
    
    AchievementSharkPreyHunted(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Prey Hunted"),
                Component.empty()
                         .append(Component.text("Defeat an enemy while they have the "))
                         .appendNewline()
                         .append(Definition.PREY)
                         .append(Component.text(" mark applied unto them.")),
                HeroRegistry.SHARK
        );
    }
    
    @EventHandler
    public void handleHariantDeathEvent(HariantDeathEvent ev) {
        final HariantEntity entity = ev.getEntity();
        final DamageInstance damageInstance = ev.getDamageInstance();
        
        if (!(damageInstance.getDamageSource().getSource() instanceof HariantPlayer player)) {
            return;
        }
        
        if (!(player.touchHeroData(HeroRegistry.SHARK, HeroDataShark.class, HeroDataShark::getBloodScent).orElse(null) instanceof BloodScent bloodScent)) {
            return;
        }
        
        if (!bloodScent.getEntity().equals(entity)) {
            return;
        }
        
        this.progress(player.getProfile());
    }
    
}