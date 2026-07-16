package me.hapyl.hariant.talent.target;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentContext;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public final class TalentTargetBlock implements TalentTarget {
    
    private final int maxDistance;
    private final Predicate<Block> filter;
    
    TalentTargetBlock(int maxDistance, @NotNull Predicate<Block> filter) {
        this.maxDistance = maxDistance;
        this.filter = filter;
    }
    
    @Nullable
    @Override
    public TalentContext createContext(@NotNull HariantPlayer player) {
        final Block hitBlock = player.getHandle().getTargetBlockExact(maxDistance);
        
        if (hitBlock == null || !filter.test(hitBlock)) {
            return null;
        }
        
        return TalentContext.create(hitBlock);
    }
    
    @NotNull
    @Override
    public Component errorMessage() {
        return Component.text("No valid target block!");
    }
}
