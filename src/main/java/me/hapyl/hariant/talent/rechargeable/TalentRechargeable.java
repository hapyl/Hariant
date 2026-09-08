package me.hapyl.hariant.talent.rechargeable;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayFieldInstance;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class TalentRechargeable extends Talent {
    
    private final RechargeType rechargeType;
    private final int maximumCharges;
    
    public TalentRechargeable(@NotNull Key key, @NotNull Component name, @NotNull Icon icon, int maximumCharges, @NotNull RechargeType rechargeType) {
        super(key, name, icon);
        
        this.maximumCharges = maximumCharges;
        this.rechargeType = rechargeType;
    }
    
    public int getMaximumCharges() {
        return maximumCharges;
    }
    
    public @NotNull Component getMaximumChargesComponent() {
        return rechargeType.getComponent(maximumCharges);
    }
    
    @Override
    public void onCreate(@NotNull HariantPlayer player) {
        // Update charges
        this.onUpdate(player, player.getRechargeableTalentData(this));
    }
    
    @Override
    public abstract @NotNull TalentTarget target(@NotNull HariantPlayer player);
    
    @Override
    public final @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        final RechargeableTalentData rechargeableTalentData = player.getRechargeableTalentData(this);
        
        if (rechargeableTalentData.getCharges() == 0) {
            return rechargeType.onExecuteNoCharges(player, this, rechargeableTalentData);
        }
        
        final Response response = this.execute(player, context, rechargeableTalentData);
        
        if (response.isError()) {
            return response;
        }
        
        // Decrement charges
        rechargeableTalentData.decrementCharges(1);
        
        return rechargeType.onExecute(player, this, rechargeableTalentData);
    }
    
    @Override
    public final boolean respectCooldown() {
        // If recharge type is ONE_AFTER_ANOTHER, we have to ignore the cooldown to allow talent execution
        return rechargeType != RechargeType.ONE_AFTER_ANOTHER;
    }
    
    @Override
    protected void initAttributeFields(@NotNull List<? super DisplayFieldInstance> attributeFields) {
        super.initAttributeFields(attributeFields);
        
        attributeFields.add(DisplayFieldInstance.create(Component.text("Maximum Charges"), Component.text(maximumCharges)));
    }
    
    public abstract @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext talentContext, @NotNull RechargeableTalentData rechargeableTalentData);
    
    @Override
    public final void onCooldownEnded(@NotNull HariantEntity entity) {
        if (!(entity instanceof HariantPlayer player)) {
            return;
        }
        
        // Pass implementation to recharge type
        rechargeType.onCooldownEnded(player, this, player.getRechargeableTalentData(this));
    }
    
    public void onUpdate(@NotNull HariantPlayer player, @NotNull RechargeableTalentData rechargeableTalentData) {
        final PlayerInventory inventory = player.getInventory();
        
        final int slot = rechargeableTalentData.getTalentIndex().getSlot();
        final ItemStack itemStack = inventory.getItem(slot);
        
        // If there isn't an item in the data is somehow created for non-active talent, return
        if (itemStack == null || slot < 0) {
            return;
        }
        
        inventory.setItem(slot, rechargeType.createItem(this, rechargeableTalentData.getCharges()));
    }
    
}
