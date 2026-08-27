package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.text.RomanNumber;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.event.monitor.MonitorEventListener;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AchievementCombatMaster extends AchievementImpl {
    
    static {
        MonitorEventListener.listener(HariantDamageEvent.class, ev -> {
            if (!(ev.getAttacker() instanceof HariantPlayer player)) {
                return;
            }
            
            AchievementRegistry.COMBAT_MASTER_3.progressFamily(player.getProfile(), ev.getDamage());
        });
    }
    
    AchievementCombatMaster(@NotNull Key key, int index, int goal, @NotNull AchievementTier achievementTier, @Nullable Achievement parent) {
        super(
                key,
                goal,
                Component.text("Combat Master " + RomanNumber.toRoman(index)),
                Component.empty()
                         .append(Component.text("Deal a total of "))
                         .append(Component.text("%,d".formatted(goal), Colors.RED))
                         .append(Component.text(" DMG by any means."))
        );
        
        setCategory(AchievementCategory.COMBAT);
        setTier(achievementTier);
        setParent(parent);
    }
    
    @Override
    public boolean isProgressCapped() {
        return false;
    }
    
}
