package me.hapyl.hariant.reward;

import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.inventory.adder.Adder;
import me.hapyl.hariant.inventory.adder.AdderError;
import me.hapyl.hariant.inventory.item.AbstractItem;
import me.hapyl.hariant.inventory.item.Resource;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface Reward extends Keyed, Named {
    
    @NotNull Component SEPARATOR = Component.text(" × ", Colors.DARK_GRAY);
    
    @Override
    @NotNull Key getKey();
    
    @Override
    @NotNull Component getName();
    
    @ApiStatus.OverrideOnly
    @NotNull Adder<? extends AbstractItem, ?> reward(@NotNull PlayerProfile profile);
    
    default int priority() {
        return 0;
    }
    
    default boolean claim(@NotNull PlayerProfile profile, @NotNull ErrorHandler errorHandler) {
        final RewardsEntry rewards = profile.getDatabase().rewards;
        
        // Check whether the reward is already claimed
        if (rewards.hasClaimed(this)) {
            errorHandler.handle(profile, this, () -> "This reward is already claimed!");
            return false;
        }
        
        final Adder<? extends AbstractItem, ?> adder = this.reward(profile);
        
        adder.onSuccess(_ -> {
                 // On success, mark the reward as claimed
                 rewards.setClaimed(this);
             })
             .onError(error -> {
                 // On error, pass to the handler
                 errorHandler.handle(profile, this, error);
             });
        
        return adder.isSuccess();
    }
    
    default boolean claim(@NotNull PlayerProfile profile) {
        return this.claim(profile, ErrorHandler.DEFAULT_HANDLER);
    }
    
    default boolean hasClaimed(@NotNull PlayerProfile profile) {
        return profile.getDatabase().rewards.hasClaimed(this);
    }
    
    default @NotNull Component format(@NotNull PlayerProfile profile) {
        // Default format delegates to the name of the reward plus a checkmark if the player has claimed the reward
        return Component.empty()
                        .append(this.getName())
                        .appendSpace()
                        .append(Components.checkmark(this.hasClaimed(profile) ? true : null));
    }
    
    static @NotNull Reward ofResource(@NotNull Key key, @NotNull Resource resource, int amount) {
        return new RewardResource(key, resource, amount);
    }
    
    interface ErrorHandler {
        
        @NotNull ErrorHandler DEFAULT_HANDLER = (profile, reward, error) -> profile.messageError(
                Component.empty()
                         .append(Component.text("Failed to claim "))
                         .append(reward.getName())
                         .append(Component.text("! "))
                         .append(Component.text(error.getError(), Colors.RED))
        );
        
        void handle(@NotNull PlayerProfile profile, @NotNull Reward reward, @NotNull AdderError error);
        
    }
    
}