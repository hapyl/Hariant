package me.hapyl.hariant.inventory.drop;

import me.hapyl.hariant.profile.PlayerProfile;
import org.jetbrains.annotations.NotNull;

public interface LootGenerator {
    
    @NotNull DropSummary generateLoot(@NotNull PlayerProfile profile);
    
}
