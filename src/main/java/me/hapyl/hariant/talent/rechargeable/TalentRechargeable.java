package me.hapyl.hariant.talent.rechargeable;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayFieldInstance;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.InternalTasks;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class TalentRechargeable extends Talent {
    
    private static final Material DEFAULT_NO_CHARGES_MATERIAL = Material.GRAY_DYE;
    
    private final int maxCharges;
    private final ItemStack itemNoCharges;
    
    public TalentRechargeable(@NotNull Key key, @NotNull Component name, @NotNull Icon icon, final int maxCharges) {
        super(key, name, icon);
        
        this.maxCharges = maxCharges;
        this.itemNoCharges = createItemNoCharges();
    }
    
    public int getMaxCharges() {
        return maxCharges;
    }
    
    public final @NotNull ItemStack getItemNoCharges() {
        return itemNoCharges;
    }
    
    public @NotNull Material getMaterialNoCharges() {
        return DEFAULT_NO_CHARGES_MATERIAL;
    }
    
    @Override
    public void onCreate(@NotNull HariantPlayer player) {
        // Bump data to fix item stacks
        player.getRechargeableTalentData(this).updateCharges();
    }
    
    @Override
    public abstract @NotNull TalentTarget target(@NotNull HariantPlayer player);
    
    @Override
    public final @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        // Check talent data
        final RechargeableTalentData data = player.getRechargeableTalentData(this);
        
        // If no more charges, show when the next charge is in
        if (data.getCharges() == 0) {
            final int nextChargeIn = player.getCooldownTimeLeft(this);
            
            player.playSound(Sound.BLOCK_WOODEN_DOOR_CLOSE, 0.75f);
            return Response.error("No more charges, next one in %s!".formatted(Tick.format(nextChargeIn)));
        }
        
        final Response response = this.execute(player, context, data);
        
        if (response.isError()) {
            return response;
        }
        
        // Decrement charges
        data.decrementCharges();
        
        // If the player is currently on cooldown, HOLD, otherwise start the cooldown
        if (player.hasCooldown(this)) {
            return Response.hold();
        }
        else {
            return Response.ok();
        }
    }
    
    @Override
    public boolean respectCooldown() {
        return false;
    }
    
    @Override
    protected void initAttributeFields(@NotNull List<? super DisplayFieldInstance> attributeFields) {
        super.initAttributeFields(attributeFields);
        
        attributeFields.add(new DisplayFieldInstance(Component.text("Charges"), Component.text(maxCharges)));
    }
    
    @Override
    public final void onCooldownStarted(@NotNull HariantEntity entity, int cooldown) {
    }
    
    @Override
    public final void onCooldownEnded(@NotNull HariantEntity entity) {
        if (!(entity instanceof HariantPlayer player) || player.isDead()) {
            return;
        }
        
        final RechargeableTalentData data = player.getRechargeableTalentData(this);
        
        data.incrementCharges();
        
        // If charges aren't at their maximum, start the cooldown again at the next game tick
        if (data.getCharges() < maxCharges) {
            InternalTasks.now(() -> player.setCooldown(this));
        }
        
        // Fx
        player.playSound(Sound.ENTITY_CHICKEN_EGG, 2.0f);
    }
    
    public abstract @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context, @NotNull RechargeableTalentData talentData);
    
    private @NotNull ItemStack createItemNoCharges() {
        return this.createBuilder()
                   .setItemModel(this.getMaterialNoCharges())
                   .addLore()
                   .addLore(Component.text("No more charges!", Colors.RED))
                   .asIcon();
    }
    
    
}
