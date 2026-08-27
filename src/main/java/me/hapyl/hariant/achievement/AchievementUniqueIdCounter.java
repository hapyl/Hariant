package me.hapyl.hariant.achievement;

import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class AchievementUniqueIdCounter implements ComponentLike {
    
    private static final int NOT_COUNTING = -1;
    
    private final Achievement achievement;
    private final PlayerProfile profile;
    private final int count;
    
    private int currentId;
    private int currentCount;
    
    AchievementUniqueIdCounter(@NotNull Achievement achievement, @NotNull PlayerProfile profile, @Range(from = 1, to = Integer.MAX_VALUE) int count) {
        this.achievement = achievement;
        this.profile = profile;
        this.count = count;
        this.currentId = NOT_COUNTING;
    }
    
    public void count(@NotNull UniqueId uniqueId) {
        final int id = uniqueId.getUniqueId();
        
        // If current id matches the id, increment counter
        if (currentId == id) {
            currentCount++;
            
            // If current count reached count, trigger the achievement
            if (currentCount == count) {
                achievement.progress(profile);
            }
        }
        // Otherwise, reset the id and the counter
        else {
            currentId = id;
            currentCount = 1;
        }
    }
    
    public void reset() {
        // We only need to reset the id, since counter starts at 1 and will reassign the id properly
        // on the next call of `count()`; UniqueId cannot be negative
        currentId = NOT_COUNTING;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return Component.text(currentCount + "/" + count);
    }
    
}