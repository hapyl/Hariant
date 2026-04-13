package me.hapyl.hariant.talent.target;

import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentContextImpl;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.NotImplementedException;
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
    @Nullable
    TalentContext createContext(@NotNull HariantPlayer player);
    
    @NotNull
    Component errorMessage();
    
    @NotNull
    static TalentTarget none() {
        class Holder {
            private static final TalentTarget EMPTY = new TalentTarget() {
                @NotNull
                @Override
                public TalentContext createContext(@NotNull HariantPlayer player) {
                    return TalentContext.empty();
                }
                
                @NotNull
                @Override
                public Component errorMessage() {
                    return Component.empty();
                }
            };
        }
        
        return Holder.EMPTY;
    }
    
    @NotNull
    static TalentTarget targetEntityRayCast(double maxDistance, double lookupRadius, @NotNull Predicate<HariantEntity> filter) {
        return new TalentTargetEntityRayCast(maxDistance, lookupRadius, filter);
    }
    
    @NotNull
    static TalentTarget targetBlock(int maxDistance, @NotNull Predicate<Block> filter) {
        return new TalentTargetBlock(maxDistance, filter);
    }
    
    @Deprecated // Not implemented
    @NotNull
    static TalentTarget targetEntityDotProduct(double maxDistance, double dot, @NotNull Predicate<HariantEntity> filter) {
        throw new NotImplementedException();
    }
    
}
