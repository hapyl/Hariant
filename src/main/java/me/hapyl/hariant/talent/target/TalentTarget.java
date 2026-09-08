package me.hapyl.hariant.talent.target;

import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentContext;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public interface TalentTarget {
    
    /**
     * Creates the {@link TalentContext} for the given {@link HariantPlayer}.
     *
     * <p>
     * This method should return {@code null} if the target retrieval fails to indicate that an error happened and send the
     * {@link #errorMessage()} to the player.
     * </p>
     *
     * @param player - The player for whom to create the context.
     * @return a talent context, or {@code null} if creating failed.
     */
    @Nullable TalentContext createContext(@NotNull HariantPlayer player);
    
    @NotNull Component errorMessage();
    
    static @NotNull TalentTarget none() {
        class Holder {
            private static final TalentTarget EMPTY = new TalentTarget() {
                @Override
                public @NotNull TalentContext createContext(@NotNull HariantPlayer player) {
                    return TalentContext.empty();
                }
                
                @Override
                public @NotNull Component errorMessage() {
                    return Component.empty();
                }
            };
        }
        
        return Holder.EMPTY;
    }
    
    static @NotNull TalentTarget targetEntity(double maxDistance, double lookupRadius, @NotNull TalentTargetEntityRayCast.BlockCollision blockCollision, @NotNull TalentTargetEntityRayCast.EntityPriority entityPriority, @NotNull Predicate<HariantEntity> filter) {
        return new TalentTargetEntityRayCast(maxDistance, lookupRadius, blockCollision, entityPriority, filter);
    }
    
    static @NotNull TalentTarget targetBlock(int maxDistance, @NotNull Predicate<Block> filter) {
        return new TalentTargetBlock(maxDistance, filter);
    }
    
}
