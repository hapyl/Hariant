package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantAttributeUpdateEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class AchievementElementalist extends AchievementImpl implements Listener {
    
    private static final double ELEMENTAL_MASTERY = 500;
    
    public AchievementElementalist(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Elementalist"),
                Component.empty()
                         .append(Component.text("Reach "))
                         .append(AttributeType.ELEMENTAL_MASTERY.format(ELEMENTAL_MASTERY))
                         .appendSpace()
                         .append(AttributeType.ELEMENTAL_MASTERY)
                         .append(Component.text(" or higher."))
        );
        
        setCategory(AchievementCategory.ELEMENTS_OF_THE_WORLD);
        setTier(AchievementTier.TIER_2);
    }
 
    @EventHandler
    public void handleHariantAttributeUpdateEvent(HariantAttributeUpdateEvent ev) {
        if (!(ev.getEntity() instanceof HariantPlayer player) || ev.getAttributeType() != AttributeType.ELEMENTAL_MASTERY || ev.getValue() < ELEMENTAL_MASTERY) {
            return;
        }
        
        AchievementRegistry.ELEMENTALIST.progress(player.getProfile());
    }
    
}