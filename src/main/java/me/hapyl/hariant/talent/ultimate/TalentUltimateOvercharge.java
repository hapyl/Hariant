package me.hapyl.hariant.talent.ultimate;

import me.hapyl.eterna.module.component.ComponentStyler;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayFieldInstance;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class TalentUltimateOvercharge extends TalentUltimate {
    
    private static final Component OVERCHARGE_PREFIX_AND_SUFFIX_COMPONENT = Component.text("||", Colors.DARK_PURPLE, TextDecoration.OBFUSCATED);
    private static final Component USAGE_COMPONENT = Component.empty()
                                                              .append(Component.text("Your ultimate has ", Colors.DEFAULT_COLOR))
                                                              .append(Component.text("overcharged", Colors.ULTIMATE_OVERCHARGE))
                                                              .append(Component.text("!", Colors.DEFAULT_COLOR));
    
    private static final ComponentStyler OVERCHARGE_HEADER_STYLER = ComponentStyler.create(Style.style(Colors.DARK_GRAY));
    
    private final double overchargeCost;
    
    public TalentUltimateOvercharge(@NotNull Key key, @NotNull Component name, @NotNull Icon icon, @NotNull UltimateResourceType resource, double cost, double overchargeCost) {
        if (overchargeCost <= cost) {
            throw new IllegalArgumentException("`overchargeCost` cannot be lower than `cost`");
        }
        
        super(key, name, icon, resource, cost);
        
        this.overchargeCost = overchargeCost;
    }
    
    public abstract @NotNull Component overchargeDescription();
    
    public abstract @NotNull Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, @NotNull ChargeLevel chargeLevel);
    
    @Override
    public double getMaximumCost() {
        return overchargeCost;
    }
    
    @Override
    public double getConsumption() {
        return overchargeCost;
    }
    
    @Override
    public void onResourceValue(@NotNull HariantPlayer player, double previousValue, double newValue) {
        if (previousValue < overchargeCost && newValue >= overchargeCost) {
            player.sendSubtitle(Component.text("ᴜʟᴛɪᴍᴀᴛᴇ ᴏᴠᴇʀᴄʜᴀʀɢᴇᴅ", Colors.ULTIMATE_OVERCHARGE, TextDecoration.BOLD), 5, 10, 5);
            player.sendMessage(createUsageComponent(USAGE_COMPONENT));
            
            player.playSound(Sound.BLOCK_CONDUIT_DEACTIVATE, 1.75f);
            player.playSound(Sound.ENTITY_WARDEN_SONIC_BOOM, 1.25f);
            return;
        }
        
        super.onResourceValue(player, previousValue, newValue);
    }
    
    @Override
    public @NotNull Component getComponent(@NotNull HariantPlayer player, double ultimateResource) {
        final Component component = super.getComponent(player, ultimateResource);
        
        // If player has more `ultimateResource` than the minimum cost, calculate the percentage towards overcharge
        final double minimumCost = this.getMinimumCost();
        final double maximumCost = this.getMaximumCost();
        
        if (ultimateResource > minimumCost) {
            final double percent = (ultimateResource - minimumCost) / (maximumCost - minimumCost);
            
            // If fully overcharged, tell just that
            if (percent >= 1.0) {
                return Component.empty()
                                .append(OVERCHARGE_PREFIX_AND_SUFFIX_COMPONENT)
                                .appendSpace()
                                .append(Component.text("OVERCHARGED", Colors.ULTIMATE_OVERCHARGE, TextDecoration.BOLD))
                                .appendSpace()
                                .append(OVERCHARGE_PREFIX_AND_SUFFIX_COMPONENT);
            }
            // Otherwise append base component with percent towards overcharge
            else {
                return Component.empty()
                                .append(component)
                                .appendSpace()
                                .append(Component.text("%,.0f%%".formatted(percent * 100), Colors.ULTIMATE_OVERCHARGE, TextDecoration.BOLD));
            }
        }
        
        // Otherwise, return the base component
        return component;
    }
    
    @Override
    public final @NotNull Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, double consumedResource) {
        final ChargeLevel chargeLevel = consumedResource >= overchargeCost ? ChargeLevel.OVERCHARGED : ChargeLevel.NORMAL;
        
        return execute(player, context, chargeLevel);
    }
    
    @Override
    public abstract @NotNull TalentTarget target(@NotNull HariantPlayer player);
    
    @Override
    public @NotNull String getTalentClassName() {
        return "Overcharge Ultimate Talent";
    }
    
    @Override
    protected void initAttributeFields(@NotNull List<? super DisplayFieldInstance> attributeFields) {
        super.initAttributeFields(attributeFields);
        
        attributeFields.add(new DisplayFieldInstance(
                getUltimateResourceType().getName().append(Component.text(" Overcharge Cost")),
                Component.text("%.0f".formatted(overchargeCost))
        ));
    }
    
    @Override
    public @NotNull ItemBuilder createBuilder() {
        final ItemBuilder builder = super.createBuilder();
        
        builder.addLore();
        builder.addLore(Component.text("ᴛʜɪꜱ ᴜʟᴛɪᴍᴀᴛᴇ ᴄᴀɴ ʙᴇ ᴏᴠᴇʀᴄʜᴀʀɢᴇᴅ", Colors.ULTIMATE_OVERCHARGE));
        builder.addWrappedLore(
                Component.empty()
                         .append(Component.text("Overcharged ultimate consumes more "))
                         .append(getUltimateResourceType().getName())
                         .append(Component.text(" and empowers it:")),
                OVERCHARGE_HEADER_STYLER
        );
        
        builder.addLore();
        builder.addWrappedLore(overchargeDescription(), HariantConstants.COMPONENT_STYLER_DESCRIPTION_PADDING_1);
        
        return builder;
    }
    
}