package me.hapyl.hariant.talent.rechargeable;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentIndex;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class RechargeableTalentData {
    
    private final HariantPlayer player;
    private final TalentRechargeable talent;
    private final TalentIndex talentIndex;
    
    private int charges;
    
    public RechargeableTalentData(@NotNull HariantPlayer player, @NotNull TalentRechargeable talent) {
        this.player = player;
        this.talent = talent;
        this.talentIndex = player.getHero().getTalentIndex(talent);
        this.charges = talent.getMaxCharges();
    }
    
    public int getCharges() {
        return charges;
    }
    
    public void incrementCharges() {
        charges = Math.min(talent.getMaxCharges(), charges + 1);
        updateCharges();
    }
    
    public void decrementCharges() {
        charges = Math.max(0, charges - 1);
        updateCharges();
    }
    
    public void updateCharges() {
        final Player bukkitPlayer = player.getHandle();
        final PlayerInventory inventory = bukkitPlayer.getInventory();
        
        final int slot = talentIndex.getSlot();
        final ItemStack itemStack = inventory.getItem(slot);
        
        // If there isn't an item in the data is somehow created for non-active talent, return
        if (itemStack == null || slot < 0) {
            return;
        }
        
        // If there is not more charges, we use a different item, because otherwise there isn't
        // a way to differentiate between one charge and no charges
        if (charges == 0) {
            inventory.setItem(slot, talent.getItemNoCharges());
        }
        // Otherwise set the amount to the charges
        else {
            // Yeah, we have to create new item each time ¯\_(ツ)_/¯
            inventory.setItem(
                    slot,
                    talent.createBuilder()
                          .setAmount(charges)
                          .asIcon()
            );
        }
        
        bukkitPlayer.updateInventory();
    }
    
}
