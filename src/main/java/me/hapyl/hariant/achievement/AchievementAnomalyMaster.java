package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.text.RomanNumber;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantElementalAnomalyEvent;
import me.hapyl.hariant.event.monitor.MonitorEventListener;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AchievementAnomalyMaster extends AchievementImpl {
    
    static {
        MonitorEventListener.listener(HariantElementalAnomalyEvent.class, ev -> {
            if (!(ev.getSource() instanceof HariantPlayer player)) {
                return;
            }
            
            AchievementRegistry.ANOMALY_MASTER_3.progressFamily(player.getProfile(), 1.0);
        });
    }
    
    AchievementAnomalyMaster(@NotNull Key key, int index, int goal, @NotNull AchievementTier achievementTier, @Nullable Achievement parent) {
        super(
                key,
                goal,
                Component.text("Anomaly Master " + RomanNumber.toRoman(index)),
                Component.empty()
                         .append(Component.text("Trigger elemental anomaly "))
                         .append(Component.text("%,d".formatted(goal), Colors.NUMBER))
                         .append(Component.text(" times."))
        );
        
        setCategory(AchievementCategory.ELEMENTS_OF_THE_WORLD);
        setTier(achievementTier);
        setParent(parent);
    }
    
    @Override
    public boolean isProgressCapped() {
        return false;
    }
    
}
