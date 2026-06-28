package me.hapyl.hariant.talent.ultimate;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.component.Keybind;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantTalentUltimateEvent;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayFieldInstance;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.task.executor.ExecutorService;
import me.hapyl.hariant.task.executor.Promise;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.util.Duration;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.SoundFx;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class TalentUltimate extends Talent implements Duration {
    
    private static final Style STYLE_ULTIMATE_ON_COOLDOWN = Style.style(Colors.DARK_GRAY, TextDecoration.STRIKETHROUGH);
    private static final Component CHARGED_COMPONENT = Component.text("Your ultimate has charged!", Colors.DEFAULT_COLOR);
    
    private final UltimateResourceType ultimateResourceType;
    private final double cost;
    
    private SoundFx soundFx;
    
    public TalentUltimate(@NotNull Key key, @NotNull Component name, @NotNull Icon icon, @NotNull UltimateResourceType ultimateResourceType, final double cost) {
        super(key, name, icon);
        
        this.ultimateResourceType = ultimateResourceType;
        this.cost = cost;
        this.soundFx = SoundFx.create(Sound.ENTITY_ENDER_DRAGON_GROWL, 2.0f);
    }
    
    @NotNull
    public SoundFx getSoundFx() {
        return soundFx;
    }
    
    public void setSoundFx(@NotNull SoundFx soundFx) {
        this.soundFx = soundFx;
    }
    
    @NotNull
    public UltimateResourceType getUltimateResourceType() {
        return ultimateResourceType;
    }
    
    public double getMinimumCost() {
        return cost;
    }
    
    public double getMaximumCost() {
        return cost;
    }
    
    public double getConsumption() {
        return cost;
    }
    
    @EventLike
    public void onResourceValue(@NotNull HariantPlayer player, final double previousValue, final double newValue) {
        final double minimumResourceCost = this.getMinimumCost();
        
        if (previousValue < minimumResourceCost && newValue >= minimumResourceCost) {
            player.sendSubtitle(Component.text("ᴜʟᴛɪᴍᴀᴛᴇ ᴄʜᴀʀɢᴇᴅ", Colors.ULTIMATE_RESOURCE_ENERGY, TextDecoration.BOLD), 5, 10, 5);
            player.sendMessage(createUsageComponent(CHARGED_COMPONENT));
            
            player.playSound(Sound.BLOCK_CONDUIT_ACTIVATE, 2.0f);
        }
    }
    
    @ApiStatus.OverrideOnly
    public @NotNull Component getComponent(@NotNull HariantPlayer player, double ultimateResource) {
        final double percent = ultimateResource / this.getMinimumCost();
        
        return Component.text(percent >= 1.0 ? "CHARGED!" : "%,.0f%%".formatted(percent * 100), this.ultimateResourceType.getStyle().decorate(TextDecoration.BOLD));
    }
    
    @NotNull
    public final Component getComponent(@NotNull HariantPlayer player) {
        final TextComponent.Builder builder = Component.text();
        final TextColor resourceColor = ultimateResourceType.getStyle().color();
        
        // Always append resource prefix
        builder.append(ultimateResourceType.getPrefix().style(ultimateResourceType.getStyle()));
        builder.appendSpace();
        
        // If ultimate currently in use, show how much time is left
        if (player.isUsingUltimate()) {
            final int ticksAlive = player.localTicks();
            final int durationTimeLeft = Math.max(0, getDuration() - (ticksAlive - player.getUsedUltimateAt()));
            
            builder.append(Component.text("IN USE", resourceColor, TextDecoration.BOLD));
            builder.append(Component.text(" (%s)".formatted(durationTimeLeft == 0 ? Tick.INFINITY_CHAR : Tick.format(durationTimeLeft)), resourceColor));
        }
        // Otherwise, call `getComponent()`
        else {
            final Component component = this.getComponent(player, player.getUltimateResource());
            
            // If ultimate is on cooldown, gray-out the component and append the cooldown time left
            if (player.hasCooldown(this)) {
                final int cooldownTimeLeft = player.getCooldownTimeLeft(this);
                
                builder.append(Components.applyStyle(component, STYLE_ULTIMATE_ON_COOLDOWN));
                builder.append(Component.text(" (%s)".formatted(Tick.format(cooldownTimeLeft)), Colors.TICK));
            }
            // Otherwise append normally
            else {
                builder.append(component);
            }
        }
        
        return builder.build();
    }
    
    protected final @NotNull Component createUsageComponent(@NotNull Component component) {
        return Component.empty()
                        .append(ultimateResourceType.getPrefixStyled())
                        .appendSpace()
                        .append(component)
                        .appendSpace()
                        .append(Component.text("Press ", Colors.DEFAULT_COLOR))
                        .append(Component.keybind(Keybind.SWAP_OFFHAND, Colors.GOLD, TextDecoration.BOLD))
                        .append(Component.text(" to use it!", Colors.DEFAULT_COLOR));
    }
    
    /**
     * Executes this {@link TalentUltimate} talent for the given {@link HariantPlayer}.
     *
     * @param player           - The player for whom to execute the ultimate.
     * @param context          - The talent context for target retrieval, if needed.
     * @param consumedResource - The amount of ultimate resource consumed.
     * @return an {@link Executable} to run and await the {@link Promise} to complete in order to end the ultimate the start the cooldown.
     * @see Executable
     * @see ExecutorService
     */
    @NotNull
    public abstract Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, final double consumedResource);
    
    @NotNull
    @Override
    public abstract TalentTarget target(@NotNull HariantPlayer player);
    
    @NotNull
    @Override
    public final Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        // Check for cost
        final double ultimateResource = player.getUltimateResource();
        final double resourceCost = this.getMinimumCost();
        
        if (player.isUsingUltimate()) {
            return Response.error("You are already using ultimate!");
        }
        
        if (ultimateResource < resourceCost) {
            return Response.error("Your ultimate is not ready!");
        }
        
        // Decrement energy
        final double resourceConsumption = this.getConsumption();
        
        player.decrementUltimateResource(resourceConsumption);
        player.setUsingUltimate(true);
        
        final Executable executable = execute(player, context, ultimateResource);
        
        // Ultimates always delegate to player and can only be cancelled on death
        player.delegate(executable, DelegateType.PERSISTENT);
        
        executable.execute().then(() -> {
            player.setUsingUltimate(false);
            player.setCooldown(this);
        });
        
        this.broadcast(player);
        
        // Call event
        new HariantTalentUltimateEvent(player, this, resourceConsumption).callEvent();
        
        return Response.ok();
    }
    
    @NotNull
    @Override
    public String getTalentClassName() {
        return "Ultimate Talent";
    }
    
    @Override
    protected void initAttributeFields(@NotNull List<? super DisplayFieldInstance> attributeFields) {
        super.initAttributeFields(attributeFields);
        
        attributeFields.add(new DisplayFieldInstance(
                ultimateResourceType.getName().append(Component.text(" Cost")),
                Component.text("%.0f".formatted(cost))
        ));
    }
    
    public void broadcast(@NotNull HariantPlayer player) {
        Hariant.getPlayers().forEach(other -> {
            final EnumTeam otherTeam = other.getPlayerTeam();
            
            final Component youOrPlayerName = other.equals(player)
                                              ? Component.text("You", Colors.DEFAULT_COLOR)
                                              : player.getName().color(otherTeam.getStyle().color());
            
            
            other.sendMessage(
                    Component.empty()
                             .append(HariantConstants.GENERIC_ULTIMATE_PREFIX.color(Colors.ULTIMATE_RESOURCE_ENERGY))
                             .appendSpace()
                             .append(youOrPlayerName)
                             .append(Component.text(" used ", Colors.DEFAULT_COLOR))
                             .append(this.getName().color(Colors.GOLD))
                             .append(Component.text("!", Colors.DEFAULT_COLOR))
            );
            
            soundFx.play(other);
        });
    }
    
}
