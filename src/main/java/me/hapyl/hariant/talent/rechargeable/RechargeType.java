package me.hapyl.hariant.talent.rechargeable;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.text.NumberToWord;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.task.InternalTasks;
import me.hapyl.hariant.util.ThisClassShouldNeMovedToEternaAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public enum RechargeType {
    
    ONE_AFTER_ANOTHER {
        private static final Material NO_CHARGES_MATERIAL = Material.GRAY_DYE;
        
        @Override
        public void onCooldownEnded(@NotNull HariantPlayer player, @NotNull TalentRechargeable talent, @NotNull RechargeableTalentData rechargeableTalentData) {
            // Increment charges
            rechargeableTalentData.incrementCharges(1);
            
            // If we're not at max charges, start the cooldown again
            if (rechargeableTalentData.getCharges() < talent.getMaximumCharges()) {
                InternalTasks.now(() -> player.setCooldown(talent));
            }
            
            // Fx
            player.playSound(Sound.ENTITY_CHICKEN_EGG, 2.0f);
        }
        
        @Override
        public @NotNull Response onExecuteNoCharges(@NotNull HariantPlayer player, @NotNull TalentRechargeable talent, @NotNull RechargeableTalentData rechargeableTalentData) {
            final int nextChargeIn = player.getCooldownTimeLeft(talent);
            
            player.playSound(Sound.BLOCK_WOODEN_DOOR_CLOSE, 0.75f);
            return Response.error("No more charges, next one in %s!".formatted(Tick.format(nextChargeIn)));
        }
        
        @Override
        public @NotNull Response onExecute(@NotNull HariantPlayer player, @NotNull TalentRechargeable talent, @NotNull RechargeableTalentData rechargeableTalentData) {
            // If player is already on cooldown, hold
            if (player.hasCooldown(talent)) {
                return Response.hold();
            }
            // Otherwise start the cooldown normally
            else {
                return Response.ok();
            }
        }
        
        @Override
        public @NotNull ItemStack createItem(@NotNull TalentRechargeable talent, int charges) {
            if (charges == 0) {
                return talent.createBuilder()
                             .setItemModel(NO_CHARGES_MATERIAL)
                             .addLore()
                             .addLore(Component.text("No more charges!", Colors.RED))
                             .asIcon();
            }
            
            return super.createItem(talent, charges);
        }
        
        @Override
        public @NotNull Component getComponent(int maximumCharges) {
            return Component.text("This talent has %s initial charges.".formatted(NumberToWord.toWord(maximumCharges).toLowerCase()), Colors.DARK_GRAY);
        }
    },
    
    DEPLETE_ALL {
        @Override
        public @NotNull Response onExecute(@NotNull HariantPlayer player, @NotNull TalentRechargeable talent, @NotNull RechargeableTalentData rechargeableTalentData) {
            // If out of charges, reset the charges and start cooldown
            if (rechargeableTalentData.getCharges() == 0) {
                rechargeableTalentData.incrementCharges(talent.getMaximumCharges());
                return Response.ok();
            }
            // Otherwise start a tiny internal cooldown and hold
            else {
                player.setCooldown(talent, 2);
                return Response.hold();
            }
        }
        
        @Override
        public @NotNull Response onExecuteNoCharges(@NotNull HariantPlayer player, @NotNull TalentRechargeable talent, @NotNull RechargeableTalentData rechargeableTalentData) {
            // Realistically this should never be called but must be overridden
            return Response.error("On cooldown!");
        }
        
        @Override
        public @NotNull Component getComponent(int maximumCharges) {
            return Component.text("This talent can be used %s consecutively.".formatted(numberToString(maximumCharges)), Colors.DARK_GRAY);
        }
        
        @ThisClassShouldNeMovedToEternaAPI
        private static @NotNull String numberToString(int number) {
            return switch (number) {
                case 1 -> "once";
                case 2 -> "twice";
                case 3 -> "thrice";
                case 4 -> "four times";
                case 5 -> "five times";
                case 6 -> "six times";
                case 7 -> "seven times";
                case 8 -> "eight times";
                case 9 -> "nine times";
                case 10 -> "ten times";
                default -> number + " times";
            };
        }
        
    };
    
    public void onCooldownEnded(@NotNull HariantPlayer player, @NotNull TalentRechargeable talent, @NotNull RechargeableTalentData rechargeableTalentData) {
    }
    
    public @NotNull Response onExecuteNoCharges(@NotNull HariantPlayer player, @NotNull TalentRechargeable talent, @NotNull RechargeableTalentData rechargeableTalentData) {
        throw new IllegalStateException();
    }
    
    public @NotNull Response onExecute(@NotNull HariantPlayer player, @NotNull TalentRechargeable talent, @NotNull RechargeableTalentData rechargeableTalentData) {
        throw new IllegalStateException();
    }
    
    public @NotNull ItemStack createItem(@NotNull TalentRechargeable talent, int charges) {
        return talent.createBuilder().setAmount(charges).asIcon();
    }
    
    public @NotNull Component getComponent(int maximumCharges) {
        return Component.empty();
    }
    
}
